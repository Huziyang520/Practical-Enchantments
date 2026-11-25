// 狙击手：超过 50 格才生效，每再远 50 格加一档，封顶可配置。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;

public final class SniperEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":sniper";

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, SniperEnchantment::onDamage);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		if (!MerlinApi.combat().isPlayerArrowShot(event.source())) {
			return;
		}
		double distance = MerlinApi.combat().horizontalDistance(event.attacker(), event.target());
		if (distance <= Tunables.sniperMinDistance()) {
			return;
		}
		double bonus = Tunables.sniperBonusPercent(ctx.level(), distance) / 100.0D;
		event.damage().setValue(event.damage().floatValue() * (float) (1.0D + bonus));
	}
}
