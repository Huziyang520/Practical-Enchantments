// 连弩：上弦速度按百分比缩短，−(15 − 3 × 等级)%，等级越高缩得越少，直到 5 级不再缩短。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.EnchantmentDefinitions;
import com.practicalenchantments.support.EnchantIds;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowChargeTimeMixin {
	@Inject(method = "getChargeDuration", at = @At("RETURN"), cancellable = true)
	private static void practicalenchantments$shortenCharge(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		int level = EnchantIds.levelOf(stack, EnchantmentDefinitions.REPEATING_CROSSBOW.toString());
		if (level <= 0) {
			return;
		}
		int percent = Math.max(0, 15 - 3 * level);
		if (percent <= 0) {
			return;
		}
		cir.setReturnValue(Math.max(1, cir.getReturnValue() * (100 - percent) / 100));
	}
}
