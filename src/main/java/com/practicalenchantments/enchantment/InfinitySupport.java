// 按 id 读「无限」在物品上的等级。

package com.practicalenchantments.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class InfinitySupport {
	private static final ResourceLocation INFINITY = ResourceLocation.withDefaultNamespace("infinity");

	private InfinitySupport() {
	}

	public static int level(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments == null || enchantments.isEmpty()) {
			return 0;
		}
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.unwrapKey().map(key -> key.location().equals(INFINITY)).orElse(false)) {
				return enchantments.getLevel(holder);
			}
		}
		return 0;
	}
}
