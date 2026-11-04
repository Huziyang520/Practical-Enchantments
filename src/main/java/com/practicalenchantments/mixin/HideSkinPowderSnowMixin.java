// 覆皮：附在靴子上时视同皮革靴，可站上细雪。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.HideSkinEnchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public abstract class HideSkinPowderSnowMixin {
	@Inject(method = "canEntityWalkOnPowderSnow", at = @At("RETURN"), cancellable = true, require = 1)
	private static void practicalenchantments$walkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ() || !(entity instanceof LivingEntity living)) {
			return;
		}
		if (HideSkinEnchantment.has(living.getItemBySlot(EquipmentSlot.FEET))) {
			cir.setReturnValue(true);
		}
	}
}
