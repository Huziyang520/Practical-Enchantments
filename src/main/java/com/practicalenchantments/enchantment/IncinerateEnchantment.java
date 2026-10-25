// 焚灭附魔 - 剑、斧、矛 I 级

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public final class IncinerateEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":incinerate";
	public static final String GROUP_NAME = "looting";

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.POST_KILL, IncinerateEnchantment::onPostKill);
	}

	private static void onPostKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) return;
		LivingEntity victim = event.victim();
		if (victim instanceof ServerPlayer) return;
		KillMarks.markIncinerated(victim);
		int baseXp = victim.getExperienceReward();
		if (baseXp > 0) {
			player.giveExperiencePoints(baseXp);
		}
	}
}
