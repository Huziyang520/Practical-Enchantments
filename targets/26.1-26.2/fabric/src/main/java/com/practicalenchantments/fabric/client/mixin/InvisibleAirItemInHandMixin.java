// 第三人称手持风王结界时不提交物品本体，手臂照常渲染（生物手持走这一层）。

package com.practicalenchantments.fabric.client.mixin;

import com.practicalenchantments.content.InvisibleAirRender;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class InvisibleAirItemInHandMixin {
	@Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
			at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$hideHeldItem(ArmedEntityRenderState state, ItemStackRenderState renderState,
			ItemStack stack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light,
			CallbackInfo info) {
		if (InvisibleAirRender.isHidden(stack)) {
			info.cancel();
		}
	}
}
