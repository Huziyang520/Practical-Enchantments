// 护身符的附魔惩罚：铁七五折、金对折、钻石两五折、下界合金只收零头，永远不会出现「过于昂贵」。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AmuletPenaltyMixin {
	@Shadow
	private DataSlot cost;

	@Shadow
	public abstract int getCost();

	@Inject(method = "createResult", at = @At("TAIL"), require = 1)
	private void practicalenchantments$reduceAmuletPenalty(CallbackInfo info) {
		AnvilMenu self = (AnvilMenu) (Object) this;
		ItemStack input = self.getSlot(0).getItem();
		ItemStack addition = self.getSlot(1).getItem();
		ItemStack amulet = AmuletSupport.isAmulet(input) ? input : (AmuletSupport.isAmulet(addition) ? addition : ItemStack.EMPTY);
		if (amulet.isEmpty()) {
			return;
		}
		int current = this.getCost();
		if (current <= 0) {
			return;
		}
		this.cost.set(Math.max(1, Math.round(current * AmuletSupport.penaltyFactor(amulet))));
	}
}
