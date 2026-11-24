// 收猎：死亡掉落期间捕获本次生成的 ItemEntity，延迟后移到击杀者脚下。

package com.practicalenchantments.enchantment.support;

import com.practicalenchantments.config.Tunables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public final class ScavengerCapture {
	private static final ThreadLocal<LivingEntity> ACTIVE = new ThreadLocal<>();
	private static final Map<UUID, List<ItemEntity>> CAPTURED = new HashMap<>();
	private static final Map<UUID, Scheduled> SCHEDULED = new HashMap<>();

	private ScavengerCapture() {
	}

	public static void begin(LivingEntity victim) {
		ACTIVE.set(victim);
		CAPTURED.computeIfAbsent(victim.getUUID(), key -> new ArrayList<>()).clear();
	}

	public static void end() {
		ACTIVE.remove();
	}

	public static void capture(ItemEntity item) {
		LivingEntity victim = ACTIVE.get();
		if (victim == null) {
			return;
		}
		CAPTURED.computeIfAbsent(victim.getUUID(), key -> new ArrayList<>()).add(item);
	}

	public static void onKill(LivingEntity victim, ServerPlayer killer, ServerLevel level) {
		if (KillMarks.isIncinerated(victim)) {
			CAPTURED.remove(victim.getUUID());
			return;
		}
		UUID id = victim.getUUID();
		SCHEDULED.put(id, new Scheduled(level, killer.getUUID(),
				level.getGameTime() + Math.max(1, Tunables.scavengerDelayTicks())));
	}

	public static void tick(ServerLevel level) {
		if (SCHEDULED.isEmpty()) {
			return;
		}
		long now = level.getGameTime();
		Iterator<Map.Entry<UUID, Scheduled>> iterator = SCHEDULED.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, Scheduled> entry = iterator.next();
			Scheduled scheduled = entry.getValue();
			if (scheduled.level != level || scheduled.fireAt > now) {
				continue;
			}
			iterator.remove();
			move(scheduled, CAPTURED.remove(entry.getKey()));
			if (now - scheduled.fireAt > 200L) {
				CAPTURED.remove(entry.getKey());
			}
		}
	}

	private static void move(Scheduled scheduled, List<ItemEntity> items) {
		if (items == null || items.isEmpty()) {
			return;
		}
		ServerPlayer killer = scheduled.level.getServer().getPlayerList().getPlayer(scheduled.killer);
		if (killer == null) {
			return;
		}
		Vec3 feet = killer.position();
		for (ItemEntity item : items) {
			if (item == null || !item.isAlive()) {
				continue;
			}
			double radius = Tunables.scavengerRadius();
			item.setPos(feet.x + (item.getRandom().nextDouble() - 0.5D) * radius,
					feet.y, feet.z + (item.getRandom().nextDouble() - 0.5D) * radius);
			item.setDeltaMovement(Vec3.ZERO);
		}
	}

	private record Scheduled(ServerLevel level, UUID killer, long fireAt) {
	}
}
