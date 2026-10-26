// 第三人称手持风王结界时不提交物品本体，手臂照常渲染。

package com.practicalenchantments.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.practicalenchantments.content.InvisibleAirRender;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class InvisibleAirItemInHandMixin {
	@Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$hideHeldItem(LivingEntity entity, ItemStack stack,
			ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, CallbackInfo info) {
		if (InvisibleAirRender.isHidden(stack)) {
			info.cancel();
		}
	}
}
