// 有瑕疵的黑曜石：仿照霜冰，计划刻推进裂纹阶段，末阶段碎回熔岩源。

package com.practicalenchantments.content;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class FlawedObsidianBlock extends Block {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	private static final int LAST_AGE = 3;
	private static final int DELAY_BASE = 20;
	private static final int DELAY_RANDOM = 20;

	public FlawedObsidianBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (oldState.getBlock() != this) {
			level.scheduleTick(pos, this, nextDelay(level.getRandom()));
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int age = state.getValue(AGE);
		if (age < LAST_AGE) {
			level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_ALL);
			level.scheduleTick(pos, this, nextDelay(random));
			return;
		}
		level.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return new ItemStack(PeContent.FLAWED_OBSIDIAN_ITEM.get());
	}
	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
		level.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
	}

	private static int nextDelay(RandomSource random) {
		return DELAY_BASE + random.nextInt(DELAY_RANDOM);
	}
}
