// Fabric 客户端入口：装配容器界面与配置界面，专用服务器不加载本类。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.network.BagSearchPayload;
import com.practicalenchantments.network.BagSearchSender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class PracticalEnchantmentsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BagSearchSender.install(text -> ClientPlayNetworking.send(new BagSearchPayload(text)));
		PeFabricScreens.install();
		FcapConfigClient.install();
	}
}
