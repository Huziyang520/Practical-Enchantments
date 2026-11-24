// 死亡时清理战斗标记层数；鱼死网破的爆炸也在这里触发。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.armor.MutualDestructionEnchantment;
import com.practicalenchantments.enchantment.support.CombatMarkTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntityDeath {
	@Inject(method = "die", at = @At("HEAD"))
	private void pe$onDeath(DamageSource source, CallbackInfo info) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!(self.level() instanceof ServerLevel level)) {
			return;
		}
		CombatMarkTracker.get(level).clear(self.getUUID());
		MutualDestructionEnchantment.onDeath(self, source);
	}
}
