// 重击：始终暴击；命中后攻击冷却缩短一成，未命中拉长六成；每次攻击额外消耗一次耐久。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class HeavyBlowEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":heavy_blow";
	private static final Map<UUID, Boolean> LAST_HIT = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> LAST_HIT_TICK = new ConcurrentHashMap<>();
	private static final float HIT_FACTOR = 0.9F;
	private static final float MISS_FACTOR = 1.6F;

	private HeavyBlowEnchantment() {
	}

	public static boolean has(Player player) {
		return levelOf(player.getMainHandItem()) > 0;
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
		player.getMainHandItem().hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(EquipmentSlot.MAINHAND));
	}

	public static float cooldownFactor(Player player) {
		Boolean hit = LAST_HIT.get(player.getUUID());
		return Boolean.TRUE.equals(hit) ? HIT_FACTOR : MISS_FACTOR;
	}

	private static int levelOf(ItemStack stack) {
		ResourceLocation id = new ResourceLocation(ID);
		for (Map.Entry<Enchantment, Integer> entry
				: net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantments(stack).entrySet()) {
			if (id.equals(BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey()))) {
				return entry.getValue() == null ? 0 : entry.getValue();
			}
		}
		return 0;
	}
}
