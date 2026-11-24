// 裁决命中计数：同一玩家对同一目标累计，换目标或死亡后清零。

package com.practicalenchantments.enchantment.support;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.practicalenchantments.PracticalEnchantments;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class VerdictTracker extends SavedData {
	private static final int MAX_ENTRIES = 10_000;
	private static final long EXPIRE_MILLIS = 30L * 24L * 60L * 60L * 1000L;
	private static final java.util.Set<UUID> APPLYING = java.util.concurrent.ConcurrentHashMap.newKeySet();
	public static final SavedDataType<VerdictTracker> TYPE = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "verdict_hits"),
			VerdictTracker::new,
			codec(),
			DataFixTypes.SAVED_DATA_MAP_DATA);

	private final Map<String, Integer> hits;
	private final Map<String, Long> stamps;

	public VerdictTracker() {
		this(new HashMap<>(), new HashMap<>());
	}

	private VerdictTracker(Map<String, Integer> hits, Map<String, Long> stamps) {
		this.hits = new HashMap<>(hits);
		this.stamps = new HashMap<>(stamps);
	}

	public static VerdictTracker get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(TYPE);
	}

	public int hits(UUID player, UUID target) {
		return this.hits.getOrDefault(key(player, target), 0);
	}

	public int record(UUID player, UUID target) {
		if (this.hits.size() > MAX_ENTRIES) {
			this.hits.clear();
			this.stamps.clear();
		}
		String key = key(player, target);
		int value = this.hits.getOrDefault(key, 0) + 1;
		this.hits.put(key, value);
		this.stamps.put(key, System.currentTimeMillis());
		this.setDirty();
		return value;
	}

	public void clearTarget(UUID target) {
		String suffix = "|" + target;
		boolean changed = this.hits.keySet().removeIf(key -> key.endsWith(suffix));
		changed |= this.stamps.keySet().removeIf(key -> key.endsWith(suffix));
		if (changed) {
			this.setDirty();
		}
	}

	public void expire() {
		long cutoff = System.currentTimeMillis() - EXPIRE_MILLIS;
		boolean changed = this.stamps.entrySet().removeIf(entry -> entry.getValue() < cutoff);
		if (changed) {
			this.hits.keySet().removeIf(key -> !this.stamps.containsKey(key));
			this.setDirty();
		}
	}

	public static boolean isApplying(UUID target) {
		return APPLYING.contains(target);
	}

	public static void markApplying(UUID target) {
		APPLYING.add(target);
	}

	public static void clearApplying(UUID target) {
		APPLYING.remove(target);
	}

	private static String key(UUID player, UUID target) {
		return player + "|" + target;
	}

	private static Codec<VerdictTracker> codec() {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("hits", Map.of())
						.forGetter(tracker -> tracker.hits),
				Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("stamps", Map.of())
						.forGetter(tracker -> tracker.stamps)
		).apply(instance, VerdictTracker::new));
	}
}
