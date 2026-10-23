package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 恶魔交易附魔 - 胸甲 Ⅲ 级（宝藏）。
 */
public final class DemonicPactEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":demonic_pact";

	/** 互斥组：恶魔交易 ↔ 掉落不死亡 */

	/** 冷却（tick） */
	private static final long COOLDOWN_TICKS = 1200L;

	/** 玩家 UUID → 上次触发的 gameTime */
	private static final ConcurrentHashMap<UUID, Long> LAST_TRIGGER = new ConcurrentHashMap<>();

	private DemonicPactEnchantment() {
	}


	/**
	 * 尝试触发恶魔交易。
	 *
	 * @return true 表示已免死（Mixin 应取消死亡）
	 */
	public static boolean trySave(ServerPlayer player, DamageSource source) {
		ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
		int level = DeathSaveSupport.getLevel(chestplate, "demonic_pact");
		if (level <= 0) {
			return false;
		}
		// 虚空 / /kill 等绕过无敌的伤害不救（与不死图腾一致）
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}

		long now = player.level().getGameTime();
		Long last = LAST_TRIGGER.get(player.getUUID());
		if (last != null && now - last < COOLDOWN_TICKS) {
			return false;
		}

		int cost = 64 - 8 * (level - 1);
		if (countEmeralds(player) < cost) {
			return false;
		}
		consumeEmeralds(player, cost);

		int darknessTicks = (20 - 4 * (level - 1)) * 20;
		DeathSaveSupport.performResurrection(player, darknessTicks);
		LAST_TRIGGER.put(player.getUUID(), now);
		return true;
	}

	private static int countEmeralds(ServerPlayer player) {
		int count = 0;
		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			if (!stack.isEmpty() && stack.is(Items.EMERALD)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	private static void consumeEmeralds(ServerPlayer player, int amount) {
		int remaining = amount;
		var items = player.getInventory().getNonEquipmentItems();
		for (int i = 0; i < items.size() && remaining > 0; i++) {
			ItemStack stack = items.get(i);
			if (stack.isEmpty() || !stack.is(Items.EMERALD)) {
				continue;
			}
			int take = Math.min(remaining, stack.getCount());
			stack.shrink(take);
			remaining -= take;
		}
	}
}
