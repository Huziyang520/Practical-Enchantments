// 第三人称手持风王结界的玩家手臂按空手姿势，不因握持而前抬。

package com.practicalenchantments.fabric.client.mixin;

import com.practicalenchantments.content.InvisibleAirRender;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public class InvisibleAirArmPoseMixin {
	@Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
			at = @At("HEAD"), cancellable = true, require = 1)
	private static void practicalenchantments$relaxArm(Avatar avatar, ItemStack stack, InteractionHand hand,
			CallbackInfoReturnable<HumanoidModel.ArmPose> info) {
		if (InvisibleAirRender.isHidden(stack)) {
			info.setReturnValue(HumanoidModel.ArmPose.EMPTY);
		}
	}
}
