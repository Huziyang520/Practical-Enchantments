// 仁击：打有“不仁”层数的目标加伤并消耗一层，然后再叠“不义”。

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

public final class BenevolentStrikeEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":benevolent_strike";

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, BenevolentStrikeEnchantment::onDamage);
		registrar.register(holder, BuiltInEvents.POST_ATTACK, BenevolentStrikeEnchantment::onAttack);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		CombatMarkTracker tracker = CombatMarkTracker.get(event.level());
		if (tracker.righteous(event.target().getUUID()) <= 0) {
			return;
		}
		if (tracker.consumeRighteous(event.target().getUUID())) {
			double bonus = Tunables.benevolentDamageBonusPercent() / 100.0D;
			event.damage().setValue(event.damage().floatValue() * (float) (1.0D + bonus));
		}
	}

	private static void onAttack(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		if (event.target().isDeadOrDying()) {
			return;
		}
		CombatMarkTracker.get(event.level()).addBenevolent(event.target().getUUID(),
				Tunables.benevolentMaxLayers());
	}
}
