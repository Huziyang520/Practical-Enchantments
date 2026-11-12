// 风王结界的横扫伤害翻倍：横扫循环里那次命中单独结算。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.weapon.InvisibleAirEnchantment;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class SweepDamageMixin {
	// 只改横扫那一下的伤害参数，不动调用点描述符。
	@ModifyArg(method = "attack", require = 1, index = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private float practicalenchantments$doubleSweepDamage(float amount) {
		Player self = (Player) (Object) this;
		return InvisibleAirEnchantment.hasInMainHand(self) ? amount * (float) Tunables.invisibleAirSweepFactor() : amount;
	}
}
