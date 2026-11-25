// DefendVillageTargetGoal 直接看声望，不看 lastHurtByMob；在选目标时跳过威压穿戴者。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.armor.IntimidationEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefendVillageTargetGoal.class)
public class MixinDefendVillageTargetGoal {
	@Shadow
	private LivingEntity potentialTarget;

	@Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
	private void pe$skipIntimidatingPlayer(CallbackInfoReturnable<Boolean> info) {
		if (info.getReturnValueZ() && this.potentialTarget instanceof ServerPlayer player
				&& IntimidationEnchantment.protects(player)) {
			info.setReturnValue(false);
		}
	}
}
