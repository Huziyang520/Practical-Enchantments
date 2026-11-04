// 速矢附魔 - 弓/弩 Ⅲ 级：射出时按等级放大箭矢初速。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.support.EnchantIds;
import net.minecraft.world.item.ItemStack;

public final class SwiftArrowEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":swift_arrow";
	public static final double SPEED_PER_LEVEL = 0.25D;

	private SwiftArrowEnchantment() {
	}

	public static int level(ItemStack stack) {
		return EnchantIds.levelOf(stack, ID);
	}

	public static double speedFactor(ItemStack stack) {
		int level = level(stack);
		return level <= 0 ? 1.0D : 1.0D + SPEED_PER_LEVEL * level;
	}
}
