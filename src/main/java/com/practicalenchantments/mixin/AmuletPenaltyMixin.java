// 护身符的附魔惩罚：铁七五折、金对折、钻石两五折、下界合金只收零头，永远不会出现「过于昂贵」。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class AmuletPenaltyMixin {
	@Inject(method = "getBaseRepairCost", at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$capAmuletPenalty(CallbackInfoReturnable<Integer> info) {
		ItemStack self = (ItemStack) (Object) this;
		if (!AmuletSupport.isAmulet(self)) {
			return;
		}
		int cap = AmuletSupport.repairCostCap(self);
		if (info.getReturnValueI() > cap) {
			info.setReturnValue(cap);
		}
	}
}
