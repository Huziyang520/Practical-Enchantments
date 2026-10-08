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
 *   <li>实体命中 → 走 MerlinLib 的 {@code PROJECTILE_HIT} 事件回调
 *       （{@code PenetrationEnchantment#onProjectileHit} 调 {@link #captureMob}），
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
 * <h2>1.20.1 移植：速度同步的字段名换了</h2>
 *
 * <p>公开方法签名与 26.3 完全一致（{@link #captureMob}、{@link #startItemPull}、
 * {@link #tick}、{@link #clear} 以及全部常量），只有一处被迫改动：</p>
 *
 * <p>26.3 的注释写着"Entity#hurtMarked 字段已移除，markHurt() 改为写 syncVelocity"，
 * 于是 26.3 的代码直接置位 {@code target.syncVelocity = true}。<b>1.20.1 恰好相反</b>：
 * 没有 {@code syncVelocity}，有的是公开字段 {@code Entity.hurtMarked}。这不是猜测——
 * 用 {@code javap} 把整个 Forge jar 扫了一遍，引用 {@code hurtMarked} 的类只有 7 个，
 * 其中服务端的 {@code ServerEntity} 是真正的消费者：</p>
 *
 * <pre>
 * net/minecraft/server/level/ServerEntity.class:
 *   1129: getfield  Entity.hurtMarked:Z
 *   1136: ifeq      1162
 *   1139: new       ClientboundSetEntityMotionPacket
 *   1151: invokevirtual broadcastAndSend
 *   1155: getfield  Entity.hurtMarked:Z
 *   1159: putfield  Entity.hurtMarked:Z      // 发完清位
 * </pre>
 *
 * <p>也就是说 1.20.1 里置位 {@code hurtMarked} 正是"把这一时刻的速度发给客户端"的官方手段，
 * 与 26.3 置位 {@code syncVelocity} 完全对位。所以这里照旧每刻置位，只是字段名换成
 * {@code hurtMarked}。</p>
 *
 * <h2>跨方块：为什么是位置直移，而不是给掉落物"开穿透"</h2>
 *
 * <p>掉落物被拉向玩家的路上遇到方块会被原版碰撞挡住（跨一格或多格障碍即被拦截）。直觉的修法是
 * 牵引期间给掉落物开穿透（{@code Entity.noPhysics = true}），<b>但对 {@code ItemEntity} 无效</b>：
 * {@code javap -c net.minecraft.world.entity.item.ItemEntity} 显示它每一步 tick 都自己重算该字段
 * ——客户端分支直接置 {@code false}；服务端分支置 {@code !level.noCollision(this, box)}，为真时还会
 * 调 {@code moveTowardsClosestSpace} 把自己挤出方块。也就是说外部置的值会被它自己的 tick 覆盖，
 * 穿透这条路根本不成立。</p>
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
	public static final double ITEM_PULL_SPEED = 0.55D;
	public static final double ITEM_PULL_RADIUS = 10.0D;
	/**
	 * 以玩家为中心的第二扫描半径。
	 *
	 * <p>三叉戟只扫自己身边（{@link #ITEM_PULL_RADIUS}），而被粉碎的方块可能在回归路径之外，
	 * 其掉落物从不在三叉戟的扫描球里出现过——这就是"经常失败"。玩家身边再扫一遍把它们接上。</p>
	 */
	public static final double ITEM_PULL_PLAYER_RADIUS = 8.0D;
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
	public static final int ITEM_PULL_DURATION_TICKS = 100;
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

	/** 实体命中捕获（MerlinLib PROJECTILE_HIT 回调调用）：记录目标并开窗 */
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
				// 1.20.1 反过来——没有 syncVelocity，速度同步的公开入口就是 hurtMarked
				// （见类 javadoc 里 ServerEntity 的 javap 证据）。置位它，服务端才会把
				// 这一时刻的速度用 ClientboundSetEntityMotionPacket 发给客户端。
				target.hurtMarked = true;
				target.setOnGround(false);
			}
		}

		// 牵引回归路径附近的掉落物（粉碎/贯穿产物）：每刻重设速度，保证拉到身边
		Vec3 ownerPos = owner.position();
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class,
			anchor.getBoundingBox().inflate(ITEM_PULL_RADIUS))) {
			pullItem(level, item, ownerPos);
		}
		// 再以玩家为中心扫一遍：偏离回归路径的产物从未进入三叉戟的扫描球，只有这一遍能接住。
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class,
			owner.getBoundingBox().inflate(ITEM_PULL_PLAYER_RADIUS))) {
			pullItem(level, item, ownerPos);
		}

		if (state.ticks <= 0) {
			ACTIVE.remove(owner.getUUID());
		}
		return true;
	}

	/**
	 * 把一件掉落物朝玩家拽一步。
	 *
	 * <p>和生物分支一样每刻重设速度；关键在于补上速度同步——1.20.1 的 {@code ServerEntity} 只在
	 * {@code Entity.hurtMarked} 置位时才把这一时刻的速度发出去（见类 javadoc 的 javap 证据）。
	 * 原先生物分支置位、掉落物分支忘了置：服务端一直在拉，客户端看到的还是旧位置，表现就是
	 * "拉了但经常像失败"。</p>
	 *
	 * @param level    服务端世界（跨越障碍时用它做碰撞探测）
	 * @param item     掉落物
	 * @param ownerPos 玩家的当前位置
	 */
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
		crossObstacles(level, item, direction, distance);
	}

	/**
	 * 下一步会被方块挡住时，把掉落物沿同一方向一次送到障碍另一侧的空位上。
	 *
	 * <p>探测用的是掉落物自己的碰撞盒（与它 {@code move()} 时同一套判定）：先把碰撞盒按一小步
	 * （{@link #ITEM_CROSS_STEP}）前移，这一步走得通就直接交给原版移动，本方法不介入；走不通才从
	 * 第 2 步起逐步加大，取第一个碰撞盒放得下的位置直移过去。最多 {@link #ITEM_CROSS_MAX_STEPS}
	 * 步，且不超过到玩家的剩余距离（免得冲过头）。</p>
	 *
	 * <h2>探测为什么必须是纯方块，而不是 {@code noCollision}</h2>
	 *
	 * <p>初版用的是 {@code Level#noCollision(Entity, AABB)}。它看着合适，实际把<b>实体</b>也算进
	 * 碰撞（{@code CollisionGetter} 里它就是 {@code getEntityCollisions(...).isEmpty()} 加上方块
	 * 那一半），于是平地上也会"被挡住"：掉落物被牵引到玩家身边时，前移一步的碰撞盒正好套住玩家
	 * 自己的身体，探测报真，跨越逻辑便按同一方向去找 1~4 格外的"空位"——找到的是玩家<b>另一侧</b>
	 * 的地面，于是 {@code setPos} 把掉落物瞬移过玩家，下一刻方向反过来又瞬移回来。表现就是
	 * "平地也拉不回来"，一直在玩家身侧抖。</p>
	 *
	 * <p>成群掉落物同理：彼此也是实体，同样互相当成障碍。所以探测改用
	 * {@code CollisionGetter#getBlockCollisions}——它只取方块形状，玩家与邻近掉落物都不再算障碍，
	 * 跨越只为真正的方块存在。</p>
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

	/**
	 * 这一段碰撞盒是否被<b>方块</b>挡住（不算实体）。
	 *
	 * <p>{@code getBlockCollisions} 是 {@code CollisionGetter} 上的公开方法，返回的只有方块形状与
	 * 世界边界；玩家、生物、其他掉落物都不在其中。跨越逻辑只该被方块触发，所以用它。</p>
	 *
	 * @param level 服务端世界
	 * @param item  掉落物（提供碰撞上下文，例如它自己的尺寸与 {@code noPhysics}）
	 * @param box   待测的碰撞盒
	 * @return true 表示被方块挡住
	 */
	private static boolean blockedByBlock(ServerLevel level, ItemEntity item, net.minecraft.world.phys.AABB box) {
		return level.getBlockCollisions(item, box).iterator().hasNext();
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
