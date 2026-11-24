// 避雷：让穿戴者在雷雨天成为优先落雷点。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;

public final class LightningRodEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":lightning_rod";
	private static Holder<Enchantment> HOLDER;

	private LightningRodEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
	}

	public static boolean wears(ServerPlayer player) {
		return HOLDER != null && AmuletSupport.wornLevel(player, HOLDER) > 0;
	}
}
