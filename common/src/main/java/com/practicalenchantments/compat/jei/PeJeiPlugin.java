// JEI 插件：把进化附魔台的掷骰（概率、消耗与产物）展示成一条配方。

package com.practicalenchantments.compat.jei;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.PeContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class PeJeiPlugin implements IModPlugin {
	@Override
	public Identifier getPluginUid() {
		return Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "jei");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new EvolutionCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(EvolutionCategory.TYPE, MerlinApi.evolution().rules());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(EvolutionCategory.TYPE, PeContent.item(PeContent.EVOLUTION_TABLE));
	}
}
