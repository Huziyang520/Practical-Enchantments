// 玩家层覆写了同名方法，不单独拦则玩家第三人称仍会显示剑身。

package com.practicalenchantments.neoforge.client.mixin;

import com.practicalenchantments.content.InvisibleAirRender;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
public class InvisibleAirPlayerItemInHandMixin {
	@Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
			at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$hideHeldItem(AvatarRenderState state, ItemStackRenderState renderState,
			ItemStack stack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light,
			CallbackInfo info) {
		if (InvisibleAirRender.isHidden(stack)) {
			info.cancel();
		}
	}
}
