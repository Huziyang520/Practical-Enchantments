// 击杀类附魔留下的短时标记：附魔回调判定一次，Mixin 在执行点消费一次。

package com.practicalenchantments.enchantment.support;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.LivingEntity;

public final class KillMarks {
	private static final long LIFETIME_MILLIS = 5_000L;
	private static final int MAX_ENTRIES = 512;
	private static final Map<UUID, Long> INCINERATED = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> DOUBLED = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> VERDICT_PENDING = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> VERDICT_CLAIMED = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> MUTUAL_FIRED = new ConcurrentHashMap<>();

	private KillMarks() {
	}

	public static void markIncinerated(LivingEntity victim) {
		mark(INCINERATED, victim);
	}

	public static boolean consumeIncinerated(LivingEntity victim) {
		return consume(INCINERATED, victim);
	}

	public static boolean isIncinerated(LivingEntity victim) {
		Long expiry = INCINERATED.get(victim.getUUID());
		return expiry != null && expiry >= System.currentTimeMillis();
	}

	public static void markDoubled(LivingEntity victim) {
		mark(DOUBLED, victim);
	}

	public static boolean consumeDoubled(LivingEntity victim) {
		return consume(DOUBLED, victim);
	}

	public static void markVerdictPending(LivingEntity victim) {
		mark(VERDICT_PENDING, victim);
	}

	public static boolean isVerdictPending(LivingEntity victim) {
		return isActive(VERDICT_PENDING, victim);
	}

	public static void clearVerdictPending(LivingEntity victim) {
		VERDICT_PENDING.remove(victim.getUUID());
	}

	public static void markVerdictClaimed(LivingEntity victim) {
		mark(VERDICT_CLAIMED, victim);
	}

	public static boolean isVerdictClaimed(LivingEntity victim) {
		return isActive(VERDICT_CLAIMED, victim);
	}

	public static void markMutualFired(UUID victim) {
		if (MUTUAL_FIRED.size() > MAX_ENTRIES) {
			MUTUAL_FIRED.clear();
		}
		MUTUAL_FIRED.put(victim, System.currentTimeMillis() + LIFETIME_MILLIS);
	}

	public static boolean consumeMutualFired(UUID victim) {
		Long expiry = MUTUAL_FIRED.remove(victim);
		return expiry != null && expiry >= System.currentTimeMillis();
	}

	private static boolean isActive(Map<UUID, Long> marks, LivingEntity victim) {
		Long expiry = marks.get(victim.getUUID());
		return expiry != null && expiry >= System.currentTimeMillis();
	}

	private static void mark(Map<UUID, Long> marks, LivingEntity victim) {
		if (marks.size() > MAX_ENTRIES) {
			marks.clear();
		}
		marks.put(victim.getUUID(), System.currentTimeMillis() + LIFETIME_MILLIS);
	}

	private static boolean consume(Map<UUID, Long> marks, LivingEntity victim) {
		Long expiry = marks.remove(victim.getUUID());
		return expiry != null && expiry >= System.currentTimeMillis();
	}
}
