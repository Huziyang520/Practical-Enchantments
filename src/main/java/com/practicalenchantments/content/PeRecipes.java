// 本模组的配方序列化器。书袋升级是代码配方，序列化器只负责读写空对象。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.platform.Services;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public final class PeRecipes {
	private static final ResourceLocation BOOK_BAG_UPGRADE =
			new ResourceLocation(PracticalEnchantments.MOD_ID, "book_bag_upgrade");
	@Nullable
	private static RecipeSerializer<BookBagUpgradeRecipe> bookBagUpgrade;

	private PeRecipes() {
	}

	public static void register() {
		if (bookBagUpgrade != null) {
			return;
		}
		bookBagUpgrade = new BookBagUpgradeRecipe.Serializer();
		RecipeSerializer<BookBagUpgradeRecipe> serializer = bookBagUpgrade;
		Services.REGISTRATIONS.register(BuiltInRegistries.RECIPE_SERIALIZER, BOOK_BAG_UPGRADE, () -> serializer);
	}

	public static RecipeSerializer<BookBagUpgradeRecipe> bookBagUpgrade() {
		if (bookBagUpgrade == null) {
			throw new IllegalStateException("[PracticalEnchantments] the book bag recipe serializer is not registered");
		}
		return bookBagUpgrade;
	}
}
