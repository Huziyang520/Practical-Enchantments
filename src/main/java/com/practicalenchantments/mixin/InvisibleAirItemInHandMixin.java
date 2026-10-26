// 第三人称手持风王结界时不提交物品本体，手臂照常渲染（含生物手持）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.InvisibleAirRender;
import com.mojang.blaze3d.vertex.PoseStack;
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
	private void practicalenchantments$hideHeldItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context,
			HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo info) {
		if (InvisibleAirRender.isHidden(stack)) {
			info.cancel();
		}
	}
}
