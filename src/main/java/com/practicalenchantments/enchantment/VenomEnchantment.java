// 淬毒附魔 - 剑 Ⅱ 级（附魔台可出，专家级图书管理员）。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class VenomEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":venom";

	private VenomEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, VenomEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		int duration = Tunables.secondsToTicks(ctx.level() == 1
					? Tunables.venomSecondsL1() : Tunables.venomSecondsL2());
		event.target().addEffect(new MobEffectInstance(MobEffects.POISON, duration, ctx.level() - 1));
	}
}
