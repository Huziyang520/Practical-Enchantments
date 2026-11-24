// 避雷：雷暴时把落雷点改到最近的露天穿戴者。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.armor.LightningRodEnchantment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class MixinServerLevelLightning {
	@Inject(method = "findLightningTargetAround", at = @At("HEAD"), cancellable = true)
	private void pe$redirectLightning(BlockPos pos, CallbackInfoReturnable<BlockPos> info) {
		if (!Tunables.lightningRodPriorityOverRod()) {
			return;
		}
		ServerLevel level = (ServerLevel) (Object) this;
		if (!level.isThundering()) {
			return;
		}
		ServerPlayer best = null;
		double bestDistance = Double.MAX_VALUE;
		double radius = Tunables.lightningRodRadius();
		for (ServerPlayer player : level.players()) {
			if (!LightningRodEnchantment.wears(player)) {
				continue;
			}
			if (Tunables.lightningRodRequireSky() && !level.canSeeSky(player.blockPosition())) {
				continue;
			}
			double distance = player.blockPosition().distSqr(pos);
			if (distance <= radius * radius && distance < bestDistance) {
				best = player;
				bestDistance = distance;
			}
		}
		if (best != null) {
			info.setReturnValue(best.blockPosition());
		}
	}
}
