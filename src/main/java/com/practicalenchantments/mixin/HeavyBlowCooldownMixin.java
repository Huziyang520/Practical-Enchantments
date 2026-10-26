// 重击的攻击节奏：命中与未命中的冷却倍率。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.HeavyBlowEnchantment;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class HeavyBlowCooldownMixin {
	@Inject(method = "getCurrentItemAttackStrengthDelay", at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$shiftCooldown(CallbackInfoReturnable<Float> info) {
		Player self = (Player) (Object) this;
		if (HeavyBlowEnchantment.has(self)) {
			info.setReturnValue(info.getReturnValueF() * HeavyBlowEnchantment.cooldownFactor(self));
		}
	}
}
