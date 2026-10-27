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
	@ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), argsOnly = true, require = 0)
	private ItemStack practicalenchantments$hideHeldStackOld(ItemStack stack) {
		return InvisibleAirRender.isHidden(stack) ? ItemStack.EMPTY : stack;
	}

	@ModifyVariable(method = "submitArmWithItem", at = @At("HEAD"), argsOnly = true, require = 0)
	private ItemStack practicalenchantments$hideHeldStack(ItemStack stack) {
		return InvisibleAirRender.isHidden(stack) ? ItemStack.EMPTY : stack;
	}
}
