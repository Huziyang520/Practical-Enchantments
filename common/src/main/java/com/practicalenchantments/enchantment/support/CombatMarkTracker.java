// 仁击 / 义斩的层数持久化：按目标 UUID 存两份计数，死亡时由死亡 Mixin 清理。

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

public final class CombatMarkTracker extends SavedData {
	private static final int MAX_ENTRIES = 10_000;
	public static final SavedDataType<CombatMarkTracker> TYPE = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "combat_marks"),
			CombatMarkTracker::new,
			codec(),
			DataFixTypes.SAVED_DATA_MAP_DATA);

	private final Map<String, Integer> benevolent;
	private final Map<String, Integer> righteous;

	public CombatMarkTracker() {
		this(new HashMap<>(), new HashMap<>());
	}

	private CombatMarkTracker(Map<String, Integer> benevolent, Map<String, Integer> righteous) {
		this.benevolent = new HashMap<>(benevolent);
		this.righteous = new HashMap<>(righteous);
	}

	public static CombatMarkTracker get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(TYPE);
	}

	public int benevolent(UUID target) {
		return this.benevolent.getOrDefault(target.toString(), 0);
	}

	public int righteous(UUID target) {
		return this.righteous.getOrDefault(target.toString(), 0);
	}

	public int addBenevolent(UUID target, int limit) {
		return add(this.benevolent, target, limit);
	}

	public int addRighteous(UUID target, int limit) {
		return add(this.righteous, target, limit);
	}

	public boolean consumeRighteous(UUID target) {
		return consume(this.righteous, target);
	}

	public boolean consumeBenevolent(UUID target) {
		return consume(this.benevolent, target);
	}

	public void clear(UUID target) {
		String key = target.toString();
		boolean changed = this.benevolent.remove(key) != null;
		changed |= this.righteous.remove(key) != null;
		if (changed) {
			this.setDirty();
		}
	}

	private int add(Map<String, Integer> marks, UUID target, int limit) {
		if (marks.size() > MAX_ENTRIES) {
			marks.clear();
		}
		String key = target.toString();
		int value = Math.min(limit, marks.getOrDefault(key, 0) + 1);
		marks.put(key, value);
		this.setDirty();
		return value;
	}

	private boolean consume(Map<String, Integer> marks, UUID target) {
		String key = target.toString();
		Integer value = marks.get(key);
		if (value == null || value <= 0) {
			return false;
		}
		if (value == 1) {
			marks.remove(key);
		} else {
			marks.put(key, value - 1);
		}
		this.setDirty();
		return true;
	}

	private static Codec<CombatMarkTracker> codec() {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("benevolent", Map.of())
						.forGetter(tracker -> tracker.benevolent),
				Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("righteous", Map.of())
						.forGetter(tracker -> tracker.righteous)
		).apply(instance, CombatMarkTracker::new));
	}
}
