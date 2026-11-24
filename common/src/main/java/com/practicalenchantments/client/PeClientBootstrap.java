// 客户端每 tick 入口：由两个加载器的 ClientTickEvent 调用。

package com.practicalenchantments.client;

public final class PeClientBootstrap {
	private PeClientBootstrap() {
	}

	public static void tick() {
		EyeOfJudgmentClient.tick();
	}
}
