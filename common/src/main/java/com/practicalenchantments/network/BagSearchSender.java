// 客户端发包的唯一出口：两端各自在客户端入口里装上实现。

package com.practicalenchantments.network;

import java.util.function.Consumer;

public final class BagSearchSender {
	private static Consumer<String> sink;

	private BagSearchSender() {
	}

	public static void install(Consumer<String> sender) {
		sink = sender;
	}

	public static void send(String query) {
		if (sink != null) {
			sink.accept(query);
		}
	}
}
