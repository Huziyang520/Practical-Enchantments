package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.AerialHasteEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 浮空速掘：消除空中挖掘速度惩罚。
 */
@Mixin(Player.class)
public abstract class PlayerGetDestroySpeedMixin {

	@Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
	private void practicalenchantments$cancelAirbornePenalty(BlockState state, CallbackInfoReturnable<Float> cir) {
		@SuppressWarnings("DataFlowIssue")
		Player self = (Player) (Object) this;

		// 仅当玩家在空中时才存在 ÷5 惩罚
		if (self.onGround()) {
			return;
		}

		// 头盔佩戴浮空速掘附魔时，乘回 5 抵消惩罚
		if (hasAerialHaste(self)) {
			cir.setReturnValue(cir.getReturnValueF() * 5.0f);
		}
	}

	private static boolean hasAerialHaste(Player player) {
		Holder<Enchantment> holder = AerialHasteEnchantment.HOLDER;
		if (holder == null) {
			return false;
		}
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		if (helmet.isEmpty()) {
			return false;
		}
		return helmet.getEnchantments().getLevel(holder) > 0;
	}
}
