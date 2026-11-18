// 书袋搜索框的查询串：客户端输入时发给服务端，服务端按它重建可见槽位。

package com.practicalenchantments.network;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BagSearchPayload(String query) implements CustomPacketPayload {
	public static final Type<BagSearchPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "bag_search"));

	public static final StreamCodec<FriendlyByteBuf, BagSearchPayload> STREAM_CODEC =
			CustomPacketPayload.codec(BagSearchPayload::write, BagSearchPayload::read);

	private void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.query, 64);
	}

	private static BagSearchPayload read(FriendlyByteBuf buffer) {
		return new BagSearchPayload(buffer.readUtf(64));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
