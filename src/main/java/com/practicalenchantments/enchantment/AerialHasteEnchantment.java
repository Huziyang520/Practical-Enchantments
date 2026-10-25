// Aerial haste enchantment.

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;

public final class AerialHasteEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":aerial_haste";
	public static volatile Enchantment HOLDER;

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		HOLDER = PracticalEnchantments.resolveEnchantment(ID);
	}
}
