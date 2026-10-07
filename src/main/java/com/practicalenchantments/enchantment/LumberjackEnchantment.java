package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * 伐木工附魔 - 斧 I 级
 * 效果：砍伐原木时递归破坏整棵相连树木及树叶；单次最大 64 方块上限
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 * <ol>
 * <li><b>{@code registerCallbacks} 去掉 {@code HolderLookup.Provider} 参数</b>，改用
 * {@code PracticalEnchantments.resolveEnchantment(ID)}（原因见 {@code AerialHasteEnchantment}
 * 类注释第 1 条）。</li>
 *
 * <li><b>{@code Holder<Enchantment>} → {@code Enchantment}</b>：见 {@code VenomEnchantment}
 * 类注释第 2 条。注意本类的 {@code holder} 局部变量在注册后就不再使用，故移植时直接内联进
 * {@code register} 调用，没有留一个没人读的局部变量。</li>
 *
 * <li><b>{@code level.destroyBlock(BlockPos, boolean, Entity)} 是可用的。</b>它<b>不在</b>
 * {@code Level} 上直接声明——是 {@code LevelWriter} 接口的 default 方法
 * （{@code Level → CommonLevelAccessor → LevelSimulatedRW → LevelWriter}，已用 javap 逐层确认），
 * 所以调用点写法与 26.3 完全一致，只是 import 里不需要多带任何东西。</li>
 *
 * <li><b>重入保护原样保留，并且是必需的。</b>{@code ThreadLocal<Boolean> PROCESSING} 挡的是这样一条链：
 * 本回调调用 {@code destroyBlock} → 又触发 {@code POST_BLOCK_BREAK} → 又进入本回调。
 * 1.20.1 的 {@code MixinPlayerDestroyBlock} 用 {@code Deque} 支持嵌套破坏，所以这条链在
 * 1.20.1 上<b>真的会成立</b>，这个标志不是摆设。{@code finally} 里复位同样保留，
 * 保证异常路径下不会永久卡住。</li>
 *
 * <li><b>其它逐字直接移植。</b>{@code event.player()}（记录里的类型已经是 {@code ServerPlayer}，
 * 那句 {@code instanceof} 恒真，保留原样以免改变代码形状）、{@code event.pos()/blockState()/tool()}、
 * {@code BlockTags.LOGS/LEAVES}、{@code current.offset(BlockPos)}、
 * {@code level.getBlockState(BlockPos)}、BFS 的 6 个方向与 64 上限全部同形。</li>
 *
 * <li><b>一个原样保留的细节</b>：起始点 {@code brokenPos} 只是入队并标为已处理，<b>不会</b>被
 * {@code destroyBlock} 再破坏一次（它已经被玩家破坏了），{@code brokenCount} 也不含它。
 * 26.3 如此，移植未改。</li>
 * </ol>
 */
public final class LumberjackEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":lumberjack";
	private static final int MAX_BLOCKS = 64;

	// 防止事件重入导致无限递归
	private static final ThreadLocal<Boolean> PROCESSING = ThreadLocal.withInitial(() -> Boolean.FALSE);


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.POST_BLOCK_BREAK, LumberjackEnchantment::onBlockBreak);
	}

	private static void onBlockBreak(BuiltInEvents.PostBlockBreakEvent event, EnchantmentContext ctx) {
		// 防止重入导致无限递归
		if (PROCESSING.get()) return;

		// ⚠️ 26.3 这里写的是 `if (!(event.player() instanceof ServerPlayer player)) return;`，
		// 在 1.20.1 上<b>编译不过</b>：本版本 MerlinLib 的 PostBlockBreakEvent 已经把 player
		// 声明成 ServerPlayer（26.3 的 MerlinLib 也是），而 Java 16+ 对"操作数类型已经是模式类型
		// 的子类型"的 instanceof 模式匹配直接报错
		// （expression type ServerPlayer is a subtype of pattern type ServerPlayer）。
		// 原句的意图是"不是服务端玩家就跳过"，在本版本里这个条件<b>恒为真</b>，所以改成直接取值，
		// 行为完全不变。注意：这处错误在 26.3 的源码里同样存在，属于原工程自带的疏漏。
		ServerPlayer player = event.player();
		ServerLevel level = event.level();
		BlockPos brokenPos = event.pos();
		BlockState brokenState = event.blockState();

		// 只处理原木（使用 minecraft:logs 标签）
		if (!brokenState.is(BlockTags.LOGS)) return;

		// 检查是否有伐木工附魔
		ItemStack tool = event.tool();
		if (tool.isEmpty()) return;

		// 设置重入标志
		PROCESSING.set(true);

		try {
			// 递归破坏相连的原木和树叶
			Set<BlockPos> processed = new HashSet<>();
			Queue<BlockPos> queue = new LinkedList<>();
			queue.add(brokenPos);
			processed.add(brokenPos);

			int brokenCount = 0;

			while (!queue.isEmpty() && brokenCount < MAX_BLOCKS) {
				BlockPos current = queue.poll();

				// 检查 6 个方向
				for (BlockPos offset : new BlockPos[]{
					new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0),
					new BlockPos(0, 1, 0), new BlockPos(0, -1, 0),
					new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)
				}) {
					BlockPos neighbor = current.offset(offset);

					if (processed.contains(neighbor)) continue;
					if (brokenCount >= MAX_BLOCKS) break;

					BlockState neighborState = level.getBlockState(neighbor);

					// 如果是原木或树叶（使用 minecraft:logs 和 minecraft:leaves 标签）
					if (neighborState.is(BlockTags.LOGS) || neighborState.is(BlockTags.LEAVES)) {
						processed.add(neighbor);
						queue.add(neighbor);

						// 破坏方块
						level.destroyBlock(neighbor, true, player);
						brokenCount++;
					}
				}
			}
		} finally {
			// 清除重入标志
			PROCESSING.set(false);
		}
	}
}