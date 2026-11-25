// 原版 FireBlock.tick 会向四周蔓延；燎原放置的火焰在 burn_blocks=false 时接管 tick。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.support.WildfireTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class MixinFireBlock {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void pe$controlWildfire(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
									CallbackInfo info) {
		Long expiry = WildfireTracker.expiry(level, pos);
		if (expiry == null || Tunables.wildfireBurnBlocks()) {
			return;
		}
		if (level.getGameTime() >= expiry) {
			level.removeBlock(pos, false);
			WildfireTracker.clear(level, pos);
		} else {
			level.scheduleTick(pos, Blocks.FIRE, 1);
		}
		info.cancel();
	}
}
