package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 免死类附魔（恶魔交易 / 掉落不死亡）共用工具。
 *
 * <p>提供：① 不依赖注册器的 ItemStack 附魔等级查询（Mixin 里拿不到
 * 附魔对象，按附魔 ID 字符串比对）；② 原版不死图腾同款复活效果；
 * ③ 全身物品掉落工具。</p>
 *
 * <h2>1.20.1 移植：三处被版本逼着改的地方</h2>
 *
 * <p>方法名与签名（{@link #getLevel}、{@link #getLevelFull}、{@link #findArmorWith}、
 * {@link #performResurrection}、{@link #dropEverything}）全部与 26.3 一致，方便另外那 22 个
 * {@code *Enchantment} 类直接接上。内部实现有三处必须改，逐条记下：</p>
 * <ol>
 *   <li><b>附魔按 ID 比对的方式。</b>26.3 遍历 {@code ItemStack#getEnchantments()}（数据组件，
 *       键是 {@code Holder<Enchantment>}），用 {@code holder.unwrapKey().map(key -> key.identifier())}
 *       取 id。1.20.1 的附魔存在 NBT 里，改用
 *       {@link EnchantmentHelper#getEnchantments(ItemStack)}，它返回
 *       {@code Map<Enchantment, Integer>}，再用
 *       {@link BuiltInRegistries#ENCHANTMENT}{@code .getKey(enchantment)} 反查
 *       {@link ResourceLocation}。级别语义完全一致：{@code EnchantmentHelper.getEnchantments}
 *       对附魔书走 {@code EnchantedBookItem.getEnchantments}，对普通物品走
 *       {@code ItemStack.getEnchantmentTags}（已用 javap -c 确认其分支），与 26.3 数据组件路径
 *       同样兼顾附魔书。</li>
 *   <li><b>背包"非装备物品"的取法。</b>26.3 用
 *       {@code player.getInventory().getNonEquipmentItems()}；1.20.1 的 {@code Inventory} 没有这个方法，
 *       主背包 + 快捷栏就是公开字段 {@code inventory.items}
 *       （{@code NonNullList<ItemStack>}，已用 javap 确认），护甲与副手另有 {@code armor} /
 *       {@code offhand} 两个表——下面照旧用 {@code getItemBySlot} / {@code setItemSlot} 走槽位，
 *       免得直接依赖内部分区。</li>
 *   <li><b>掉落的落地方式。</b>26.3 用
 *       {@code player.createItemStackToDrop(stack, true, false)} 造实体、再自己
 *       {@code level().addFreshEntity(...)}。1.20.1 没有 {@code createItemStackToDrop}，等价物是
 *       {@code Player#drop(ItemStack, boolean, boolean)}。查 {@code javap -c} 确认了两件事：
 *       原版死亡掉落走的就是 {@code Inventory#dropAll} → 三参 {@code Player.drop(stack, true, false)}；
 *       而 {@code ServerPlayer} 覆写了这个三参方法，它在造完实体后负责
 *       {@code captureDrops() ? add : level().addFreshEntity(...)}。所以这里直接调三参
 *       {@code drop}，落点、散布与拾取保护都与原版死亡掉落逐字相同——比 26.3 那版更贴原版，
 *       因为 26.3 是手工复刻的。</li>
 * </ol>
 */
public final class DeathSaveSupport {

	/** 图腾复活广播事件（见 {@code ServerPlayer#handleEntityEvent(byte)}） */
	private static final byte TOTEM_ANIMATION_EVENT = 35;

	/** 装备槽（头盔/胸甲/护腿/靴子） */
	public static final EquipmentSlot[] ARMOR_SLOTS = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	/** 死亡时需要处理的全部装备槽（四护甲 + 副手） */
	private static final EquipmentSlot[] EQUIPMENT_SLOTS = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
	};

	private DeathSaveSupport() {
	}

	/**
	 * 读取物品上指定 practical 附魔的等级（无则 0）。
	 *
	 * <p>用 ID 字符串扫描物品的附魔，避免在 Mixin / 静态桥接里持有附魔对象
	 * （26.3 要持有的是 Holder，1.20.1 是 Enchantment，但"按 id 而不是按对象比对"的思路一致）。</p>
	 *
	 * @param stack 物品
	 * @param path  附魔路径名（如 "demonic_pact"）
	 * @return 附魔等级，0 表示未附魔
	 */
	public static int getLevel(ItemStack stack, String path) {
		return getLevelFull(stack, PracticalEnchantments.MOD_ID + ":" + path);
	}

	/**
	 * 读取物品上任意附魔（含原版）的等级，按完整 ID 比对。
	 *
	 * @param stack  物品
	 * @param fullId 完整附魔 ID（如 "minecraft:loyalty"）
	 * @return 等级，0 表示未附魔
	 */
	public static int getLevelFull(ItemStack stack, String fullId) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
			ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey());
			if (id != null && (id.getNamespace() + ":" + id.getPath()).equals(fullId)) {
				return entry.getValue();
			}
		}
		return 0;
	}

	/** 找到第一个携带指定附魔的护甲槽（头/胸/腿/靴顺序），无则 null。 */
	public static EquipmentSlot findArmorWith(ServerPlayer player, String path) {
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			if (getLevel(player.getItemBySlot(slot), path) > 0) {
				return slot;
			}
		}
		return null;
	}

	/**
	 * 执行不死图腾同款复活：1 点生命 + 生命恢复/伤害吸收/抗火 + 图腾动画，
	 * 再附加模组自己的黑暗效果。
	 *
	 * @param player        被复活的玩家
	 * @param darknessTicks 黑暗持续刻数（0 = 不加黑暗）
	 */
	public static void performResurrection(ServerPlayer player, int darknessTicks) {
		player.setHealth(1.0F);
		player.removeEffect(MobEffects.REGENERATION);
		player.removeEffect(MobEffects.ABSORPTION);
		player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
		player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
		player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
		if (darknessTicks > 0) {
			player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, darknessTicks, 0));
		}
		if (player.level() instanceof ServerLevel serverLevel) {
			serverLevel.broadcastEntityEvent(player, TOTEM_ANIMATION_EVENT);
		}
	}

	/**
	 * 把玩家背包（含快捷栏）+ 四件护甲 + 副手全部掉落脚边；
	 * 指定槽位的装备（被摧毁的那件）直接清空、不掉落。
	 *
	 * @param player        玩家
	 * @param destroyedSlot 要摧毁（不掉落）的装备槽，null 表示装备也全部掉落
	 */
	public static void dropEverything(ServerPlayer player, EquipmentSlot destroyedSlot) {
		List<ItemStack> drops = new ArrayList<>();

		// 背包主区 + 快捷栏。26.3 是 getNonEquipmentItems()，1.20.1 没有该方法：
		// Inventory.items 就是这个分区（护甲/副手另在 armor / offhand 两个表里）。
		for (ItemStack stack : player.getInventory().items) {
			if (!stack.isEmpty()) {
				drops.add(stack.copy());
				stack.setCount(0);
			}
		}

		// 护甲 + 副手
		for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
			ItemStack stack = player.getItemBySlot(slot);
			if (stack.isEmpty()) {
				continue;
			}
			if (slot == destroyedSlot) {
				// 被附魔献祭的装备：直接摧毁，不掉落
				player.setItemSlot(slot, ItemStack.EMPTY);
			} else {
				drops.add(stack.copy());
				player.setItemSlot(slot, ItemStack.EMPTY);
			}
		}

		// 与原版死亡掉落一致：随机散射到脚下。
		// 26.3 原版路径 = LivingEntity#createItemStackToDrop(stack, randomly=true, thrownFromHand=false)
		// + Level#addFreshEntity；1.20.1 该方法的等价物是 Player#drop(ItemStack, boolean, boolean)
		// （javap -c 确认：原版 Inventory#dropAll 调的就是这个三参重载，且 ServerPlayer 覆写它、
		//  在造完实体后负责 addFreshEntity / captureDrops）。
		for (ItemStack drop : drops) {
			player.drop(drop, true, false);
		}
	}
}
