// 书袋搜索框的查询串：客户端输入时发给服务端，服务端按它重建可见槽位。

package com.practicalenchantments.network;

import net.minecraft.network.FriendlyByteBuf;

public record BagSearchPayload(String query) {
	private static final int MAX_LENGTH = 64;

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.query, MAX_LENGTH);
	}

	public static BagSearchPayload read(FriendlyByteBuf buffer) {
		return new BagSearchPayload(buffer.readUtf(MAX_LENGTH));
	}
}
