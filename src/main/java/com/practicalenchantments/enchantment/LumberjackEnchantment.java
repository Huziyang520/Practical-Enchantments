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

		// ⚠️ 上游版本这里写的是 `if (!(event.player() instanceof ServerPlayer player)) return;`，
		// 在 1.20.1 上<b>编译不过</b>：本版本 MerlinLib 的 PostBlockBreakEvent 已经把 player
		// 声明成 ServerPlayer（上游版本的 MerlinLib 也是），而 Java 16+ 对"操作数类型已经是模式类型
		// 的子类型"的 instanceof 模式匹配直接报错
		// （expression type ServerPlayer is a subtype of pattern type ServerPlayer）。
		// 原句的意图是"不是服务端玩家就跳过"，在本版本里这个条件<b>恒为真</b>，所以改成直接取值，
		// 行为完全不变。注意：这处错误在 上游版本的源码里同样存在，属于原工程自带的疏漏。
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