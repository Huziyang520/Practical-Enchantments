package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 枯萎附魔 - 剑 Ⅱ 级（宝藏，专家级图书管理员）。
 *
 * <p>命中时附加凋零：Ⅰ 级 6 秒，Ⅱ 级 12 秒（凋零可对亡灵生效）。
 * 武器基础伤害被削弱：Ⅰ 级 -1 点，Ⅱ 级 -0.5 点（等级越高代价越小）。
 * 与「火焰附加」「淬毒」三向互斥。</p>
 */
public final class WitherAspectEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":wither_aspect";

	private WitherAspectEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("枯萎")
			.supportedItems("#minecraft:swords")
			.weight(1)
			.maxLevel(2)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(4)
			.exclusiveSet(VenomEnchantment.EXCLUSIVE_SET)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, WitherAspectEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		// 凋零：Ⅰ 6 秒 / Ⅱ 12 秒
		int duration = ctx.level() == 1 ? 6 * 20 : 12 * 20;
		event.target().addEffect(new MobEffectInstance(MobEffects.WITHER, duration, 0));

		// 武器伤害惩罚：Ⅰ -1，Ⅱ -0.5
		float penalty = ctx.level() == 1 ? 1.0F : 0.5F;
		float afterPenalty = Math.max(0.0F, event.damage().getValue() - penalty);
		event.damage().setValue(afterPenalty);
	}
}
