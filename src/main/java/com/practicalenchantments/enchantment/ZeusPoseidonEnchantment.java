// 宙斯与波塞冬：三叉戟命中生物即召雷，不再要求雷雨；窗口内连续召雷可把天气变成雷暴。
// 只从进化附魔台获得，与引雷互斥。冷却表是本地状态，不依赖库里的冷却接口。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Items;

public final class ZeusPoseidonEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":zeus_poseidon";
	private static final int TICKS_PER_SECOND = 20;
	private static final Map<UUID, Deque<Long>> RECENT_STRIKES = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> STORM_READY_AT = new ConcurrentHashMap<>();

	private ZeusPoseidonEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
				BuiltInEvents.PROJECTILE_HIT, ZeusPoseidonEnchantment::onProjectileHit);
	}

	private static void onProjectileHit(BuiltInEvents.ProjectileHitEvent event, EnchantmentContext ctx) {
		if (!event.weapon().is(Items.TRIDENT)) {
			return;
		}
		LivingEntity target = event.target();
		ServerLevel level = event.level();
		if (target == null || (Tunables.zeusRequireSky() && !level.canSeeSky(target.blockPosition()))) {
			return;
		}
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.spawn(level, target.blockPosition(), MobSpawnType.TRIGGERED);
		if (bolt != null && event.attacker() instanceof ServerPlayer player) {
			bolt.setCause(player);
		}
		countStrike(event);
	}

	private static void countStrike(BuiltInEvents.ProjectileHitEvent event) {
		LivingEntity attacker = event.attacker();
		if (attacker == null) {
			return;
		}
		long now = event.level().getGameTime();
		int windowTicks = Tunables.zeusStormWindowSeconds() * TICKS_PER_SECOND;
		int cooldownTicks = Tunables.zeusStormCooldownSeconds() * TICKS_PER_SECOND;
		Deque<Long> strikes = RECENT_STRIKES.computeIfAbsent(attacker.getUUID(), key -> new ArrayDeque<>());
		synchronized (strikes) {
			strikes.addLast(now);
			while (!strikes.isEmpty() && now - strikes.peekFirst() > windowTicks) {
				strikes.removeFirst();
			}
			if (strikes.size() < Tunables.zeusStormStrikes()) {
				return;
			}
			Long readyAt = STORM_READY_AT.get(attacker.getUUID());
			if (readyAt != null && now < readyAt) {
				return;
			}
			strikes.clear();
			STORM_READY_AT.put(attacker.getUUID(), now + cooldownTicks);
			event.level().setWeatherParameters(0,
					Tunables.zeusStormDurationSeconds() * TICKS_PER_SECOND, true, true);
		}
	}
}
