// 宙斯与波塞冬：三叉戟命中生物即召雷，不再要求雷雨；窗口内连续召雷可把天气变成雷暴。
// 只从进化附魔台获得，与引雷互斥。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ZeusPoseidonEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":zeus_poseidon";
	private static final int WINDOW_TICKS = 90 * 20;
	private static final int STRIKES_NEEDED = 5;
	private static final int STORM_TICKS = 300 * 20;
	private static final int COOLDOWN_TICKS = 12000 * 20;
	private static final String STORM_COOLDOWN = "zeus_poseidon_storm";
	private static final Map<UUID, Deque<Long>> RECENT_STRIKES = new ConcurrentHashMap<>();

	private ZeusPoseidonEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.PROJECTILE_HIT, ZeusPoseidonEnchantment::onProjectileHit);
	}

	private static void onProjectileHit(BuiltInEvents.ProjectileHitEvent event, EnchantmentContext ctx) {
		if (!event.weapon().is(Items.TRIDENT)) {
			return;
		}
		LivingEntity target = event.target();
		ServerLevel level = event.level();
		if (target == null || !level.canSeeSky(target.blockPosition())) {
			return;
		}
		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.spawn(level, target.blockPosition(), EntitySpawnReason.TRIGGERED);
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
		Deque<Long> strikes = RECENT_STRIKES.computeIfAbsent(attacker.getUUID(), key -> new ArrayDeque<>());
		synchronized (strikes) {
			strikes.addLast(now);
			while (!strikes.isEmpty() && now - strikes.peekFirst() > WINDOW_TICKS) {
				strikes.removeFirst();
			}
			if (strikes.size() < STRIKES_NEEDED) {
				return;
			}
			if (!MerlinApi.cooldowns().ready(attacker, STORM_COOLDOWN, COOLDOWN_TICKS)) {
				return;
			}
			strikes.clear();
			MerlinApi.cooldowns().trigger(attacker, STORM_COOLDOWN, COOLDOWN_TICKS);
			event.level().getServer().setWeatherParameters(0, STORM_TICKS, true, true);
		}
	}
}
