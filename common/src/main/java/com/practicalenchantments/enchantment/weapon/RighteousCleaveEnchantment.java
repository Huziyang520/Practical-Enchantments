// 义斩：与仁击对称，消耗“不义”加伤，然后叠“不仁”。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.support.CombatMarkTracker;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;

public final class RighteousCleaveEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":righteous_cleave";

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, RighteousCleaveEnchantment::onDamage);
		registrar.register(holder, BuiltInEvents.POST_ATTACK, RighteousCleaveEnchantment::onAttack);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		CombatMarkTracker tracker = CombatMarkTracker.get(event.level());
		if (tracker.benevolent(event.target().getUUID()) <= 0) {
			return;
		}
		if (tracker.consumeBenevolent(event.target().getUUID())) {
			double bonus = Tunables.righteousDamageBonusPercent() / 100.0D;
			event.damage().setValue(event.damage().floatValue() * (float) (1.0D + bonus));
		}
	}

	private static void onAttack(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		if (event.target().isDeadOrDying()) {
			return;
		}
		CombatMarkTracker.get(event.level()).addRighteous(event.target().getUUID(),
				Tunables.righteousMaxLayers());
	}
}
