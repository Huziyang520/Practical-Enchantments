// 风王结界的横扫伤害翻倍：横扫命中的目标在主目标之外，按同一挥击窗口识别。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.weapon.InvisibleAirEnchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class SweepDamageMixin {
	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, index = 3, require = 1)
	private float practicalenchantments$doubleSweepDamage(float amount, ServerLevel level, DamageSource source) {
		if (!(source.getEntity() instanceof Player player)) {
			return amount;
		}
		if (!SweepWindow.isSweepHit(player, (LivingEntity) (Object) this)) {
			return amount;
		}
		return InvisibleAirEnchantment.hasInMainHand(player) ? amount * 2.0F : amount;
	}
}
