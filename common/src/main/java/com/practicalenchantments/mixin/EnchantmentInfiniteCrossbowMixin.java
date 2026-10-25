// 无限可以被附到弩上：Enchantment#canEnchant(ItemStack) 只查 supported_items。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.VanillaTweaks;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentInfiniteCrossbowMixin {
	private static final String INFINITY_KEY = "enchantment.minecraft.infinity";

	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$allowInfinityOnCrossbow(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (!VanillaTweaks.infinityCrossbow() || !(stack.getItem() instanceof CrossbowItem)) {
			return;
		}
		Enchantment self = (Enchantment) (Object) this;
		if (self.description().getContents() instanceof TranslatableContents contents
				&& INFINITY_KEY.equals(contents.getKey())) {
			cir.setReturnValue(true);
		}
	}
}
