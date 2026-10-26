// 癫狂药水的酿造混合：1.21.1 用代码注册，没有数据包配方。

package com.practicalenchantments.content;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

public final class WrathBrewing {
	private WrathBrewing() {
	}

	public static void register(PotionBrewing.Builder builder, HolderLookup.Provider registries) {
		Holder<Potion> wrath = potion(registries, WrathContent.POTION_ID);
		Holder<Potion> longWrath = potion(registries, WrathContent.LONG_POTION_ID);
		if (wrath == null || longWrath == null) {
			return;
		}
		builder.addMix(Potions.AWKWARD, Items.MAGMA_BLOCK, wrath);
		builder.addMix(wrath, Items.REDSTONE, longWrath);
		builder.addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
		builder.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
	}

	private static Holder<Potion> potion(HolderLookup.Provider registries, ResourceLocation id) {
		var lookup = registries.lookup(Registries.POTION);
		if (lookup.isEmpty()) {
			return null;
		}
		return lookup.get().get(ResourceKey.create(Registries.POTION, id)).orElse(null);
	}
}
