// 眩惑客户端：只反转本地移动输入，视角与服务端判定不受影响。

package com.practicalenchantments.client;

import com.practicalenchantments.content.PeEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec2;

public final class DizzinessClient {
	private DizzinessClient() {
	}

	public static Vec2 flip(Vec2 input) {
		if (input == null || !active()) {
			return input;
		}
		return new Vec2(-input.x, -input.y);
	}

	public static boolean active() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.player != null && PeEffects.hasDizziness(minecraft.player);
	}
}
