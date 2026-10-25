// 汲灵附魔 - 剑/斧/矛 Ⅴ 级（附魔台可出，老手级图书管理员）。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.entity.ExperienceOrb;

public final class SoulSiphonEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":soul_siphon";
	private static final float COEFFICIENT_PER_LEVEL = 0.2F;
	private static final float CAP_PER_LEVEL = 2.0F;

	private SoulSiphonEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, SoulSiphonEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		float raw = Math.max(0.0F, event.originalDamage());
		int xp = Math.round(Math.min(raw * COEFFICIENT_PER_LEVEL * ctx.level(), CAP_PER_LEVEL * ctx.level()));
		if (xp <= 0) {
			return;
		}
		var target = event.target();
		ExperienceOrb orb = new ExperienceOrb(event.level(),
			target.getX(), target.getY() + 0.5, target.getZ(), xp);
		event.level().addFreshEntity(orb);
	}
}
