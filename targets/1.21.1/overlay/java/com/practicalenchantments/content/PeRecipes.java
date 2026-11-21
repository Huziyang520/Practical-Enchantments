// 本模组的配方序列化器。书袋升级是代码配方，没有 json 文件。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.platform.Services;
import com.mojang.serialization.MapCodec;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class PeRecipes {
	private static final ResourceLocation BOOK_BAG_UPGRADE =
			ResourceLocation.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "book_bag_upgrade");
	private static RecipeSerializer<BookBagUpgradeRecipe> bookBagUpgrade;

	private PeRecipes() {
	}

	public static void register() {
		if (bookBagUpgrade != null) {
			return;
		}
		bookBagUpgrade = new RecipeSerializer<>() {
			@Override
			public MapCodec<BookBagUpgradeRecipe> codec() {
				return MapCodec.unit(BookBagUpgradeRecipe.INSTANCE);
			}

			@Override
			public StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, BookBagUpgradeRecipe> streamCodec() {
				return StreamCodec.unit(BookBagUpgradeRecipe.INSTANCE);
			}
		};
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
