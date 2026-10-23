package com.practicalenchantments.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * 按 id 读「无限」在物品上的等级。
 */
public final class InfinitySupport {

	/** 原版无限的 id。 */
	private static final Identifier INFINITY = Identifier.withDefaultNamespace("infinity");

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
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments == null || enchantments.isEmpty()) {
			return 0;
		}
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.unwrapKey().map(key -> key.identifier().equals(INFINITY)).orElse(false)) {
				return enchantments.getLevel(holder);
			}
		}
		return 0;
	}
}
