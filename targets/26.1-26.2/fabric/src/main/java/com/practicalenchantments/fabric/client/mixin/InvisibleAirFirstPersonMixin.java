// 第一人称手持风王结界时按空手渲染，武器不进入视野。

package com.practicalenchantments.fabric.client.mixin;

import com.practicalenchantments.content.InvisibleAirRender;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemInHandRenderer.class)
public class InvisibleAirFirstPersonMixin {
	@ModifyVariable(method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
			at = @At("HEAD"), argsOnly = true)
	private ItemStack practicalenchantments$hideHeldStack(ItemStack stack) {
		return InvisibleAirRender.isHidden(stack) ? ItemStack.EMPTY : stack;
	}
}
