// 燎原使用的是原版火焰方块；这个表只负责给本模组放置的火焰设置寿命并阻止蔓延。

package com.practicalenchantments.enchantment.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class WildfireTracker {
	private static final int MAX_ENTRIES = 4096;
	private static final Map<ResourceKey<Level>, Map<Long, Long>> EXPIRY = new ConcurrentHashMap<>();

	private WildfireTracker() {
	}

	public static void mark(ServerLevel level, BlockPos pos, long expiry) {
		Map<Long, Long> marks = EXPIRY.computeIfAbsent(level.dimension(), key -> new ConcurrentHashMap<>());
		if (marks.size() > MAX_ENTRIES) {
			marks.clear();
		}
		marks.put(pos.asLong(), expiry);
	}

	public static Long expiry(ServerLevel level, BlockPos pos) {
		Map<Long, Long> marks = EXPIRY.get(level.dimension());
		return marks == null ? null : marks.get(pos.asLong());
	}

	public static void clear(ServerLevel level, BlockPos pos) {
		Map<Long, Long> marks = EXPIRY.get(level.dimension());
		if (marks != null) {
			marks.remove(pos.asLong());
		}
	}
}
