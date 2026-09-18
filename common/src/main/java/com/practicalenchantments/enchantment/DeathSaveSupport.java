package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 免死类附魔（恶魔交易 / 掉落不死亡）共用工具。
 *
 * <p>提供：① 不依赖注册器的 ItemStack 附魔等级查询（Mixin 里拿不到
 * HolderLookup，按附魔 ID 字符串比对）；② 原版不死图腾同款复活效果；
 * ③ 全身物品掉落工具。</p>
 */
public final class DeathSaveSupport {

	/** 图腾复活广播事件（见 {@link ServerPlayer#handleEntityEvent(byte)}） */
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
	 * <p>用 ID 字符串扫描 {@link ItemStack#getEnchantments()}，避免在 Mixin /
	 * 静态桥接里持有附魔 Holder（Holder 要在数据包加载后解析）。</p>
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
		for (var entry : stack.getEnchantments().entrySet()) {
			Holder<Enchantment> holder = entry.getKey();
			boolean match = holder.unwrapKey()
				.map(key -> {
					Identifier id = key.identifier();
					return (id.getNamespace() + ":" + id.getPath()).equals(fullId);
				})
				.orElse(false);
			if (match) {
				return entry.getIntValue();
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

		// 背包主区 + 快捷栏（非装备物品列表）
		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
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

		// 与原版死亡掉落一致：随机散射到脚下，而不是 Block.popResource 那样原地堆在方块中心。
		// 26.3 原版路径 = LivingEntity#createItemStackToDrop(stack, randomly=true, thrownFromHand=false)
		// + Level#addFreshEntity，这里照抄以获得相同的散布手感。
		for (ItemStack drop : drops) {
			ItemEntity itemEntity = player.createItemStackToDrop(drop, true, false);
			if (itemEntity != null) {
				player.level().addFreshEntity(itemEntity);
			}
		}
	}
}
