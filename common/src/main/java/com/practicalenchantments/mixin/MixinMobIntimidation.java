// 铁傀儡的仇恨目标在 AI tick 结束后再清一次，覆盖旧声望导致的持续锁定。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.armor.IntimidationEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MixinMobIntimidation {
	@Inject(method = "tick", at = @At("TAIL"))
	private void pe$clearGolemTarget(CallbackInfo info) {
		if (!Tunables.intimidationPreventGolemAnger()) {
			return;
		}
		Mob self = (Mob) (Object) this;
		if (!(self instanceof IronGolem)) {
			return;
		}
		LivingEntity target = self.getTarget();
		if (target instanceof ServerPlayer player && IntimidationEnchantment.protects(player)) {
			self.setTarget(null);
		}
	}
}
