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

/**
 * 三叉戟牵引状态中心（贯穿/粉碎 × 忠诚）。
 *
 * <p>两条捕获路径共用一张按<b>投掷者 UUID</b> 索引的状态表：</p>
 * <ul>
 *   <li>实体命中 → 走 enchantlib 的 {@code PROJECTILE_HIT} 事件回调
 *       （{@link PenetrationEnchantment#onProjectileHit} 调 {@link #captureMob}），
 *       不再对 ThrownTrident#onHitEntity 注入 Mixin；</li>
 *   <li>方块命中（粉碎）→ {@code TridentSmashMixin} 破坏成功后调 {@link #startItemPull}。</li>
 * </ul>
 *
 * <p>{@code TridentPullMixin} 的三叉戟 tick 只读本表：对存活目标与掉落物施加
 * 朝向主人的牵引。牵引不依赖三叉戟的 {@code dealtDamage}——方块命中路径上
 * 该字段不会置位（坑 30 根因）。</p>
 *
 * <p>同一玩家同时多把三叉戟回归时共用一条状态，牵引每刻重设速度，
 * 效果只增强不冲突。</p>
 */
public final class TridentPullSupport {

	/** 生物牵引速度（每刻重设，带重力/摩擦补偿） */
	public static final double MOB_PULL_SPEED = 0.55D;
	/** 牵引上抛分量：让目标离地，减少地面摩擦 */
	public static final double MOB_PULL_LIFT = 0.12D;
	/** 掉落物牵引速度与半径（力度必须盖过地面摩擦才能"拉到身边"） */
	public static final double ITEM_PULL_SPEED = 0.45D;
	public static final double ITEM_PULL_RADIUS = 6.0D;
	/** 距离小于该值停止牵引（避免抖动） */
	public static final double PULL_EPSILON = 0.5D;
	/** 粉碎产物牵引窗口（tick）：要长于掉落物飞回来的时间 */
	public static final int ITEM_PULL_DURATION_TICKS = 60;
	/** 实体命中牵引窗口（tick） */
	public static final int MOB_PULL_DURATION_TICKS = 25;

	private static final class State {
		@Nullable LivingEntity mobTarget;
		int ticks;
	}

	/** 按投掷者 UUID 索引的活动牵引状态 */
	private static final Map<UUID, State> ACTIVE = new ConcurrentHashMap<>();

	private TridentPullSupport() {
	}

	/** 实体命中捕获（enchantlib PROJECTILE_HIT 回调调用）：记录目标并开窗 */
	public static void captureMob(UUID thrower, LivingEntity target) {
		State state = ACTIVE.computeIfAbsent(thrower, k -> new State());
		state.mobTarget = target;
		state.ticks = MOB_PULL_DURATION_TICKS;
	}

	/** 粉碎方块命中开窗（TridentSmashMixin 调用）：仅牵引掉落物 */
	public static void startItemPull(UUID thrower) {
		State state = ACTIVE.computeIfAbsent(thrower, k -> new State());
		state.ticks = Math.max(state.ticks, ITEM_PULL_DURATION_TICKS);
	}

	/**
	 * 三叉戟 tick 驱动：对本投掷者的活动牵引施加一次力。
	 *
	 * @param level  服务端世界
	 * @param owner  投掷者（生物牵引的目标点）
	 * @param anchor 三叉戟实体（掉落物扫描中心——掉落物生成在方块处，
	 *               围绕回归中的三叉戟扫描才能在半路"接住"它们一路带到主人身边）
	 * @return 本刻是否仍有活动牵引（供 Mixin 决定是否继续扫描）
	 */
	public static boolean tick(ServerLevel level, LivingEntity owner, Entity anchor) {
		State state = ACTIVE.get(owner.getUUID());
		if (state == null || state.ticks <= 0) {
			ACTIVE.remove(owner.getUUID());
			return false;
		}
		state.ticks--;

		// 牵引命中生物（末影龙/凋灵免疫）
		LivingEntity target = state.mobTarget;
		if (target != null && target.isAlive()
			&& !(target instanceof EnderDragon) && !(target instanceof WitherBoss)) {
			Vec3 desired = owner.position().subtract(target.position());
			if (desired.length() > PULL_EPSILON) {
				double distBoost = Math.min(desired.length() / 16.0D, 1.2D);
				target.setDeltaMovement(desired.normalize().scale(MOB_PULL_SPEED * distBoost)
					.add(0, MOB_PULL_LIFT, 0));
				// 26.3 起 Entity#hurtMarked 字段已移除，markHurt() 改为写 syncVelocity；
				// 牵引需要每刻把速度同步给客户端，直接置位该公开字段。
				target.syncVelocity = true;
				target.setOnGround(false);
			}
		}

		// 牵引回归路径附近的掉落物（粉碎/贯穿产物）：每刻重设速度，保证拉到身边
		Vec3 ownerPos = owner.position();
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class,
			anchor.getBoundingBox().inflate(ITEM_PULL_RADIUS))) {
			Vec3 move = ownerPos.subtract(item.position());
			if (move.length() > PULL_EPSILON) {
				item.setDeltaMovement(move.normalize().scale(ITEM_PULL_SPEED).add(0, 0.04D, 0));
				item.setPickUpDelay(0);
			}
		}

		if (state.ticks <= 0) {
			ACTIVE.remove(owner.getUUID());
		}
		return true;
	}

	/** 清理某玩家的活动牵引（三叉戟落地/移除时不必精确清理，窗口到期自动清） */
	public static void clear(UUID thrower) {
		ACTIVE.remove(thrower);
	}
}
