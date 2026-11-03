// 判定手持物品是否带风王结界，客户端渲染隐藏共用同一口径。

package com.practicalenchantments.content;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class InvisibleAirRender {
	private static final String ID = "practical_enchantments:invisible_air";

	private InvisibleAirRender() {
	}

	public static boolean isHidden(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		for (Holder<Enchantment> holder : stack.getEnchantments().keySet()) {
			Optional<ResourceKey<Enchantment>> key = holder.unwrapKey();
			if (key.isPresent() && ID.equals(key.get().location().toString())) {
				return true;
			}
		}
		return false;
	}
}
