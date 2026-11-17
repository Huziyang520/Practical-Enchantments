// Fabric 客户端配置入口：把模组列表里的配置按钮接到 Forge Config API Port 的配置界面。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.PracticalEnchantments;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public final class FcapConfigClient {
	private FcapConfigClient() {
	}

	public static void install() {
		ConfigScreenFactoryRegistry.INSTANCE.register(PracticalEnchantments.MOD_ID, ConfigurationScreen::new);
	}
}
