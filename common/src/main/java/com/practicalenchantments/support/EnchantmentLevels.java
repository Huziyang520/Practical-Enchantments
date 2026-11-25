// 附魔物品的等级查询，供 PE 内部多个附魔共用。

package com.practicalenchantments.support;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchantmentLevels {
	private EnchantmentLevels() {
	}

	public static int of(ItemStack stack, String path) {
		return ofFull(stack, PracticalEnchantments.MOD_ID + ":" + path);
	}

	public static int ofFull(ItemStack stack, String fullId) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		for (var entry : stack.getEnchantments().entrySet()) {
			Holder<Enchantment> holder = entry.getKey();
			boolean match = holder.unwrapKey()
					.map(key -> {
						Identifier id = key.identifier();
						return (id.getNamespace() + ":" + id.getPath()).equals(fullId);
					})
					.orElse(false);
			if (match) {
				return entry.getIntValue();
			}
		}
		return 0;
	}
}
