// 威压：玩家直接伤害村民时取消声望损失。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.armor.IntimidationEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class MixinVillagerReputation {
	@Inject(method = "onReputationEventFrom", at = @At("HEAD"), cancellable = true)
	private void pe$noReputationLoss(ReputationEventType type, Entity source, CallbackInfo info) {
		if (!Tunables.intimidationPreventReputationLoss()) {
			return;
		}
		if (type != ReputationEventType.VILLAGER_HURT && type != ReputationEventType.VILLAGER_KILLED) {
			return;
		}
		if (source instanceof ServerPlayer player && IntimidationEnchantment.protects(player)) {
			info.cancel();
		}
	}
}
