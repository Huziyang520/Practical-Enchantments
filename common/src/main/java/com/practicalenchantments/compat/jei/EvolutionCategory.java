// JEI 分类：进化附魔台的投入、两种产物与进度动画。

package com.practicalenchantments.compat.jei;

import com.huziyang520.merlinlib.api.EvolutionRule;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.PeContent;
import java.util.Locale;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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
	private static final int SLOT_Y = 2;
	private static final int ARROW_X = 46;
	private static final int ARROW_Y = 3;
	private static final int OUTPUT_X = 68;
	private static final int TRANSMUTE_X = 90;
	private final IGuiHelper guiHelper;
	private final IDrawable tableIcon;

	public EvolutionCategory(IGuiHelper guiHelper) {
		super(TYPE, Component.translatable("practical_enchantments.jei.evolution"),
				guiHelper.createDrawableItemStack(new ItemStack(PeContent.item(PeContent.EVOLUTION_TABLE))), 108, 40);
		this.guiHelper = guiHelper;
		this.tableIcon = guiHelper.createDrawableItemStack(new ItemStack(PeContent.item(PeContent.EVOLUTION_TABLE)));
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, EvolutionRule rule, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, SLOT_Y)
				.addItemStack(enchantedBook(rule.from()));
		builder.addSlot(RecipeIngredientRole.INPUT, 23, SLOT_Y)
				.addItemStack(new ItemStack(Items.LAPIS_LAZULI, Math.max(1, rule.lapisCost())));
		builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
				.addItemStack(enchantedBook(rule.to()))
				.addRichTooltipCallback((view, tooltip) -> tooltip.add(chance("evolve", rule.evolveChance())));
		builder.addSlot(RecipeIngredientRole.OUTPUT, TRANSMUTE_X, SLOT_Y)
				.addItemStack(new ItemStack(Items.ENCHANTED_BOOK))
				.addRichTooltipCallback((view, tooltip) -> {
					tooltip.add(Component.translatable("practical_enchantments.jei.evolution.transmuted"));
					tooltip.add(chance("transmute", rule.transmuteChance()));
					tooltip.add(chance("fail", rule.failureChance()));
				});
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, EvolutionRule rule, IFocusGroup focuses) {
		builder.addDrawableWidget(this.guiHelper.createAnimatedRecipeArrow(rule.ticks()))
				.setPosition(ARROW_X, ARROW_Y)
				.setTooltip(Component.translatable("practical_enchantments.jei.evolution.time",
						String.format(Locale.ROOT, "%.1f", rule.ticks() / 20.0F)));
		builder.addDrawable(this.tableIcon, 48, 21);
	}

	private static Component chance(String part, float chance) {
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
