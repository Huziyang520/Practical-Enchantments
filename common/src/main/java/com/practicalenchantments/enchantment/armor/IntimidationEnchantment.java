// 威压：穿戴者打村民不进声望、不记仇；打铁傀儡触发进度。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.advancement.PeAdvancementTriggers;
import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.item.enchantment.Enchantment;

public final class IntimidationEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":intimidation";
	private static Holder<Enchantment> HOLDER;

	private IntimidationEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(HOLDER, BuiltInEvents.POST_ATTACK, IntimidationEnchantment::onAttack);
	}

	public static boolean protects(LivingEntity entity) {
		return HOLDER != null && entity instanceof ServerPlayer player
				&& AmuletSupport.wornLevel(player, HOLDER) > 0;
	}

	private static void onAttack(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		if (event.attacker() instanceof ServerPlayer player && event.target() instanceof IronGolem
				&& ctx.level() > 0) {
			PeAdvancementTriggers.intimidateIronGolem(player);
		}
	}
}
