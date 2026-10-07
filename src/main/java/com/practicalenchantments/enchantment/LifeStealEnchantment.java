package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.PracticalEnchantmentsForge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 吸血附魔 - 剑/斧/矛 Ⅴ 级（附魔台可出，老手级图书管理员）。
 *
 * <p>命中目标后回复生命，回复量 = 目标本次<b>实际损失生命</b>（护甲/护盾减免后）
 * × 4% × 等级，最高 20%/级。</p>
 *
 * <p>实际伤害只有加载器的 AFTER_DAMAGE 事件提供，故不走 MerlinLib 的附魔事件注册，
 * 由加载器入口把平台事件桥接到 {@link #onAfterDamage}。</p>
 *
 * <h2>1.20.1 移植说明</h2>
 *
 * <p>本类<b>没有</b> {@code registerCallbacks}——26.3 也没有，而且
 * {@code PracticalEnchantments.registerEventCallbacks()} 明确不调用它
 * （那里写着"LifeSteal 走两加载器 AFTER_DAMAGE 桥接"）。<b>移植时没有给本类添加
 * {@code registerCallbacks}。</b></p>
 *
 * <ol>
 * <li><b>桥接住在入口类里，不在本类里。</b>26.3 的平台桥接住在<b>加载器入口</b>
 * （Fabric 的 {@code AFTER_DAMAGE}、NeoForge 的 {@code LivingDamageEvent.Post}）。
 * 1.20.1 只有 Forge 一个加载器，所以那一半的对应物写在
 * {@link PracticalEnchantmentsForge#onLivingDamage} 里——<b>入口的归入口</b>，
 * 本类只管"被打之后怎么回血"。26.3 也是这个分工，没有变。</li>
 *
 * <li><b>{@code onAfterDamage} 的签名逐字保留</b>（含 5 个参数与顺序）：它是 26.3
 * 两套入口共用的桥接点，保持同形意味着 1.20.1 的入口可以逐句对上。</li>
 *
 * <li><b>{@code blockedDamage} 与 {@code blocked} 在 1.20.1 上没有来源。</b>
 * 26.3 的 AFTER_DAMAGE 事件带盾牌格挡信息；Forge 的 {@code LivingDamageEvent} 不带
 * （格挡信息在更早的 {@code LivingHurtEvent} 上）。本附魔<b>本来就不用这两个参数</b>，
 * 故入口转发时传 {@code 0.0F} 与 {@code false}。这不是行为损失。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code source.getEntity() instanceof ServerPlayer player}、
 * {@code DeathSaveSupport.getLevel(player.getMainHandItem(), "life_steal")}、
 * {@code player.heal(float)}、{@code HEAL_PER_LEVEL = 0.04F} 与
 * {@code amount <= 0.0F} 的短路全部同形。</li>
 *
 * <li><b>一个需要知情的时序细节（与 26.3 相同，非移植引入）</b>：回血发生在伤害结算的同一 tick
 * 内、扣血<b>之前</b>，所以"被反击致死的那一击"也可能先把攻击者奶回来。26.3 的 AFTER_DAMAGE
 * 同理。</li>
 * </ol>
 */
public final class LifeStealEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":life_steal";

	private static final float HEAL_PER_LEVEL = 0.04F;

	private LifeStealEnchantment() {
	}


	/**
	 * AFTER_DAMAGE 桥接：被打的是 target，攻击者持吸血武器时给攻击者回血。
	 *
	 * <p>由 {@link PracticalEnchantmentsForge#onLivingDamage} 在护甲/魔法减免之后、扣血之前调用，
	 * 因此 {@code amount} 就是"本次实际造成的伤害"。</p>
	 *
	 * @param entity        被击者
	 * @param source        伤害来源
	 * @param amount        实际造成的伤害（护甲减免后）
	 * @param blockedDamage 被盾格挡的部分（本附魔不用）
	 * @param blocked       是否被完全格挡（本附魔不用）
	 */
	public static void onAfterDamage(LivingEntity entity, DamageSource source,
									 float amount, float blockedDamage, boolean blocked) {
		if (!(source.getEntity() instanceof ServerPlayer player) || amount <= 0.0F) {
			return;
		}
		int level = DeathSaveSupport.getLevel(player.getMainHandItem(), "life_steal");
		if (level <= 0) {
			return;
		}
		float heal = amount * HEAL_PER_LEVEL * level;
		if (heal > 0.0F) {
			player.heal(heal);
		}
	}
}