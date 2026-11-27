// 鱼死网破：穿戴者死亡时以尸体为中心爆炸一次；村民穿戴者炸开时记「生物兵器？」进度。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.advancement.PeAdvancementTriggers;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.content.AmuletSupport;
import com.practicalenchantments.enchantment.support.KillMarks;
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
		if (!(victim.level() instanceof ServerLevel level)) {
			return;
		}
		if (KillMarks.consumeMutualFired(victim.getUUID())) {
			return;
		}
		if (wornLevel(victim) <= 0) {
			return;
		}
		KillMarks.markMutualFired(victim.getUUID());
		ServerPlayer killer = source.getEntity() instanceof ServerPlayer player ? player : null;
		boolean villager = victim instanceof Villager;
		level.getServer().execute(() -> explode(level, victim, killer, villager));
	}

	private static int wornLevel(LivingEntity entity) {
		int level = HOLDER == null ? 0 : AmuletSupport.wornLevel(entity, HOLDER);
		// 数据包重载后 Holder 可能已不是物品里存的那份，再按 id 查一次。
		return level > 0 ? level : AmuletSupport.wornLevel(entity, ID);
	}

	private static void explode(ServerLevel level, LivingEntity victim, ServerPlayer killer, boolean villager) {
		if (!level.isLoaded(victim.blockPosition())) {
			return;
		}
		Level.ExplosionInteraction interaction = Tunables.mutualDestructionBlockDestruction()
				? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
		level.explode(victim, victim.getX(), victim.getY(), victim.getZ(),
				(float) Tunables.mutualDestructionExplosionPower(), Tunables.mutualDestructionFire(), interaction);
		if (villager && killer != null) {
			PeAdvancementTriggers.mutualDestructionVillager(killer);
		}
	}
}
