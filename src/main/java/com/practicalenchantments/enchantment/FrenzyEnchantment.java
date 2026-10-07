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
 *
 * <p>击杀生物后获得速度：Ⅰ 级速度 II 6 秒，Ⅱ 级速度 III 8 秒；
 * 附在斧上时持续时间翻倍（Ⅰ 12 秒 / Ⅱ 16 秒）。每次击杀重新计时。</p>
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
 * <li><b>⚠️ {@code MobEffects.SPEED} → {@code MobEffects.MOVEMENT_SPEED}。</b>这是 1.20.1 的
 * 强制改名：1.20.1 的 {@code MobEffects} 里<b>没有</b> {@code SPEED} 这个字段（已用 javap 列出
 * 全部字段确认，只有 {@code MOVEMENT_SPEED} 与 {@code DIG_SPEED}）。26.3 叫 {@code SPEED}。
 * 换名<b>不改变任何数值或行为</b>，只是同一效果的字段改名。</li>
 *
 * <li><b>等级语义原样保留：{@code amplifier = ctx.level()}。</b>于是 Ⅰ 级给的是
 * {@code MOVEMENT_SPEED} 增幅 1，即游戏内显示的"速度 II"；Ⅱ 级增幅 2，即"速度 III"。
 * 与 javadoc 描述一致，也和 26.3 完全一致（不是 off-by-one）。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code MobEffectInstance(MobEffect, int, int, boolean, boolean)}
 * 五参构造器在 1.20.1 存在（已用 javap 确认），两个布尔分别是
 * {@code ambient=true}（粒子更透明，信标/潮涌核心那种观感）与
 * {@code visible=true}（显示图标）——这里与 26.3 传的值相同。</li>
 * </ol>
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