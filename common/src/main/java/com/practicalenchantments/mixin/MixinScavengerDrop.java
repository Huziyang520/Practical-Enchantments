// 收猎：死亡掉落生成期间打开捕获窗口。

package com.practicalenchantments.mixin;

import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinScavengerDrop {
	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void pe$beginCapture(ServerLevel level, DamageSource source, CallbackInfo info) {
		MerlinApi.drops().beginDeathDropCapture((LivingEntity) (Object) this);
	}

	@Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
	private void pe$endCapture(ServerLevel level, DamageSource source, CallbackInfo info) {
		MerlinApi.drops().endDeathDropCapture();
	}
}
