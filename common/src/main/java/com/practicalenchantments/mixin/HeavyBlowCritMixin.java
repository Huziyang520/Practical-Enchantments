// 重击的暴击强制：原版暴击判定返回 false（或没有暴击条件）时改成必定暴击。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.weapon.HeavyBlowEnchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class HeavyBlowCritMixin {
	@Inject(method = "canCriticalAttack", at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$alwaysCritical(Entity target, CallbackInfoReturnable<Boolean> info) {
		if (!info.getReturnValueZ() && HeavyBlowEnchantment.has((Player) (Object) this)) {
			info.setReturnValue(true);
		}
	}
}
