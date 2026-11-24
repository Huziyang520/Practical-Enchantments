// 裁决：每次命中计数，计数内按概率打出 200 点直伤；致死时必掉头颅。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.support.HeadDrops;
import com.practicalenchantments.enchantment.support.KillMarks;
import com.practicalenchantments.enchantment.support.VerdictTracker;
import com.practicalenchantments.support.ImmunityReset;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

public final class VerdictEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":verdict";
	private static final Identifier DAMAGE_TYPE = Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "verdict");

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_ATTACK, VerdictEnchantment::onAttack);
		registrar.register(holder, BuiltInEvents.POST_KILL, VerdictEnchantment::onKill);
	}

	private static void onAttack(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		if (!(event.attacker() instanceof ServerPlayer player)) {
			return;
		}
		LivingEntity target = event.target();
		if (target.isDeadOrDying() || VerdictTracker.isApplying(target.getUUID())) {
			return;
		}
		if (target instanceof ServerPlayer victim && victim.isCreative()) {
			return;
		}
		VerdictTracker tracker = VerdictTracker.get(event.level());
		int maxHits = Tunables.verdictTriggerHits(ctx.level());
		if (tracker.hits(player.getUUID(), target.getUUID()) >= maxHits) {
			return;
		}
		int hits = tracker.record(player.getUUID(), target.getUUID());
		double chance = Tunables.verdictChancePercent(ctx.level());
		if (player.getRandom().nextDouble() * 100.0D >= chance) {
			return;
		}
		VerdictTracker.markApplying(target.getUUID());
		try {
			if (Tunables.verdictHeadDrop()) {
				KillMarks.markVerdictPending(target);
			}
			ImmunityReset.zero(target);
			target.hurtServer(event.level(), source(event.level(), player), (float) Tunables.verdictDamage());
			if (!target.isDeadOrDying()) {
				KillMarks.clearVerdictPending(target);
			}
		} finally {
			VerdictTracker.clearApplying(target.getUUID());
		}
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		LivingEntity victim = event.victim();
		ServerPlayer killer = event.killer() instanceof ServerPlayer player ? player : null;
		if (KillMarks.isVerdictPending(victim)) {
			KillMarks.clearVerdictPending(victim);
			KillMarks.markVerdictClaimed(victim);
			if (Tunables.verdictHeadDrop() && killer != null) {
				HeadDrops.drop(event.level(), victim, killer);
			}
		}
		VerdictTracker.get(event.level()).clearTarget(victim.getUUID());
	}

	private static DamageSource source(net.minecraft.server.level.ServerLevel level, ServerPlayer player) {
		Holder<DamageType> type = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
				.getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, DAMAGE_TYPE));
		return new DamageSource(type, player);
	}
}
