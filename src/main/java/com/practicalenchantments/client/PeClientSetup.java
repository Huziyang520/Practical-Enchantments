// Forge 客户端装配：容器界面、方块实体渲染器与搜索包发送口。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import com.practicalenchantments.content.PeMenus;
import com.practicalenchantments.network.PeNetwork;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PeClientSetup {
	private PeClientSetup() {
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			if (PeMenus.evolutionTable() != null) {
				MenuScreens.register(PeMenus.evolutionTable(), EvolutionTableScreen::new);
			}
			if (PeMenus.bookBag() != null) {
				MenuScreens.register(PeMenus.bookBag(), BookBagScreen::new);
			}
		});
		PeNetwork.installClientSender();
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		if (EvolutionTableBlockEntity.TYPE != null) {
			event.registerBlockEntityRenderer(EvolutionTableBlockEntity.TYPE, EvolutionTableRenderer::new);
		}
	}
}
