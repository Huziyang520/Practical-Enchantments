// JEI 分类：进化附魔台的投入、两种产物与三档概率。

package com.practicalenchantments.compat.jei;

import com.huziyang520.merlinlib.api.EvolutionRule;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.PeContent;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EvolutionCategory extends AbstractRecipeCategory<EvolutionRule> {
	public static final IRecipeType<EvolutionRule> TYPE = IRecipeType.create(
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "evolution"), EvolutionRule.class);
	private static final int SLOT_Y = 6;
	private static final int OUTPUT_X = 68;
	private static final int TRANSMUTE_X = 90;
	private final IDrawable arrow;

	public EvolutionCategory(IGuiHelper guiHelper) {
		super(TYPE, Component.translatable("practical_enchantments.jei.evolution"),
				guiHelper.createDrawableItemStack(new ItemStack(PeContent.item(PeContent.EVOLUTION_TABLE))), 110, 78);
		this.arrow = guiHelper.getRecipeArrow();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, EvolutionRule rule, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, SLOT_Y)
				.addItemStack(enchantedBook(rule.from()));
		builder.addSlot(RecipeIngredientRole.INPUT, 23, SLOT_Y)
				.addItemStack(new ItemStack(Items.LAPIS_LAZULI, Math.max(1, rule.lapisCost())));
		builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
				.addItemStack(enchantedBook(rule.to()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, TRANSMUTE_X, SLOT_Y)
				.addItemStack(new ItemStack(Items.ENCHANTED_BOOK))
				.addRichTooltipCallback((view, tooltip) ->
						tooltip.add(Component.translatable("practical_enchantments.jei.evolution.transmuted")));
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, EvolutionRule rule, IFocusGroup focuses) {
		builder.addDrawable(this.arrow, 44, SLOT_Y + 1);
		builder.addText(line("evolve", rule.evolveChance()), 1, 32);
		builder.addText(line("transmute", rule.transmuteChance()), 1, 44);
		builder.addText(line("fail", rule.failureChance()), 1, 56);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, EvolutionRule rule, IRecipeSlotsView slots,
			double mouseX, double mouseY) {
		tooltip.add(Component.translatable("practical_enchantments.jei.evolution.note"));
	}

	private static Component line(String part, float chance) {
		return Component.translatable("practical_enchantments.jei.evolution." + part,
				String.valueOf(Math.round(chance * 100.0F)));
	}

	private static ItemStack enchantedBook(Identifier enchantment) {
		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
		HolderLookup.Provider registries = registryAccess();
		if (registries == null) {
			return stack;
		}
		Holder<Enchantment> holder = registries.lookupOrThrow(Registries.ENCHANTMENT)
				.get(ResourceKey.create(Registries.ENCHANTMENT, enchantment)).orElse(null);
		if (holder == null) {
			return stack;
		}
		ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		mutable.set(holder, 1);
		stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
		return stack;
	}

	private static HolderLookup.Provider registryAccess() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.level == null ? null : minecraft.level.registryAccess();
	}
}
