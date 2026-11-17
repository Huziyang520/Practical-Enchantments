// Fabric 客户端装配：屏幕注册走 MenuScreens 的 invoker，方块实体渲染器走 Fabric API。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.client.BookBagScreen;
import com.practicalenchantments.client.EvolutionTableRenderer;
import com.practicalenchantments.client.EvolutionTableScreen;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import com.practicalenchantments.fabric.PeFabricMenus;
import com.practicalenchantments.fabric.client.mixin.MenuScreensInvoker;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public final class PeFabricScreens {
	private PeFabricScreens() {
	}

	public static void install() {
		if (PeFabricMenus.evolutionTable() != null) {
			MenuScreensInvoker.pe$register(PeFabricMenus.evolutionTable(), EvolutionTableScreen::new);
		}
		if (PeFabricMenus.bookBag() != null) {
			MenuScreensInvoker.pe$register(PeFabricMenus.bookBag(), BookBagScreen::new);
		}
		if (EvolutionTableBlockEntity.TYPE != null) {
			BlockEntityRendererRegistry.register(EvolutionTableBlockEntity.TYPE, EvolutionTableRenderer::new);
		}
	}
}
