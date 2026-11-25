// 收猎：只对玩家弓 / 弩射出的箭生效，把本次死亡掉落挪到脚下。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.support.KillMarks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ScavengerEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":scavenger";
	private static Holder<Enchantment> HOLDER;

	private ScavengerEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(HOLDER, BuiltInEvents.POST_KILL, ScavengerEnchantment::onKill);
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) {
			return;
		}
		if (event.victim() instanceof ServerPlayer || HOLDER == null) {
			return;
		}
		if (!(event.source().getDirectEntity() instanceof AbstractArrow arrow)) {
			return;
		}
		ItemStack weapon = arrow.getWeaponItem();
		if (weapon.isEmpty() || weapon.getEnchantments().getLevel(HOLDER) <= 0) {
			return;
		}
		if (KillMarks.isIncinerated(event.victim())) {
			MerlinApi.drops().cancelDeathDropMove(event.victim());
			return;
		}
		MerlinApi.drops().scheduleDeathDropMove(event.victim(), player, event.level(),
				Tunables.scavengerDelayTicks(), Tunables.scavengerRadius());
	}
}
