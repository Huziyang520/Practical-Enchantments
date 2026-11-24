// 落矢：方块与实体命中都要触发，不能只用 ML 的实体命中事件。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.weapon.ArrowfallEnchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public class MixinArrowfallProjectileHit {
	@Inject(method = "onHit", at = @At("TAIL"))
	private void pe$arrowfall(HitResult hitResult, CallbackInfo info) {
		if (!(((Projectile) (Object) this) instanceof AbstractArrow arrow)) {
			return;
		}
		if (arrow.level() instanceof ServerLevel level) {
			ArrowfallEnchantment.onHit(level, arrow, hitResult);
		}
	}
}
