// 连弩附魔 - 弩 Ⅴ 级：一次装填 1 + 等级 根同种箭矢（原版发射侧已支持免拉弦连发）。

package com.practicalenchantments.enchantment.weapon;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.support.EnchantIds;
import net.minecraft.world.item.ItemStack;

public final class RepeatingCrossbowEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":repeating_crossbow";

	private RepeatingCrossbowEnchantment() {
	}

	public static int level(ItemStack stack) {
		return EnchantIds.levelOf(stack, ID);
	}
}
