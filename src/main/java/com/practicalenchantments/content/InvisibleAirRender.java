// 判定手持物品是否带风王结界，客户端渲染隐藏与命中结算共用同一口径。

package com.practicalenchantments.content;

import com.practicalenchantments.enchantment.InvisibleAirEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class InvisibleAirRender {
	private static final ResourceLocation ID = new ResourceLocation(InvisibleAirEnchantment.ID);

	private InvisibleAirRender() {
	}

	public static boolean isHidden(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		for (Enchantment enchantment : EnchantmentHelper.getEnchantments(stack).keySet()) {
			if (ID.equals(BuiltInRegistries.ENCHANTMENT.getKey(enchantment))) {
				return true;
			}
		}
		return false;
	}
}
