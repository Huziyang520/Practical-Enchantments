// 焚灭：击杀生物无战利品掉落、经验值 ×2，并让史莱姆类死后不再分裂。剑、斧、矛 Ⅰ 级。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.support.KillMarks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

public final class IncinerateEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":incinerate";
	public static final String GROUP_NAME = "looting";
	public static final String GROUP_TAG = "#" + PracticalEnchantments.MOD_ID + ":exclusive_set/" + GROUP_NAME;

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_KILL, IncinerateEnchantment::onPostKill);
	}

	private static void onPostKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) return;
		LivingEntity victim = event.victim();
		if (victim instanceof ServerPlayer) return;
		KillMarks.markIncinerated(victim);
		MerlinApi.drops().suppressDrops(victim);
		int baseXp = victim.getExperienceReward(event.level(), player);
		if (baseXp > 0) {
			player.giveExperiencePoints(baseXp);
		}
	}
}
