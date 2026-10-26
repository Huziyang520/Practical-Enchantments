// 第三人称手持风王结界的玩家手臂按空手姿势，不因握持而前抬。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.InvisibleAirRender;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class InvisibleAirArmPoseMixin {
	@Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true, require = 1)
	private static void practicalenchantments$relaxArm(AbstractClientPlayer player, InteractionHand hand,
			CallbackInfoReturnable<HumanoidModel.ArmPose> info) {
		ItemStack stack = player.getItemInHand(hand);
		if (InvisibleAirRender.isHidden(stack)) {
			info.setReturnValue(HumanoidModel.ArmPose.EMPTY);
		}
	}
}
