// 重击：始终暴击；命中后攻击冷却缩短一成，未命中拉长六成；每次攻击额外消耗一次耐久。

package com.practicalenchantments.enchantment.weapon;

import com.practicalenchantments.PracticalEnchantments;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;

public final class HeavyBlowEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":heavy_blow";
	public static Holder<Enchantment> HOLDER;
	private static final Map<UUID, Boolean> LAST_HIT = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> LAST_HIT_TICK = new ConcurrentHashMap<>();
	private static final float HIT_FACTOR = 0.9F;
	private static final float MISS_FACTOR = 1.6F;

	private HeavyBlowEnchantment() {
	}

	public static void registerCallbacks(HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
	}

	public static boolean has(Player player) {
		Holder<Enchantment> holder = HOLDER;
		if (holder == null) {
			return false;
		}
		return player.getMainHandItem().getEnchantments().getLevel(holder) > 0;
	}

	public static void markSwing(Player player, boolean hit) {
		LAST_HIT.put(player.getUUID(), hit);
		if (!hit) {
			return;
		}
		Integer previous = LAST_HIT_TICK.put(player.getUUID(), player.tickCount);
		if (previous != null && previous == player.tickCount) {
			return;
		}
		player.getMainHandItem().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
	}

	public static float cooldownFactor(Player player) {
		Boolean hit = LAST_HIT.get(player.getUUID());
		return Boolean.TRUE.equals(hit) ? HIT_FACTOR : MISS_FACTOR;
	}
}
