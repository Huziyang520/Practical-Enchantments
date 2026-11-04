// 1.20.1 的箭不记武器，射出期间用这里暂存当前武器。

package com.practicalenchantments.support;

import net.minecraft.world.item.ItemStack;

public final class ShotContext {
	private static ItemStack weapon = ItemStack.EMPTY;

	private ShotContext() {
	}

	public static void begin(ItemStack stack) {
		weapon = stack == null ? ItemStack.EMPTY : stack;
	}

	public static ItemStack weapon() {
		return weapon;
	}

	public static void end() {
		weapon = ItemStack.EMPTY;
	}
}
