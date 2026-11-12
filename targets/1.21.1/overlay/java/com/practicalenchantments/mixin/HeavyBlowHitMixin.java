// 重击的命中记录：受伤真正生效时记一次，未命中由攻击开头清掉。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.weapon.HeavyBlowEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HeavyBlowHitMixin {
	@Inject(method = "hurt", at = @At("RETURN"), require = 1)
	private void practicalenchantments$markHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		if (!info.getReturnValueZ() || !(source.getDirectEntity() instanceof Player player)) {
			return;
		}
		if (HeavyBlowEnchantment.has(player)) {
			HeavyBlowEnchantment.markSwing(player, true);
		}
	}
}
