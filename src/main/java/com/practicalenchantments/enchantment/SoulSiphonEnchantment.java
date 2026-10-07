package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.entity.ExperienceOrb;

/**
 * 汲灵附魔 - 剑/斧/矛 Ⅴ 级（附魔台可出，老手级图书管理员）。
 *
 * <p>每次命中按攻击的原始伤害（护甲减免前，含矛冲锋加成）产出经验球：</p>
 * <pre>经验 = round(min(原始伤害 × 0.2 × 等级, 2 × 等级))</pre>
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 * <ol>
 * <li><b>{@code registerCallbacks} 去掉 {@code HolderLookup.Provider} 参数</b>，改用
 * {@code PracticalEnchantments.resolveEnchantment(ID)}（原因见 {@code AerialHasteEnchantment}
 * 类注释第 1 条）。</li>
 *
 * <li><b>{@code Holder<Enchantment>} → {@code Enchantment}</b>：见 {@code VenomEnchantment}
 * 类注释第 2 条。</li>
 *
 * <li><b>没有其它改动。</b>{@code event.originalDamage()}、{@code Math.max/Math.min/Math.round}、
 * {@code ExperienceOrb(Level, double, double, double, int)} 五参构造器（已用 javap 确认）、
 * {@code event.level().addFreshEntity(Entity)}（{@code LevelWriter} 的 default 方法，
 * 调用点写法与 26.3 相同）全部同形。本类<b>除签名外是逐字直接移植</b>。</li>
 *
 * <li><b>取值时机原样保留：读的是 {@code event.originalDamage()} 而不是
 * {@code event.damage().getValue()}。</b>于是本附魔产出的经验球数量<b>不受</b>同一事件里其它附魔
 * （残杀、枯萎、强劲……）改动伤害的影响，也与 26.3 一致。</li>
 *
 * <li><b>{@code Math.round(float)} 返回 {@code int}</b>，与 26.3 相同，故 {@code int xp} 不用改。</li>
 * </ol>
 */
public final class SoulSiphonEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":soul_siphon";

	private static final float COEFFICIENT_PER_LEVEL = 0.2F;
	private static final float CAP_PER_LEVEL = 2.0F;

	private SoulSiphonEnchantment() {
	}


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, SoulSiphonEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		float raw = Math.max(0.0F, event.originalDamage());
		int xp = Math.round(Math.min(raw * COEFFICIENT_PER_LEVEL * ctx.level(), CAP_PER_LEVEL * ctx.level()));
		if (xp <= 0) {
			return;
		}
		var target = event.target();
		ExperienceOrb orb = new ExperienceOrb(event.level(),
			target.getX(), target.getY() + 0.5, target.getZ(), xp);
		event.level().addFreshEntity(orb);
	}
}