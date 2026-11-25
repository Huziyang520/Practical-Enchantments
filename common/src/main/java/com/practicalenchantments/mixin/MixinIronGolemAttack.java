// 铁傀儡重写了 doHurtTarget，Mob 上的拦截不会生效，必须在 IronGolem 自己的方法上取消。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.armor.IntimidationEnchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public class MixinIronGolemAttack {
	@Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
	private void pe$cancelAttackOnIntimidatingPlayer(ServerLevel level, Entity target,
													 CallbackInfoReturnable<Boolean> info) {
		if (!Tunables.intimidationPreventGolemAnger()) {
			return;
		}
		if (target instanceof ServerPlayer player && IntimidationEnchantment.protects(player)) {
			info.setReturnValue(false);
		}
	}
}
