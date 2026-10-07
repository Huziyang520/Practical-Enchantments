package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 淬毒附魔 - 剑 Ⅱ 级（附魔台可出，专家级图书管理员）。
 *
 * <p>命中时附加中毒：Ⅰ 级中毒 I 6 秒，Ⅱ 级中毒 II 12 秒。
 * 与「火焰附加」「枯萎」三向互斥（blade_effect 组）。亡灵生物免疫中毒为原版机制。</p>
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 * <ol>
 * <li><b>{@code registerCallbacks} 去掉 {@code HolderLookup.Provider} 参数</b>（原因见
 * {@code AerialHasteEnchantment} 类注释第 1 条）：1.20.1 用
 * {@code PracticalEnchantments.resolveEnchantment(ID)} 直接拿对象。</li>
 *
 * <li><b>{@code Holder<Enchantment>} → {@code Enchantment}</b>：注册处不再有 Holder 间接层，
 * {@code EnchantmentEventRegistrar.register(Enchantment, ...)} 收的就是普通附魔对象
 * （它另有一个收 {@code RegistryObject<Enchantment>} 的重载，本类不需要）。</li>
 *
 * <li><b>没有其它改动。</b>{@code MobEffects.POISON}、
 * {@code MobEffectInstance(MobEffect, int, int)}、{@code event.target().addEffect(...)}、
 * {@code ctx.level()} 在 1.20.1 上同名同形，事件记录 {@code BuiltInEvents.ModifyDamageEvent} 的
 * {@code target()} 访问器也未变。本类<b>除签名外是逐字直接移植</b>。</li>
 * </ol>
 */
public final class VenomEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":venom";

	/** 互斥组：淬毒 / 枯萎 / 火焰附加 */

	private VenomEnchantment() {
	}


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, VenomEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		int duration = ctx.level() == 1 ? 6 * 20 : 12 * 20;
		event.target().addEffect(new MobEffectInstance(MobEffects.POISON, duration, ctx.level() - 1));
	}
}