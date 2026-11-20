// 菜单类型注册：Fabric 用普通 MenuType，打开方仍是 SimpleMenuProvider（私有构造器由 access widener 打开）。

package com.practicalenchantments.fabric;

import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.EvolutionTableMenu;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.PeMenus;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;

public final class PeFabricMenus {
	@Nullable
	private static MenuType<EvolutionTableMenu> evolutionTable;
	@Nullable
	private static MenuType<BookBagMenu> bookBag;

	private PeFabricMenus() {
	}

	public static void install() {
		if (evolutionTable != null) {
			return;
		}
		evolutionTable = new MenuType<>(EvolutionTableMenu::new, FeatureFlags.VANILLA_SET);
		Registry.register(BuiltInRegistries.MENU, PeContent.EVOLUTION_TABLE, evolutionTable);
		PeMenus.setEvolutionTable(evolutionTable);
		bookBag = new MenuType<>(BookBagMenu::new, FeatureFlags.VANILLA_SET);
		Registry.register(BuiltInRegistries.MENU, PeContent.BOOK_BAG_LEATHER, bookBag);
		PeMenus.setBookBag(bookBag);
	}

	@Nullable
	public static MenuType<EvolutionTableMenu> evolutionTable() {
		return evolutionTable;
	}

	@Nullable
	public static MenuType<BookBagMenu> bookBag() {
		return bookBag;
	}
}
