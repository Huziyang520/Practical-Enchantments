// 菜单类型注册：MenuType 的构造器私有，Fabric 用官方 ExtendedMenuType 建。

package com.practicalenchantments.fabric;

import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.EvolutionTableMenu;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.PeMenus;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
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
		evolutionTable = new ExtendedMenuType<EvolutionTableMenu, Unit>(
				(id, inventory, data) -> new EvolutionTableMenu(id, inventory),
				StreamCodec.unit(Unit.INSTANCE));
		Registry.register(BuiltInRegistries.MENU, PeContent.EVOLUTION_TABLE, evolutionTable);
		PeMenus.setEvolutionTable(evolutionTable);
		bookBag = new ExtendedMenuType<BookBagMenu, Unit>(
				(id, inventory, data) -> new BookBagMenu(id, inventory),
				StreamCodec.unit(Unit.INSTANCE));
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
