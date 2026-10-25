// 枯萎附魔 - 剑 Ⅱ 级（宝藏，专家级图书管理员）。

package com.practicalenchantments.enchantment;

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
		int duration = ctx.level() == 1 ? 6 * 20 : 12 * 20;
		event.target().addEffect(new MobEffectInstance(MobEffects.WITHER, duration, 0));
		float penalty = ctx.level() == 1 ? 1.0F : 0.5F;
		float afterPenalty = Math.max(0.0F, event.damage().getValue() - penalty);
		event.damage().setValue(afterPenalty);
	}
}
