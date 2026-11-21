// 本模组唯一的网络通道：只承载书袋搜索串，服务端收到后交给当前菜单。

package com.practicalenchantments.network;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.BookBagMenu;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PeNetwork {
	private static final String PROTOCOL = "1";
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(PracticalEnchantments.MOD_ID, "main"),
			() -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
	private static boolean registered;

	private PeNetwork() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		CHANNEL.registerMessage(0, BagSearchPayload.class, BagSearchPayload::write,
				BagSearchPayload::read, PeNetwork::handleSearch);
	}

	private static void handleSearch(BagSearchPayload payload, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player != null && player.containerMenu instanceof BookBagMenu menu) {
				menu.setQuery(payload.query());
			}
		});
		context.setPacketHandled(true);
	}

	public static void sendSearch(String query) {
		CHANNEL.sendToServer(new BagSearchPayload(query));
	}

	public static void installClientSender() {
		BagSearchSender.install(PeNetwork::sendSearch);
	}
}
