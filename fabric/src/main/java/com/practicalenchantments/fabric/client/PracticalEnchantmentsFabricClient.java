// Fabric 客户端入口：装配容器界面与配置界面，专用服务器不加载本类。

package com.practicalenchantments.fabric.client;

import net.fabricmc.api.ClientModInitializer;

public final class PracticalEnchantmentsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PeFabricScreens.install();
		FcapConfigClient.install();
	}
}
