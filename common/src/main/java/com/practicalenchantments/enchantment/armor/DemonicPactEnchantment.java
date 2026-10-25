// 恶魔交易附魔 - 胸甲 Ⅲ 级（宝藏）。

package com.practicalenchantments.enchantment.armor;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.support.DeathSaveSupport;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class DemonicPactEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":demonic_pact";
	private static final long COOLDOWN_TICKS = 1200L;
	private static final ConcurrentHashMap<UUID, Long> LAST_TRIGGER = new ConcurrentHashMap<>();

	private DemonicPactEnchantment() {
	}

	public static boolean trySave(ServerPlayer player, DamageSource source) {
		ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
		int level = DeathSaveSupport.getLevel(chestplate, "demonic_pact");
		if (level <= 0) {
			return false;
		}

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
