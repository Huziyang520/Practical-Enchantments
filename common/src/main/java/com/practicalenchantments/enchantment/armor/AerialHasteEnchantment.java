// 浮空速掘附魔 消除空中挖掘速度惩罚

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;

public final class AerialHasteEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":aerial_haste";
	public static volatile Holder<Enchantment> HOLDER;

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
	}
}
