// 燎原轨迹方块：不碰撞、不蔓延，踩上着火，到期自动移除。

package com.practicalenchantments.content;

import com.practicalenchantments.config.Tunables;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WildfireTrailBlock extends Block {
	public WildfireTrailBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide()) {
			level.scheduleTick(pos, this, Tunables.wildfireLifetimeTicks());
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		level.removeBlock(pos, false);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
								InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		if (entity instanceof LivingEntity && !entity.fireImmune()) {
			effectApplier.apply(InsideBlockEffectType.FIRE_IGNITE);
		}
	}
}
