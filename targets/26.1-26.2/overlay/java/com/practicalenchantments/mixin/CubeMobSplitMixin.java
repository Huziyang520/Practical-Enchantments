// 焚灭击杀的立方体生物死后不再分裂（26.2+ 的 AbstractCubeMob）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.support.KillMarks;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.cubemob.AbstractCubeMob")
public abstract class CubeMobSplitMixin {
	@Inject(method = "getSplitCount", at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$skipSplitOnIncinerate(CallbackInfoReturnable<Integer> info) {
		if (KillMarks.isIncinerated((LivingEntity) (Object) this)) {
			info.setReturnValue(0);
		}
	}
}
