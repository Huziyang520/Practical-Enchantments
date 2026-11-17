// Fabric 客户端入口：只在客户端装配配置界面，专用服务器不加载本类。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.fabric.config.MezzConfigBridge;
import com.practicalenchantments.fabric.config.TweaksScreens;
import net.fabricmc.api.ClientModInitializer;

public final class PracticalEnchantmentsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PeFabricScreens.install();
		FcapConfigClient.install();
		MezzConfigBridge.install();
		TweaksScreens.install();
	}
}
