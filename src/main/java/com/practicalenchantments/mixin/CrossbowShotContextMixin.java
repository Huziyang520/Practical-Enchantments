// 1.20.1 的弩在 performShooting 里发射，同样把武器暂存进 ShotContext。

package com.practicalenchantments.mixin;

import com.practicalenchantments.support.ShotContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public abstract class CrossbowShotContextMixin {
	@Inject(method = "performShooting", at = @At("HEAD"), require = 1)
	private static void practicalenchantments$beginShot(Level level, LivingEntity shooter, InteractionHand hand,
														ItemStack stack, float velocity, float inaccuracy,
														CallbackInfo ci) {
		ShotContext.begin(stack);
	}

	@Inject(method = "performShooting", at = @At("RETURN"), require = 1)
	private static void practicalenchantments$endShot(Level level, LivingEntity shooter, InteractionHand hand,
													  ItemStack stack, float velocity, float inaccuracy,
													  CallbackInfo ci) {
		ShotContext.end();
	}
}
