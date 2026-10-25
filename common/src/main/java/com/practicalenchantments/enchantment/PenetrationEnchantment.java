// 贯穿附魔 - 三叉戟 Ⅴ 级（附魔台可出，不可交易）。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

public final class PenetrationEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":penetration";

	private PenetrationEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.PROJECTILE_HIT, PenetrationEnchantment::onProjectileHit);
	}

	private static void onProjectileHit(BuiltInEvents.ProjectileHitEvent event, EnchantmentContext ctx) {
		if (!event.weapon().is(Items.TRIDENT)) {
			return;
		}
		float extra = 1.5F + 0.5F * (ctx.level() - 1);
		event.target().damageCooldownTime = 0;
		event.target().hurtServer(event.level(),
			event.attacker().damageSources().mobAttack(event.attacker()), extra);

		if (DeathSaveSupport.getLevelFull(event.weapon(), "minecraft:loyalty") > 0) {
			TridentPullSupport.captureMob(event.attacker().getUUID(), event.target());
		}
	}
}
