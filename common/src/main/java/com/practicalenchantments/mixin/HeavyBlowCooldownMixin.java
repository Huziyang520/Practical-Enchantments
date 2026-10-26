// 重击的攻击节奏：记下这一刀有没有打中，命中把冷却压到九成、未命中拉到一点六倍。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.weapon.HeavyBlowEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class HeavyBlowCooldownMixin {
	@Redirect(method = "attack", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean practicalenchantments$recordSwing(Entity target, DamageSource source, float amount) {
		boolean hurt = target.hurtOrSimulate(source, amount);
		Player self = (Player) (Object) this;
		if (HeavyBlowEnchantment.has(self)) {
			HeavyBlowEnchantment.markSwing(self, hurt);
		}
		return hurt;
	}

	@Inject(method = "getCurrentItemAttackStrengthDelay", at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$shiftCooldown(CallbackInfoReturnable<Float> info) {
		Player self = (Player) (Object) this;
		if (HeavyBlowEnchantment.has(self)) {
			info.setReturnValue(info.getReturnValueF() * HeavyBlowEnchantment.cooldownFactor(self));
		}
	}
}
