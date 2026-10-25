// 三叉戟牵引状态中心（贯穿/粉碎 × 忠诚）。

package com.practicalenchantments.enchantment;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TridentPullSupport {
	public static final double MOB_PULL_SPEED = 0.55D;
	public static final double MOB_PULL_LIFT = 0.12D;
	public static final double ITEM_PULL_SPEED = 0.55D;
	public static final double ITEM_PULL_RADIUS = 10.0D;
	public static final double ITEM_PULL_PLAYER_RADIUS = 8.0D;
	public static final double ITEM_CROSS_STEP = 0.5D;
	public static final int ITEM_CROSS_MAX_STEPS = 8;
	public static final double PULL_EPSILON = 0.5D;
	public static final int ITEM_PULL_DURATION_TICKS = 100;
	public static final int MOB_PULL_DURATION_TICKS = 25;

	private static final class State {
		@Nullable LivingEntity mobTarget;
		int ticks;
	}

	private static final Map<UUID, State> ACTIVE = new ConcurrentHashMap<>();

	private TridentPullSupport() {
	}

	public static void captureMob(UUID thrower, LivingEntity target) {
		State state = ACTIVE.computeIfAbsent(thrower, k -> new State());
		state.mobTarget = target;
		state.ticks = MOB_PULL_DURATION_TICKS;
	}

	public static void startItemPull(UUID thrower) {
		State state = ACTIVE.computeIfAbsent(thrower, k -> new State());
		state.ticks = Math.max(state.ticks, ITEM_PULL_DURATION_TICKS);
	}

	public static boolean tick(ServerLevel level, LivingEntity owner, Entity anchor) {
		State state = ACTIVE.get(owner.getUUID());
		if (state == null || state.ticks <= 0) {
			ACTIVE.remove(owner.getUUID());
			return false;
		}
		state.ticks--;
		LivingEntity target = state.mobTarget;
		if (target != null && target.isAlive()
			&& !isBoss(target)) {
			if (target instanceof net.minecraft.world.entity.Mob mob) {
				mob.getNavigation().stop();
				mob.setTarget(null);
			}
			target.setDeltaMovement(Vec3.ZERO);
			Vec3 desired = owner.position().subtract(target.position());
			if (desired.length() > PULL_EPSILON) {
				double distBoost = Math.min(desired.length() / 12.0D, 1.6D);
				target.setDeltaMovement(desired.normalize().scale(MOB_PULL_SPEED * distBoost)
					.add(0, MOB_PULL_LIFT, 0));
				target.fallDistance = 0.0F;
				target.hurtMarked = true;
				target.setOnGround(false);
			}
		}

		Vec3 ownerPos = owner.position();
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class,
			anchor.getBoundingBox().inflate(ITEM_PULL_RADIUS))) {
			pullItem(level, item, ownerPos);
		}

		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class,
			owner.getBoundingBox().inflate(ITEM_PULL_PLAYER_RADIUS))) {
			pullItem(level, item, ownerPos);
		}

		if (state.ticks <= 0) {
			ACTIVE.remove(owner.getUUID());
		}
		return true;
	}

	private static void pullItem(ServerLevel level, ItemEntity item, Vec3 ownerPos) {
		Vec3 move = ownerPos.subtract(item.position());
		double distance = move.length();
		if (distance <= PULL_EPSILON) {
			return;
		}
		Vec3 direction = move.scale(1.0D / distance);
		item.setDeltaMovement(direction.scale(ITEM_PULL_SPEED).add(0, 0.04D, 0));
		item.setPickUpDelay(0);
		item.hurtMarked = true;
		stepToward(level, item, direction, distance);
	}

	private static void stepToward(ServerLevel level, ItemEntity item, Vec3 direction, double distance) {
		double step = Math.min(ITEM_PULL_SPEED, distance);
		Vec3 offset = direction.scale(step);
		if (blockedByBlock(level, item, item.getBoundingBox().move(offset))) {
			crossObstacles(level, item, direction, distance);
			return;
		}
		Vec3 to = item.position().add(offset);
		item.setPos(to.x, to.y, to.z);
	}

	private static void crossObstacles(ServerLevel level, ItemEntity item, Vec3 direction,
		double distance) {
		Vec3 step = direction.scale(ITEM_CROSS_STEP);
		if (!blockedByBlock(level, item, item.getBoundingBox().move(step))) {
			return;
		}
		int furthest = (int) Math.min(ITEM_CROSS_MAX_STEPS, distance / ITEM_CROSS_STEP);
		Vec3 from = item.position();
		for (int steps = 2; steps <= furthest; steps++) {
			Vec3 offset = step.scale(steps);
			if (!blockedByBlock(level, item, item.getBoundingBox().move(offset))) {
				Vec3 landing = from.add(offset);
				item.setPos(landing.x, landing.y, landing.z);
				return;
			}
		}
	}

	private static boolean blockedByBlock(ServerLevel level, ItemEntity item, net.minecraft.world.phys.AABB box) {
		return level.getBlockCollisions(item, box).iterator().hasNext();
	}

	private static boolean isBoss(LivingEntity entity) {
		if (entity instanceof EnderDragon || entity instanceof WitherBoss) {
			return true;
		}
		String path = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
			.getKey(entity.getType()).getPath();
		return path.contains("dragon") || path.contains("wither") || path.contains("warden");
	}

	public static void clear(UUID thrower) {
		ACTIVE.remove(thrower);
	}
}
