// 狂暴附魔 - 剑/斧/矛 Ⅱ 级（宝藏，大师级图书管理员）。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.Enchantment;

public final class FrenzyEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":frenzy";

	private FrenzyEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_KILL, FrenzyEnchantment::onKill);
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) {
			return;
		}

		int baseSeconds = ctx.level() == 1 ? 6 : 8;
		boolean axe = player.getMainHandItem().is(ItemTags.AXES);
		int duration = baseSeconds * (axe ? 2 : 1) * 20;
		int amplifier = ctx.level();
		player.addEffect(new MobEffectInstance(MobEffects.SPEED, duration, amplifier, true, true));
	}
}
