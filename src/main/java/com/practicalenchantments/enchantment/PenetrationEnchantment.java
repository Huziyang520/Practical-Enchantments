package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.item.Items;

/**
 * 贯穿附魔 - 三叉戟 Ⅴ 级（附魔台可出，不可交易）。
 */
public final class PenetrationEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":penetration";


	private PenetrationEnchantment() {
	}


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.PROJECTILE_HIT, PenetrationEnchantment::onProjectileHit);
	}

	private static void onProjectileHit(BuiltInEvents.ProjectileHitEvent event, EnchantmentContext ctx) {
		// 仅三叉戟投掷触发（近战持有不触发）
		if (!event.weapon().is(Items.TRIDENT)) {
			return;
		}
		float extra = 1.5F + 0.5F * (ctx.level() - 1);

		// 弹射物本体伤害此刻已结算（mixin 注入点在 onHitEntity 之后），目标的受击冷却
		// （1.20.1 的 invulnerableTime > 10，对应 上游版本的 damageCooldownTime > 10）会把
		// "额外伤害 ≤ lastHurt"的再次 hurt 直接吞掉——表现为 1.5 + 0.5×(等级-1) 完全没生效。
		// 先清零冷却，让额外伤害全额结算。
		event.target().invulnerableTime = 0;
		event.target().hurt(event.attacker().damageSources().mobAttack(event.attacker()), extra);

		// 贯穿 × 忠诚：把命中目标交给牵引状态中心（TridentPullMixin 的回归 tick 消费）
		if (DeathSaveSupport.getLevelFull(event.weapon(), "minecraft:loyalty") > 0) {
			TridentPullSupport.captureMob(event.attacker().getUUID(), event.target());
		}
	}
}