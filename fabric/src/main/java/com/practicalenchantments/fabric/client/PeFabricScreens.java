// Fabric 侧容器界面装配。26.3 的 MenuType 构造与 MenuScreens 注册都是私有的，
// 这里用一次反射拿到 MenuType，再由 MenuScreensMixin 接管屏幕创建。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.client.EvolutionTableRenderer;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import com.practicalenchantments.content.EvolutionTableMenu;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.PeMenus;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;

public final class PeFabricScreens {
	@Nullable
	private static MenuType<EvolutionTableMenu> evolutionTable;
	@Nullable
	private static MenuType<BookBagMenu> bookBag;

	private PeFabricScreens() {
	}

	public static void install() {
		evolutionTable = createMenuType(EvolutionTableMenu::new);
		Registry.register(BuiltInRegistries.MENU, PeContent.EVOLUTION_TABLE, evolutionTable);
		PeMenus.setEvolutionTable(evolutionTable);
		bookBag = createMenuType(BookBagMenu::new);
		Registry.register(BuiltInRegistries.MENU, PeContent.BOOK_BAG_LEATHER, bookBag);
		PeMenus.setBookBag(bookBag);
		if (EvolutionTableBlockEntity.TYPE != null) {
			BlockEntityRendererRegistry.register(EvolutionTableBlockEntity.TYPE, EvolutionTableRenderer::new);
		}
	}

	public static boolean isEvolutionTable(@Nullable MenuType<?> type) {
		return type != null && type == evolutionTable;
	}

	public static boolean isBookBag(@Nullable MenuType<?> type) {
		return type != null && type == bookBag;
	}

	@SuppressWarnings("unchecked")
	private static <T extends AbstractContainerMenu> MenuType<T> createMenuType(
			BiFunction<Integer, Inventory, T> factory) {
		try {
			Class<?> supplierType = null;
			for (Class<?> nested : MenuType.class.getDeclaredClasses()) {
				if (nested.getSimpleName().equals("MenuSupplier")) {
					supplierType = nested;
					break;
				}
			}
			if (supplierType == null) {
				throw new IllegalStateException("MenuType.MenuSupplier is missing");
			}
			Constructor<MenuType> constructor = MenuType.class.getDeclaredConstructor(supplierType, FeatureFlagSet.class);
			constructor.setAccessible(true);
			Object supplier = Proxy.newProxyInstance(supplierType.getClassLoader(), new Class<?>[] {supplierType},
					(proxy, method, args) -> switch (method.getName()) {
						case "create" -> factory.apply((Integer) args[0], (Inventory) args[1]);
						case "toString" -> "practical_enchantments menu";
						case "hashCode" -> System.identityHashCode(proxy);
						case "equals" -> proxy == args[0];
						default -> null;
					});
			return (MenuType<T>) constructor.newInstance(supplier, FeatureFlags.VANILLA_SET);
		} catch (ReflectiveOperationException error) {
			throw new IllegalStateException("[PracticalEnchantments] could not build a Fabric menu type", error);
		}
	}
}
