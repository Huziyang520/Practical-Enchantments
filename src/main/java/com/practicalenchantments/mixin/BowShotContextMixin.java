// 1.20.1 的箭不记武器：弓弩射出期间把武器暂存进 ShotContext，供箭矢侧读取。

package com.practicalenchantments.mixin;

import com.practicalenchantments.support.ShotContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class BowShotContextMixin {
	@Inject(method = "releaseUsing", at = @At("HEAD"), require = 1)
	private void practicalenchantments$beginShot(ItemStack stack, Level level, LivingEntity entity, int timeLeft,
												 CallbackInfo ci) {
		ShotContext.begin(stack);
	}

	@Inject(method = "releaseUsing", at = @At("RETURN"), require = 1)
	private void practicalenchantments$endShot(ItemStack stack, Level level, LivingEntity entity, int timeLeft,
											   CallbackInfo ci) {
		ShotContext.end();
	}
}
