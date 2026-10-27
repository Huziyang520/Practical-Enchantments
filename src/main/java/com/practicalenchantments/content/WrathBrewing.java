// 癫狂药水的酿造：1.20.1 没有数据包酿造，走 Forge 的酿造注册表。

package com.practicalenchantments.content;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public final class WrathBrewing {
	private WrathBrewing() {
	}

	public static void register() {
		Potion wrath = ForgeRegistries.POTIONS.getValue(WrathContent.POTION_ID);
		Potion longWrath = ForgeRegistries.POTIONS.getValue(WrathContent.LONG_POTION_ID);
		if (wrath == null || longWrath == null) {
			return;
		}
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(potion(Potions.AWKWARD)),
				Ingredient.of(Items.MAGMA_BLOCK),
				potion(wrath));
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(potion(wrath)),
				Ingredient.of(Items.REDSTONE),
				potion(longWrath));
	}

	private static ItemStack potion(Potion potion) {
		return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
	}
}
