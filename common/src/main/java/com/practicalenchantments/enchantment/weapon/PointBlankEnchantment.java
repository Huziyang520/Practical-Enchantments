// 临场反应：8 格内箭伤按等级放大，与力量同乘区。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;

public final class PointBlankEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":point_blank";

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, PointBlankEnchantment::onDamage);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		if (!ProjectileRangeSupport.isArrowShot(event.source())) {
			return;
		}
		double distance = ProjectileRangeSupport.horizontalDistance(event.attacker(), event.target());
		if (distance > Tunables.pointBlankMaxDistance()) {
			return;
		}
		double bonus = Tunables.pointBlankBonusPercent(ctx.level()) / 100.0D;
		event.damage().setValue(event.damage().floatValue() * (float) (1.0D + bonus));
	}
}
