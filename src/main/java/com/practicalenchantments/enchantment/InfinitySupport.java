// 读「无限」在武器上的等级。

package com.practicalenchantments.enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class InfinitySupport {
	private InfinitySupport() {
	}

	public static int level(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack);
	}
}
