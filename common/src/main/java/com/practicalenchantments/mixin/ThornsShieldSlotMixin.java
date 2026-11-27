// 「刺棘可用于盾」开启时，盾上的刺棘只在举盾受击时参与结算。

package com.practicalenchantments.mixin;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.config.VanillaTweaks;
import com.practicalenchantments.enchantment.support.ThornsShieldSupport;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class ThornsShieldSlotMixin {
	private static final String THORNS_KEY = "enchantment.minecraft.thorns";

	@Inject(method = "matchingSlot", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$shieldThornsRequireBlocking(EquipmentSlot slot,
			CallbackInfoReturnable<Boolean> cir) {
		if (slot != EquipmentSlot.OFFHAND && slot != EquipmentSlot.MAINHAND) {
			return;
		}
		if (!ThornsShieldSupport.active() || !VanillaTweaks.thornsShield()) {
			return;
		}
		if (!THORNS_KEY.equals(MerlinApi.enchantmentText().key((Enchantment) (Object) this))) {
			return;
		}
		if (ThornsShieldSupport.shouldSkipShieldThorns(slot)) {
			cir.setReturnValue(false);
		}
	}
}
