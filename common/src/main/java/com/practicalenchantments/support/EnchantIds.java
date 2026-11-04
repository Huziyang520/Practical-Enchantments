// 按附魔 id 读物品上的等级，不依赖 Holder 查找。

package com.practicalenchantments.support;

import net.minecraft.world.item.ItemStack;

public final class EnchantIds {
	private EnchantIds() {
	}

	public static int levelOf(ItemStack stack, String id) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		for (var entry : stack.getEnchantments().entrySet()) {
			var key = entry.getKey().unwrapKey();
			if (key.isPresent() && key.get().identifier().toString().equals(id)) {
				return entry.getIntValue();
			}
		}
		return 0;
	}
}
