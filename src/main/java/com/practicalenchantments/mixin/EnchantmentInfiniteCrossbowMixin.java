// 无限可以被附到弩上：canEnchant 的默认实现只查 EnchantmentCategory。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.VanillaTweaks;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentInfiniteCrossbowMixin {
	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$allowInfinityOnCrossbow(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (!VanillaTweaks.infinityCrossbow() || !(stack.getItem() instanceof CrossbowItem)) {
			return;
		}
		if ((Object) this == Enchantments.INFINITY_ARROWS) {
			cir.setReturnValue(true);
		}
	}
}
