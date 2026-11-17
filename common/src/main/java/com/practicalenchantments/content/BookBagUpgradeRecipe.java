// 书袋升级配方：8 个同种材料围住一个书袋，产物继承原袋的全部内容。

package com.practicalenchantments.content;

import com.practicalenchantments.PracticalEnchantments;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class BookBagUpgradeRecipe extends CustomRecipe {
	public static final BookBagUpgradeRecipe INSTANCE = new BookBagUpgradeRecipe();
	private static final List<Item> MATERIALS =
			List.of(Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.NETHERITE_INGOT);
	private static final List<Identifier> TARGETS = List.of(
			PeContent.BOOK_BAG_IRON, PeContent.BOOK_BAG_GOLD,
			PeContent.BOOK_BAG_DIAMOND, PeContent.BOOK_BAG_NETHERITE);
	private static final List<Identifier> SOURCES = List.of(
			PeContent.BOOK_BAG_LEATHER, PeContent.BOOK_BAG_IRON,
			PeContent.BOOK_BAG_GOLD, PeContent.BOOK_BAG_DIAMOND);

	public BookBagUpgradeRecipe() {
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		return this.find(input) != null;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		Match match = this.find(input);
		if (match == null) {
			return ItemStack.EMPTY;
		}
		ItemStack upgraded = new ItemStack(match.target());
		ItemContainerContents contents = match.bag().get(DataComponents.CONTAINER);
		if (contents != null) {
			upgraded.set(DataComponents.CONTAINER, contents);
		}
		return upgraded;
	}

	private @Nullable Match find(CraftingInput input) {
		int size = input.size();
		ItemStack bagStack = ItemStack.EMPTY;
		Item material = null;
		int materials = 0;
		for (int index = 0; index < size; index++) {
			ItemStack stack = input.getItem(index);
			if (stack.isEmpty()) {
				continue;
			}
			if (BookBagStorage.isBag(stack)) {
				if (!bagStack.isEmpty()) {
					return null;
				}
				bagStack = stack;
				continue;
			}
			if (material == null) {
				material = stack.getItem();
			} else if (material != stack.getItem()) {
				return null;
			}
			materials++;
		}
		if (bagStack.isEmpty() || material == null || materials != 8) {
			return null;
		}
		for (int index = 0; index < MATERIALS.size(); index++) {
			if (MATERIALS.get(index) == material
					&& bagStack.is(PeContent.item(SOURCES.get(index)))) {
				return new Match(bagStack, PeContent.item(TARGETS.get(index)));
			}
		}
		return null;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return PeRecipes.bookBagUpgrade();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	private record Match(ItemStack bag, Item target) {
	}
}
