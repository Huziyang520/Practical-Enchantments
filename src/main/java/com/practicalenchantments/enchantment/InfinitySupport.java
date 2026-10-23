package com.practicalenchantments.enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * 读「无限」在武器上的等级。
 */
public final class InfinitySupport {

	private InfinitySupport() {
	}

	/**
	 * @param stack 武器
	 * @return 无限在该武器上的等级，没有则为 0
	 */
	public static int level(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack);
	}
}
