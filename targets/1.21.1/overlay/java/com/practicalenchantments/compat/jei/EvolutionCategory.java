// JEI 分类：进化附魔台的投入、两种产物与进度动画。

package com.practicalenchantments.compat.jei;

import com.huziyang520.merlinlib.api.EvolutionRule;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.PeContent;
import java.util.List;
import java.util.Locale;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EvolutionCategory implements IRecipeCategory<EvolutionRule> {
	public static final RecipeType<EvolutionRule> TYPE = RecipeType.create(
			PracticalEnchantments.MOD_ID, "evolution", EvolutionRule.class);
	private static final int SLOT_Y = 2;
	private static final int ARROW_X = 46;
	private static final int ARROW_Y = 3;
	private static final int OUTPUT_X = 68;
	private static final int TRANSMUTE_X = 90;
	private static final int WIDTH = 108;
	private static final int HEIGHT = 40;
	private final IGuiHelper guiHelper;
	private final IDrawable icon;
	private final IDrawable tableIcon;
	private final java.util.Map<Integer, IDrawableAnimated> arrows = new java.util.HashMap<>();

	public EvolutionCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(PeContent.item(PeContent.EVOLUTION_TABLE)));
		this.tableIcon = guiHelper.createDrawableItemStack(new ItemStack(PeContent.item(PeContent.EVOLUTION_TABLE)));
	}

	@Override
	public RecipeType<EvolutionRule> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("practical_enchantments.jei.evolution");
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
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
	public void draw(EvolutionRule rule, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
			double mouseX, double mouseY) {
		arrowFor(rule).draw(guiGraphics, ARROW_X, ARROW_Y);
		this.tableIcon.draw(guiGraphics, 48, 21);
	}

	@Override
	public List<Component> getTooltipStrings(EvolutionRule rule, IRecipeSlotsView recipeSlotsView,
			double mouseX, double mouseY) {
		if (mouseX >= ARROW_X && mouseX <= ARROW_X + 24 && mouseY >= ARROW_Y && mouseY <= ARROW_Y + 17) {
			return List.of(Component.translatable("practical_enchantments.jei.evolution.time",
					String.format(Locale.ROOT, "%.1f", rule.ticks() / 20.0F)));
		}
		return List.of();
	}

	// 每条规则的时长不同，动画箭头按时长缓存，避免每次重画都新建。
	private IDrawableAnimated arrowFor(EvolutionRule rule) {
		return this.arrows.computeIfAbsent(rule.ticks(),
				ticks -> this.guiHelper.createAnimatedRecipeArrow(ticks));
	}

	private static Component chance(String part, float chance) {
		return Component.translatable("practical_enchantments.jei.evolution." + part,
				String.valueOf(Math.round(chance * 100.0F)));
	}

	private static ItemStack enchantedBook(ResourceLocation enchantment) {
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
