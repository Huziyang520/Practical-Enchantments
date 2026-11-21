// 书袋升级配方：8 个同种材料围住一个书袋，产物继承原袋的全部内容。下界合金一档走锻造台。
// 1.20.1 的代码配方由数据包实例化，所以这里带 id 与分类而不是单例。

package com.practicalenchantments.content;

import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BookBagUpgradeRecipe extends CustomRecipe {
	private static final List<Item> MATERIALS =
			List.of(Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND);
	private static final List<ResourceLocation> TARGETS = List.of(
			PeContent.BOOK_BAG_IRON, PeContent.BOOK_BAG_GOLD, PeContent.BOOK_BAG_DIAMOND);
	private static final List<ResourceLocation> SOURCES = List.of(
			PeContent.BOOK_BAG_LEATHER, PeContent.BOOK_BAG_IRON, PeContent.BOOK_BAG_GOLD);

	public BookBagUpgradeRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(CraftingContainer input, Level level) {
		return this.find(input) != null;
	}

	@Override
	public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
		Match match = this.find(input);
		if (match == null) {
			return ItemStack.EMPTY;
		}
		ItemStack upgraded = new ItemStack(match.target());
		CompoundTag contents = match.bag().getTagElement(BookBagStorage.TAG_ITEMS);
		if (contents != null) {
			upgraded.getOrCreateTag().put(BookBagStorage.TAG_ITEMS, contents.copy());
		}
		copyTag(match.bag(), upgraded, ItemStack.TAG_ENCH);
		copyTag(match.bag(), upgraded, ItemStack.TAG_DISPLAY);
		return upgraded;
	}

	private static void copyTag(ItemStack source, ItemStack target, String key) {
		CompoundTag tag = source.getTag();
		if (tag == null || !tag.contains(key)) {
			return;
		}
		target.getOrCreateTag().put(key, tag.get(key).copy());
	}

	@Nullable
	private Match find(CraftingContainer input) {
		int size = input.getContainerSize();
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
			if (MATERIALS.get(index) == material && bagStack.is(PeContent.item(SOURCES.get(index)))) {
				return new Match(bagStack, PeContent.item(TARGETS.get(index)));
			}
		}
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 9;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PeRecipes.bookBagUpgrade();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	private record Match(ItemStack bag, Item target) {
	}

	public static final class Serializer implements RecipeSerializer<BookBagUpgradeRecipe> {
		@Override
		public BookBagUpgradeRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
			return new BookBagUpgradeRecipe(id, CraftingBookCategory.MISC);
		}

		@Override
		public BookBagUpgradeRecipe fromNetwork(ResourceLocation id, net.minecraft.network.FriendlyByteBuf buffer) {
			return new BookBagUpgradeRecipe(id, CraftingBookCategory.MISC);
		}

		@Override
		public void toNetwork(net.minecraft.network.FriendlyByteBuf buffer, BookBagUpgradeRecipe recipe) {
		}

	}
}
