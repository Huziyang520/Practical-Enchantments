package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 狂暴附魔 - 剑/斧/矛 Ⅱ 级（宝藏，大师级图书管理员）。
 */
public final class FrenzyEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":frenzy";

	private FrenzyEnchantment() {
	}


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.POST_KILL, FrenzyEnchantment::onKill);
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) {
			return;
		}
		// Ⅰ:6s/II，Ⅱ:8s/III；斧翻倍
		int baseSeconds = ctx.level() == 1 ? 6 : 8;
		boolean axe = player.getMainHandItem().is(ItemTags.AXES);
		int duration = baseSeconds * (axe ? 2 : 1) * 20;
		int amplifier = ctx.level(); // Ⅰ→amp1（速度 II），Ⅱ→amp2（速度 III）
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier, true, true));
	}
}