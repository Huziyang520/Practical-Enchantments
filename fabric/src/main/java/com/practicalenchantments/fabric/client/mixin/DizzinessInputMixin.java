// 眩惑输入取反（Fabric 客户端）。

package com.practicalenchantments.fabric.client.mixin;

import com.practicalenchantments.client.DizzinessClient;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientInput.class)
public class DizzinessInputMixin {
	@Inject(method = "getMoveVector", at = @At("RETURN"), cancellable = true)
	private void pe$dizzy(CallbackInfoReturnable<Vec2> info) {
		Vec2 flipped = DizzinessClient.flip(info.getReturnValue());
		if (flipped != info.getReturnValue()) {
			info.setReturnValue(flipped);
		}
	}
}
