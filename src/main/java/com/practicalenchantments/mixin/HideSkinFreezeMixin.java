// 覆皮：身上任意一件带覆皮的装备即可免疫冰冻。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.HideSkinEnchantment;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HideSkinFreezeMixin {
	@Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$canFreeze(CallbackInfoReturnable<Boolean> cir) {
		if (HideSkinEnchantment.wears((LivingEntity) (Object) this)) {
			cir.setReturnValue(false);
		}
	}
}
