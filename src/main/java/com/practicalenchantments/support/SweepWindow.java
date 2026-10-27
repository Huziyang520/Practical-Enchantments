// 横扫窗口：记下这次挥击的主目标与时刻，让横扫命中能识别出自己。

package com.practicalenchantments.support;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class SweepWindow {
	private static final Map<UUID, Entity> MAIN_TARGET = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> TICK = new ConcurrentHashMap<>();

	private SweepWindow() {
	}

	public static void begin(Player player, Entity target) {
		MAIN_TARGET.put(player.getUUID(), target);
		TICK.put(player.getUUID(), player.tickCount);
	}

	public static boolean isSweepHit(Player player, Entity victim) {
		Integer tick = TICK.get(player.getUUID());
		if (tick == null || tick != player.tickCount) {
			return false;
		}
		Entity main = MAIN_TARGET.get(player.getUUID());
		return main != null && main != victim;
	}
}
