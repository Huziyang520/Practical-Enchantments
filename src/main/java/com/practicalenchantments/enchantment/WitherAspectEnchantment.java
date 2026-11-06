// 枯萎附魔 - 剑 Ⅱ 级（宝藏，专家级图书管理员）。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class WitherAspectEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":wither_aspect";

	private WitherAspectEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, WitherAspectEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		int duration = Tunables.secondsToTicks(ctx.level() == 1
					? Tunables.witherAspectSecondsL1() : Tunables.witherAspectSecondsL2());
		event.target().addEffect(new MobEffectInstance(MobEffects.WITHER, duration, 0));
		float penalty = (float) (ctx.level() == 1
					? Tunables.witherAspectDamagePenaltyL1() : Tunables.witherAspectDamagePenaltyL2());
		float afterPenalty = Math.max(0.0F, event.damage().getValue() - penalty);
		event.damage().setValue(afterPenalty);
	}
}
