package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 枯萎附魔 - 剑 Ⅱ 级（宝藏，专家级图书管理员）。
 *
 * <p>命中时附加凋零：Ⅰ 级 6 秒，Ⅱ 级 12 秒（凋零可对亡灵生效）。
 * 武器基础伤害被削弱：Ⅰ 级 -1 点，Ⅱ 级 -0.5 点（等级越高代价越小）。
 * 与「火焰附加」「淬毒」三向互斥。</p>
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 * <ol>
 * <li><b>{@code registerCallbacks} 去掉 {@code HolderLookup.Provider} 参数</b>（原因见
 * {@code AerialHasteEnchantment} 类注释第 1 条），改用
 * {@code PracticalEnchantments.resolveEnchantment(ID)}。</li>
 *
 * <li><b>{@code Holder<Enchantment>} → {@code Enchantment}</b>：见
 * {@code VenomEnchantment} 类注释第 2 条。</li>
 *
 * <li><b>没有其它改动。</b>{@code MobEffects.WITHER} 在 1.20.1 上就叫这个名字（注意与之相邻的
 * {@code MobEffects.SPEED} 在 1.20.1 已改名 {@code MOVEMENT_SPEED}，但本类不涉及），
 * {@code MutableFloat.getValue()/setValue(float)}、{@code Math.max} 均同形。
 * 本类<b>除签名外是逐字直接移植</b>。</li>
 * </ol>
 */
public final class WitherAspectEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":wither_aspect";

	private WitherAspectEnchantment() {
	}


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, WitherAspectEnchantment::onHit);
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