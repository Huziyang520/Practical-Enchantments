// 三叉戟回归 tick 驱动牵引（贯穿/粉碎 × 忠诚）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.TridentPullSupport;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class TridentPullMixin {
	@Inject(method = "tick()V", at = @At("TAIL"), require = 1)
	private void practicalenchantments$pullWhileReturning(CallbackInfo ci) {
		ThrownTrident self = (ThrownTrident) (Object) this;
		if (!(self.level() instanceof ServerLevel level)) {
			return;
		}
		if (!(self.getOwner() instanceof LivingEntity owner)) {
			return;
		}
		TridentPullSupport.tick(level, owner, self);
	}
}
