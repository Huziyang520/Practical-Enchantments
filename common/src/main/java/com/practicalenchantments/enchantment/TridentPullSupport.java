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
 *
 * <h2>跨方块：为什么是位置直移，而不是给掉落物"开穿透"</h2>
 *
 * <p>掉落物被拉向玩家的路上遇到方块会被原版碰撞挡住（跨一格或多格障碍即被拦截）。直觉的修法是
 * 牵引期间给掉落物开穿透（{@code Entity.noPhysics = true}），<b>但对 {@code ItemEntity} 无效</b>：
 * {@code javap -c net.minecraft.world.entity.item.ItemEntity} 显示它每一步 tick 都自己重算该字段
 * ——客户端分支直接置 {@code false}；服务端分支置 {@code !level.noCollision(this, box)}，为真时还会
 * 调 {@code moveTowardsClosestSpace} 把自己挤出方块。也就是说外部置的值会被它自己的 tick 覆盖，
 * 穿透这条路根本不成立（该行为在本线与 1.20.1 线一致，两边都实测过字节码）。</p>
 *
 * <p>因此改为<b>位置直移</b>：每刻先用掉落物自己的碰撞盒按同样的一小步做一次 {@code noCollision}
 * 探测，只有在"这一步会被挡住"时才沿同一方向一次跨过整个障碍——从第 2 步起逐步加大，找到第一个
 * 碰撞盒放得下的位置再 {@code setPos}，最多跨 {@link #ITEM_CROSS_MAX_STEPS} 步（0.5 × 8 = 4 格）。
 * 这样：</p>
 * <ul>
 *   <li>空旷处完全不介入，原有速度牵引一分不改；</li>
 *   <li>跨过去的是<b>整个障碍</b>，掉落物不会停在方块内部（停在内部会被原版挤回原侧，等于白跨）；</li>
 *   <li>厚墙或贴天花板时找不到落点就本刻不动、等下一刻，绝不把掉落物塞进石头里；</li>
 *   <li>跨越距离按到玩家的剩余距离封顶，不会冲过头。</li>
 * </ul>
 */
public final class TridentPullSupport {

	/** 生物牵引速度（每刻重设，带重力/摩擦补偿） */
	public static final double MOB_PULL_SPEED = 0.55D;
	/** 牵引上抛分量：让目标离地，减少地面摩擦 */
	public static final double MOB_PULL_LIFT = 0.12D;
	/** 掉落物牵引速度与半径（力度必须盖过地面摩擦才能"拉到身边"） */
	public static final double ITEM_PULL_SPEED = 0.45D;
	public static final double ITEM_PULL_RADIUS = 6.0D;
	/**
	 * 跨越障碍时每一步的长度（既是探测粒度，也是"至少跨多远"）。
	 *
	 * <p>用 0.5 是为了与 {@link #ITEM_PULL_SPEED} 同量级：跨越时每刻前进的距离与正常牵引差不多，
	 * 观感上不会一会儿慢一会儿瞬移。</p>
	 */
	public static final double ITEM_CROSS_STEP = 0.5D;
	/** 一次跨越最多前进的步数（0.5 × 8 = 4 格）；再厚就本刻放弃、等下一刻 */
	public static final int ITEM_CROSS_MAX_STEPS = 8;
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
			&& !isBoss(target)) {
			// 光设速度是拽不动的：生物的 AI 与寻路会在它自己的 tick 里把速度覆盖回去，这就是
			// "完全拉不过来"的原因。先停掉它的寻路与目标，再每刻重设速度，才算真的拽住。
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
			double distance = move.length();
			if (distance > PULL_EPSILON) {
				Vec3 direction = move.scale(1.0D / distance);
				item.setDeltaMovement(direction.scale(ITEM_PULL_SPEED).add(0, 0.04D, 0));
				item.setPickUpDelay(0);
				crossObstacles(level, item, direction, distance);
			}
		}

		if (state.ticks <= 0) {
			ACTIVE.remove(owner.getUUID());
		}
		return true;
	}

	/**
	 * 下一步会被方块挡住时，把掉落物沿同一方向一次送到障碍另一侧的空位上。
	 *
	 * <p>探测用的是掉落物自己的碰撞盒（与它 {@code move()} 时同一套判定）：先把碰撞盒按一小步
	 * （{@link #ITEM_CROSS_STEP}）前移，{@code noCollision} 为真说明这一步走得通，直接交给原版移动，
	 * 本方法不介入；为假才从第 2 步起逐步加大，取第一个碰撞盒放得下的位置直移过去。最多
	 * {@link #ITEM_CROSS_MAX_STEPS} 步，且不超过到玩家的剩余距离（免得冲过头）。</p>
	 *
	 * <p>为什么必须"跨过整个障碍"而不是往前挪一点点：掉落物一旦停在方块内部，{@code ItemEntity}
	 * 自己的 tick 会把 {@code noPhysics} 置真并调 {@code moveTowardsClosestSpace} 把它挤出去，
	 * 多半就是被挤回原侧——那等于没跨。所以宁可一次跨到位。</p>
	 *
	 * @param level     服务端世界
	 * @param item      掉落物
	 * @param direction 单位化的"朝玩家"方向
	 * @param distance  到玩家的剩余距离
	 */
	private static void crossObstacles(ServerLevel level, ItemEntity item, Vec3 direction,
		double distance) {
		Vec3 step = direction.scale(ITEM_CROSS_STEP);
		if (level.noCollision(item, item.getBoundingBox().move(step))) {
			return;
		}
		int furthest = (int) Math.min(ITEM_CROSS_MAX_STEPS, distance / ITEM_CROSS_STEP);
		Vec3 from = item.position();
		for (int steps = 2; steps <= furthest; steps++) {
			Vec3 offset = step.scale(steps);
			if (level.noCollision(item, item.getBoundingBox().move(offset))) {
				Vec3 landing = from.add(offset);
				item.setPos(landing.x, landing.y, landing.z);
				return;
			}
		}
	}

	/**
	 * BOSS 与"拽不动"的存在：一律不牵引。
	 *
	 * <p>除了末影龙与凋灵，还按实体 id 兜住监守者一类的重装怪——按 id 判定而不是逐个 instanceof，
	 * 免得以后版本新增大型怪时又要回来补一次。</p>
	 *
	 * @param entity 候选目标
	 * @return true 表示不牵引
	 */
	private static boolean isBoss(LivingEntity entity) {
		if (entity instanceof EnderDragon || entity instanceof WitherBoss) {
			return true;
		}
		String path = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
			.getKey(entity.getType()).getPath();
		return path.contains("dragon") || path.contains("wither") || path.contains("warden");
	}

	/** 清理某玩家的活动牵引（三叉戟落地/移除时不必精确清理，窗口到期自动清） */
	public static void clear(UUID thrower) {
		ACTIVE.remove(thrower);
	}
}
