// Fabric 客户端入口：配置按钮走 Forge Config API Port，专用服务器不加载本类。

package com.practicalenchantments.fabric.client;

import net.fabricmc.api.ClientModInitializer;

public final class PracticalEnchantmentsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FcapConfigClient.install();
	}
}
