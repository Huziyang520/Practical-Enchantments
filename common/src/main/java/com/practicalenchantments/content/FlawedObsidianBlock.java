// 有瑕疵的黑曜石：类似霜冰，随机刻后自行碎裂，破坏时不产生任何掉落（含精准采集）。

package com.practicalenchantments.content;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class FlawedObsidianBlock extends Block {
	public FlawedObsidianBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		level.removeBlock(pos, false);
	}
}
