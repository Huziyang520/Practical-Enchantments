package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;

/**
 * 残杀附魔 - 剑/斧/矛单级（宝藏，附魔台/交易均不出）。
 *
 * <p>按目标受伤前血量比例放大最终伤害：</p>
 * <ul>
 *   <li>当前血量 10%~35%：剑/矛 ×1.5，斧 ×2</li>
 *   <li>当前血量低于 10%：剑/矛 ×3，斧 ×4</li>
 *   <li>高于 35%：伤害不变</li>
 * </ul>
 * <p>在最终伤害上乘算，与锋利等原版伤害附魔共存。</p>
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
 * <li><b>没有其它改动。</b>{@code ItemTags.AXES}、{@code getMainHandItem()}、
 * {@code getHealth()/getMaxHealth()}、{@code MutableFloat} 的读写、
 * {@code instanceof ServerPlayer player} 的模式匹配在 1.20.1 上全部同形。
 * 本类<b>除签名外是逐字直接移植</b>。</li>
 *
 * <li>阈值语义原样保留：{@code ratio < 0.10} 用严格小于，{@code ratio <= 0.35} 用小于等于，
 * 于是正好 10% 血落在第二档、正好 35% 血也落在第二档。这不是笔误，是 26.3 的原始边界。</li>
 * </ol>
 */
public final class ExecuteEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":execute";

	private static final float LOW_HP_RATIO = 0.10F;
	private static final float MID_HP_RATIO = 0.35F;

	private ExecuteEnchantment() {
	}


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, ExecuteEnchantment::onModifyDamage);
	}

	private static void onModifyDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		if (!(event.attacker() instanceof ServerPlayer player)) {
			return;
		}
		LivingEntity target = event.target();
		float maxHp = target.getMaxHealth();
		if (maxHp <= 0.0F) {
			return;
		}
		float ratio = target.getHealth() / maxHp;
		float multiplier;
		boolean axe = player.getMainHandItem().is(ItemTags.AXES);
		if (ratio < LOW_HP_RATIO) {
			multiplier = axe ? 4.0F : 3.0F;
		} else if (ratio <= MID_HP_RATIO) {
			multiplier = axe ? 2.0F : 1.5F;
		} else {
			return;
		}
		event.damage().setValue(event.damage().getValue() * multiplier);
	}
}