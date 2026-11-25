// ServerPlayer.die 是独立实现，不会走 LivingEntity.die；玩家死亡效果必须在这里触发。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.armor.MutualDestructionEnchantment;
import com.practicalenchantments.enchantment.support.CombatMarkTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class MixinServerPlayerDeath {
	@Inject(method = "die", at = @At("TAIL"))
	private void pe$onPlayerDeath(DamageSource source, CallbackInfo info) {
		ServerPlayer self = (ServerPlayer) (Object) this;
		if (!(self.level() instanceof ServerLevel level)) {
			return;
		}
		CombatMarkTracker.get(level).clear(self.getUUID());
		MutualDestructionEnchantment.onDeath(self, source);
	}
}
