package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 淬毒附魔 - 剑 Ⅱ 级（附魔台可出，专家级图书管理员）。
 *
 * <p>命中时附加中毒：Ⅰ 级中毒 I 6 秒，Ⅱ 级中毒 II 12 秒。
 * 与「火焰附加」「枯萎」三向互斥（blade_effect 组）。亡灵生物免疫中毒为原版机制。</p>
 */
public final class VenomEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":venom";

	/** 互斥组：淬毒 / 枯萎 / 火焰附加 */

	private VenomEnchantment() {
	}


	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, VenomEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		int duration = ctx.level() == 1 ? 6 * 20 : 12 * 20;
		event.target().addEffect(new MobEffectInstance(MobEffects.POISON, duration, ctx.level() - 1));
	}
}
