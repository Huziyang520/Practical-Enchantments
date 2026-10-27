// 26.1-26.2 的癫狂药水酿造：这两版没有数据包酿造配方，只能用代码注册。

package com.practicalenchantments.content;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

public final class RangeWrathBrewing {
	private static final Identifier AWKWARD_ID = Identifier.withDefaultNamespace("awkward");

	private RangeWrathBrewing() {
	}

	public static void apply(PotionBrewing.Builder builder) {
		Holder<Potion> awkward = potion(AWKWARD_ID);
		Holder<Potion> wrath = potion(WrathContent.POTION_ID);
		Holder<Potion> longWrath = potion(WrathContent.LONG_POTION_ID);
		if (awkward == null || wrath == null || longWrath == null) {
			return;
		}
		builder.addMix(awkward, Items.MAGMA_BLOCK, wrath);
		builder.addMix(wrath, Items.REDSTONE, longWrath);
		builder.addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
		builder.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
	}

	private static Holder<Potion> potion(net.minecraft.resources.Identifier id) {
		return BuiltInRegistries.POTION.get(id).orElse(null);
	}
}
