// 鱼死网破：穿戴者死亡时以尸体为中心爆炸一次，炸死村民记进度。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.advancement.PeAdvancementTriggers;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.content.AmuletSupport;
import com.practicalenchantments.enchantment.support.KillMarks;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public final class MutualDestructionEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":mutual_destruction";
	private static Holder<Enchantment> HOLDER;

	private MutualDestructionEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
	}

	public static void onDeath(LivingEntity victim, net.minecraft.world.damagesource.DamageSource source) {
		if (!(victim.level() instanceof ServerLevel level) || HOLDER == null) {
			return;
		}
		if (KillMarks.consumeMutualFired(victim.getUUID())) {
			return;
		}
		if (AmuletSupport.wornLevel(victim, HOLDER) <= 0) {
			return;
		}
		KillMarks.markMutualFired(victim.getUUID());
		ServerPlayer player = victim instanceof ServerPlayer serverPlayer ? serverPlayer : null;
		List<Villager> villagers = level.getEntitiesOfClass(Villager.class, victim.getBoundingBox().inflate(10.0D));
		level.getServer().execute(() -> explode(level, victim, player, villagers));
	}

	private static void explode(ServerLevel level, LivingEntity victim, ServerPlayer player, List<Villager> villagers) {
		if (!level.isLoaded(victim.blockPosition())) {
			return;
		}
		Level.ExplosionInteraction interaction = Tunables.mutualDestructionBlockDestruction()
				? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
		level.explode(victim, victim.getX(), victim.getY(), victim.getZ(),
				(float) Tunables.mutualDestructionExplosionPower(), Tunables.mutualDestructionFire(), interaction);
		if (player == null) {
			return;
		}
		for (Villager villager : villagers) {
			if (villager.isDeadOrDying()) {
				PeAdvancementTriggers.mutualDestructionVillager(player);
				break;
			}
		}
	}
}
