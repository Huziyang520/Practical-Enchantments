// NeoForge 客户端装配：容器界面与方块实体渲染器。

package com.practicalenchantments.neoforge.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.client.BookBagScreen;
import com.practicalenchantments.client.EvolutionTableRenderer;
import com.practicalenchantments.client.EvolutionTableScreen;
import com.practicalenchantments.client.PeClientBootstrap;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import com.practicalenchantments.content.PeMenus;
import com.practicalenchantments.network.BagSearchPayload;
import com.practicalenchantments.network.BagSearchSender;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PracticalEnchantments.MOD_ID, dist = Dist.CLIENT)
public final class PracticalEnchantmentsNeoForgeClient {
	public PracticalEnchantmentsNeoForgeClient(IEventBus modEventBus, ModContainer container) {
		BagSearchSender.install(text ->
				net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new BagSearchPayload(text)));
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> PeClientBootstrap.tick());
		modEventBus.addListener(RegisterMenuScreensEvent.class, event -> {
			if (PeMenus.evolutionTable() != null) {
				event.register(PeMenus.evolutionTable(), EvolutionTableScreen::new);
			}
			if (PeMenus.bookBag() != null) {
				event.register(PeMenus.bookBag(), BookBagScreen::new);
			}
		});
		modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> {
			if (EvolutionTableBlockEntity.TYPE != null) {
				event.registerBlockEntityRenderer(EvolutionTableBlockEntity.TYPE, EvolutionTableRenderer::new);
			}
		});
	}
}
