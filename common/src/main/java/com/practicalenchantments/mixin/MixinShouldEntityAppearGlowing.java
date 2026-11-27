// 客户端渲染判定：只让本机追踪到的实体走原版发光描边。

package com.practicalenchantments.mixin;

import com.practicalenchantments.client.EyeOfJudgmentClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinShouldEntityAppearGlowing {
	@Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
	private void pe$eyeOfJudgmentOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (EyeOfJudgmentClient.shouldOutline(entity)) {
			cir.setReturnValue(true);
		}
	}
}
