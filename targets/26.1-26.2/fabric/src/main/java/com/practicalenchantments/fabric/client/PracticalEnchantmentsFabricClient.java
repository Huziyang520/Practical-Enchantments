// Fabric 客户端入口：配置按钮走 Forge Config API Port，界面与渲染器在此装配。

package com.practicalenchantments.fabric.client;

import net.fabricmc.api.ClientModInitializer;

public final class PracticalEnchantmentsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FcapConfigClient.install();
		PeFabricScreens.install();
	}
}
