// 护身符的附魔惩罚：先将历史工作惩罚压到极低，再按等级折算总花费，并且永远不会出现「过于昂贵」。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.core.component.DataComponents;
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
	private static final int COST_FAIL = 40;
	private static boolean practical$recomputing;

	@Shadow
	private DataSlot cost;

	@Shadow
	public abstract int getCost();

	@Inject(method = "createResult", at = @At("HEAD"), require = 1)
	private void practicalenchantments$shrinkAmuletRepairCost(CallbackInfo info) {
		if (practical$recomputing) {
			return;
		}
		AnvilMenu self = (AnvilMenu) (Object) this;
		for (int slot = 0; slot <= 1; slot++) {
			ItemStack stack = self.getSlot(slot).getItem();
			if (!AmuletSupport.isAmulet(stack)) {
				continue;
			}
			int base = stack.getOrDefault(DataComponents.REPAIR_COST, 0);
			int cap = AmuletSupport.repairCostCap(stack);
			if (base > cap) {
				stack.set(DataComponents.REPAIR_COST, cap);
			}
		}
	}

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
		int scaled = Math.min(COST_FAIL - 1, Math.max(1, Math.round(current * AmuletSupport.penaltyFactor(amulet))));
		if (scaled == current) {
			return;
		}
		this.cost.set(scaled);
		if (self.getSlot(2).getItem().isEmpty() && !practical$recomputing) {
			practical$recomputing = true;
			try {
				self.createResult();
			} finally {
				practical$recomputing = false;
			}
		}
	}
}
