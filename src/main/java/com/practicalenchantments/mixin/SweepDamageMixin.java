// 风王结界的横扫伤害翻倍：横扫循环里那次命中单独结算。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.InvisibleAirEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class SweepDamageMixin {
	@Redirect(method = "attack", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean practicalenchantments$doubleSweepDamage(LivingEntity victim, DamageSource source, float amount) {
		Player self = (Player) (Object) this;
		return victim.hurt(source, InvisibleAirEnchantment.hasInMainHand(self) ? amount * 2.0F : amount);
	}
}
