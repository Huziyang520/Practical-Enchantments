// 击杀类附魔留下的短时标记：附魔回调判定一次，Mixin 在执行点消费一次。

package com.practicalenchantments.enchantment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.LivingEntity;

public final class KillMarks {
	private static final long LIFETIME_MILLIS = 5_000L;
	private static final int MAX_ENTRIES = 512;
	private static final Map<UUID, Long> INCINERATED = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> DOUBLED = new ConcurrentHashMap<>();

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
