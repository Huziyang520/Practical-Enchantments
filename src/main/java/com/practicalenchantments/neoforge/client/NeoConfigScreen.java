package com.practicalenchantments.neoforge.client;

import com.practicalenchantments.PracticalEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * 客户端配置界面：直接复用 NeoForge 原生的 {@link ConfigurationScreen}，不另画一套。
 */
@EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, value = Dist.CLIENT)
public final class NeoConfigScreen {

	private NeoConfigScreen() {
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		ModList.get().getModContainerById(PracticalEnchantments.MOD_ID)
				.ifPresent(container -> {
					// 1.21.1 的扩展点收的是构造器（延迟到打开界面时才实例化），
					// 与 26.3 直接收实例的写法不同，语义一样。
					IConfigScreenFactory factory =
							(mod, parent) -> new ConfigurationScreen(mod, parent);
					container.<IConfigScreenFactory>registerExtensionPoint(IConfigScreenFactory.class, () -> factory);
				});
	}
}
