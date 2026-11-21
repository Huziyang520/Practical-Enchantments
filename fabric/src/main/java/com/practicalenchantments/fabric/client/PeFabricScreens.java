// Fabric 客户端装配：容器界面由 MenuScreensMixin 接管，方块实体渲染器走 Fabric API。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.client.EvolutionTableRenderer;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public final class PeFabricScreens {
	private PeFabricScreens() {
	}

	public static void install() {
		if (EvolutionTableBlockEntity.TYPE != null) {
			BlockEntityRendererRegistry.register(EvolutionTableBlockEntity.TYPE, EvolutionTableRenderer::new);
		}
	}
}
