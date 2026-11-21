// 菜单类型的持有者：加载器在初始化时把注册结果写进来，公共代码只读。

package com.practicalenchantments.content;

import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public final class PeMenus {
	@Nullable
	private static MenuType<EvolutionTableMenu> evolutionTable;
	@Nullable
	private static MenuType<BookBagMenu> bookBag;

	private PeMenus() {
	}

	public static void setEvolutionTable(MenuType<EvolutionTableMenu> type) {
		evolutionTable = type;
	}

	@Nullable
	public static MenuType<EvolutionTableMenu> evolutionTable() {
		return evolutionTable;
	}

	public static void setBookBag(MenuType<BookBagMenu> type) {
		bookBag = type;
	}

	@Nullable
	public static MenuType<BookBagMenu> bookBag() {
		return bookBag;
	}
}
