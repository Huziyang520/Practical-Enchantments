// Fabric 客户端装配：屏幕注册走原版私有的 MenuScreens#register，方块实体渲染器走 Fabric API。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.client.BookBagScreen;
import com.practicalenchantments.client.EvolutionTableRenderer;
import com.practicalenchantments.client.EvolutionTableScreen;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import com.practicalenchantments.fabric.PeFabricMenus;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class PeFabricScreens {
	private PeFabricScreens() {
	}

	public static void install() {
		if (PeFabricMenus.evolutionTable() != null) {
			registerScreen(PeFabricMenus.evolutionTable(),
					(menu, inventory, title) -> new EvolutionTableScreen(menu, inventory, title));
		}
		if (PeFabricMenus.bookBag() != null) {
			registerScreen(PeFabricMenus.bookBag(),
					(menu, inventory, title) -> new BookBagScreen(menu, inventory, title));
		}
		if (EvolutionTableBlockEntity.TYPE != null) {
			BlockEntityRendererRegistry.register(EvolutionTableBlockEntity.TYPE, EvolutionTableRenderer::new);
		}
	}

	@SuppressWarnings("unchecked")
	private static <M extends AbstractContainerMenu> void registerScreen(MenuType<M> type, ScreenFactory<M> factory) {
		try {
			Class<?> constructorType = Class.forName("net.minecraft.client.gui.screens.MenuScreens$ScreenConstructor");
			Method register = MenuScreens.class.getDeclaredMethod("register", MenuType.class, constructorType);
			register.setAccessible(true);
			Object constructor = Proxy.newProxyInstance(constructorType.getClassLoader(),
					new Class<?>[] {constructorType}, (proxy, method, args) -> switch (method.getName()) {
						case "create" -> factory.create((M) args[0], (Inventory) args[1], (Component) args[2]);
						case "toString" -> "practical_enchantments screen";
						case "hashCode" -> System.identityHashCode(proxy);
						case "equals" -> proxy == args[0];
						default -> null;
					});
			register.invoke(null, type, constructor);
		} catch (ReflectiveOperationException error) {
			throw new IllegalStateException("[PracticalEnchantments] could not register a Fabric menu screen", error);
		}
	}

	private interface ScreenFactory<M extends AbstractContainerMenu> {
		Screen create(M menu, Inventory inventory, Component title);
	}
}
