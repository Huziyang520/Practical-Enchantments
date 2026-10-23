package com.practicalenchantments.enchantment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.LivingEntity;

/**
 * 击杀类附魔留下的短时标记：附魔回调判定一次，Mixin 在执行点消费一次。
 */
public final class KillMarks {

	/** 标记存活时间；击杀与掉落/分裂发生在同一次死亡流程里，5 秒已足够宽松。 */
	private static final long LIFETIME_MILLIS = 5_000L;
	/** 标记表的上限，超过就整体清空，避免异常路径堆积。 */
	private static final int MAX_ENTRIES = 512;

	private static final Map<UUID, Long> INCINERATED = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> DOUBLED = new ConcurrentHashMap<>();

	private KillMarks() {
	}

	/**
	 * 标记「死于焚灭」，供阻止史莱姆类分裂使用。
	 *
	 * @param victim 死者
	 */
	public static void markIncinerated(LivingEntity victim) {
		mark(INCINERATED, victim);
	}

	/**
	 * @param victim 死者
	 * @return 该死者是否被焚灭击杀，消费后返回 false
	 */
	public static boolean consumeIncinerated(LivingEntity victim) {
		return consume(INCINERATED, victim);
	}

	/**
	 * 只查询不消费：同一次死亡里掉落要拦多处，标记必须留到最后一处。
	 *
	 * @param victim 死者
	 * @return 该死者是否被焚灭击杀
	 */
	public static boolean isIncinerated(LivingEntity victim) {
		Long expiry = INCINERATED.get(victim.getUUID());
		return expiry != null && expiry >= System.currentTimeMillis();
	}

	/**
	 * 标记「死于神木气息」，供掉落翻倍使用。
	 *
	 * @param victim 死者
	 */
	public static void markDoubled(LivingEntity victim) {
		mark(DOUBLED, victim);
	}

	/**
	 * @param victim 死者
	 * @return 该死者是否死于神木气息，消费后返回 false
	 */
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
