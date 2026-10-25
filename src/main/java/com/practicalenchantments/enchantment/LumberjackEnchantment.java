// 伐木工附魔 - 斧 I 级

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

public final class LumberjackEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":lumberjack";
	private static final int MAX_BLOCKS = 64;
	private static final ThreadLocal<Boolean> PROCESSING = ThreadLocal.withInitial(() -> Boolean.FALSE);

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.POST_BLOCK_BREAK, LumberjackEnchantment::onBlockBreak);
	}

	private static void onBlockBreak(BuiltInEvents.PostBlockBreakEvent event, EnchantmentContext ctx) {
		if (PROCESSING.get()) return;
		ServerPlayer player = event.player();
		ServerLevel level = event.level();
		BlockPos brokenPos = event.pos();
		BlockState brokenState = event.blockState();
		if (!brokenState.is(BlockTags.LOGS)) return;
		ItemStack tool = event.tool();
		if (tool.isEmpty()) return;
		PROCESSING.set(true);

		try {
			Set<BlockPos> processed = new HashSet<>();
			Queue<BlockPos> queue = new LinkedList<>();
			queue.add(brokenPos);
			processed.add(brokenPos);
			int brokenCount = 0;

			while (!queue.isEmpty() && brokenCount < MAX_BLOCKS) {
				BlockPos current = queue.poll();

				for (BlockPos offset : new BlockPos[]{
					new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0),
					new BlockPos(0, 1, 0), new BlockPos(0, -1, 0),
					new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)
				}) {
					BlockPos neighbor = current.offset(offset);
					if (processed.contains(neighbor)) continue;
					if (brokenCount >= MAX_BLOCKS) break;
					BlockState neighborState = level.getBlockState(neighbor);

					if (neighborState.is(BlockTags.LOGS) || neighborState.is(BlockTags.LEAVES)) {
						processed.add(neighbor);
						queue.add(neighbor);
						level.destroyBlock(neighbor, true, player);
						brokenCount++;
					}
				}
			}
		} finally {
			PROCESSING.set(false);
		}
	}
}
