// 有瑕疵的黑曜石：仿照霜冰，随机刻推进裂纹阶段，末阶段碎回熔岩源。

package com.practicalenchantments.content;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class FlawedObsidianBlock extends Block {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	private static final int LAST_AGE = 3;

	public FlawedObsidianBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int age = state.getValue(AGE);
		level.setBlock(pos, age < LAST_AGE
				? state.setValue(AGE, age + 1)
				: Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
		return new ItemStack(PeContent.item(PeContent.FLAWED_OBSIDIAN));
	}
}
