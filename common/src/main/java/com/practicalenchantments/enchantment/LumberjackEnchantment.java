package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * 伐木工附魔 - 斧 I 级
 * 效果：砍伐原木时递归破坏整棵相连树木及树叶；单次最大 64 方块上限
 */
public final class LumberjackEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":lumberjack";
	private static final int MAX_BLOCKS = 64;
	
	// 防止事件重入导致无限递归
	private static final ThreadLocal<Boolean> PROCESSING = ThreadLocal.withInitial(() -> Boolean.FALSE);


	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_BLOCK_BREAK, LumberjackEnchantment::onBlockBreak);
	}

	private static void onBlockBreak(BuiltInEvents.PostBlockBreakEvent event, EnchantmentContext ctx) {
		// 防止重入导致无限递归
		if (PROCESSING.get()) return;
		
		if (!(event.player() instanceof ServerPlayer player)) return;
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
