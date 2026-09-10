package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentEffectsBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 中国人附魔 - 胸甲 I 级
 * 效果：装备后解锁创造飞行；空中受击耐久消耗 ×3；拿下胸甲立即恢复正常
 */
public final class ChineseEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":chinese";

	/** 本附魔的 Holder（注册回调时解析） */
	private static Holder<Enchantment> HOLDER;

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("中国人")
			.supportedItems("#minecraft:enchantable/chest_armor")
			.weight(1) // 宝藏附魔，不可通过附魔台获得（weight 最小必须为 1）
			.maxLevel(1)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(0)
			.slots("chest")
			.effects(EnchantmentEffectsBuilder.create().build()));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);

		// 飞行解锁/撤销：使用全局 LIVING_ENTITY_TICK，拿下胸甲时（即便身上无其他附魔装备）
		// 也能可靠地撤销飞行，避免"永久飞行"。
		EnchantLibEvents.enableLivingEntityTick();
		EnchantLibEvents.LIVING_ENTITY_TICK.register(ChineseEnchantment::onLivingTick);

		// 空中受击耐久 ×3
		registrar.register(HOLDER, BuiltInEvents.POST_HURT, ChineseEnchantment::onPostHurt);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;

		boolean hasChinese = hasChinese(player);
		var abilities = player.getAbilities();

		if (hasChinese) {
			// 解锁创造飞行（仅开放飞行权限）
			if (!abilities.mayfly) {
				abilities.mayfly = true;
				player.onUpdateAbilities();
			}
		} else if (abilities.mayfly) {
			// 胸甲被拿下或耐久归零：撤销飞行权限，恢复正常
			abilities.mayfly = false;
			player.onUpdateAbilities();
		}
	}

	private static void onPostHurt(BuiltInEvents.PostHurtEvent event, EnchantmentContext ctx) {
		if (!(event.target() instanceof ServerPlayer player)) return;

		// 仅对空中受击生效
		if (player.onGround()) return;

		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty()) return;

		// 空中受击：额外造成 2 倍正常单件护甲耐久消耗，合计约 3 倍
		int extra = 2 * Math.max(1, (int) (event.amount() / 4));
		chestItem.hurtAndBreak(extra, player, EquipmentSlot.CHEST);
	}

	/** 判断玩家胸甲上是否有中国人附魔 */
	private static boolean hasChinese(ServerPlayer player) {
		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty() || HOLDER == null) return false;
		return chestItem.getEnchantments().getLevel(HOLDER) > 0;
	}
}
