// 威压：村民不记住攻击者，避免触发铁傀儡愤怒。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.armor.IntimidationEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class MixinVillagerHurtBy {
	@Inject(method = "setLastHurtByMob", at = @At("HEAD"), cancellable = true)
	private void pe$forgetAttacker(LivingEntity attacker, CallbackInfo info) {
		if (!Tunables.intimidationPreventGolemAnger()) {
			return;
		}
		if (attacker instanceof ServerPlayer player && IntimidationEnchantment.protects(player)) {
			info.cancel();
		}
	}
}
