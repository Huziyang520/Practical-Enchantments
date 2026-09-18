package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.ExclusiveGroupBuilder;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 贯穿附魔 - 三叉戟 Ⅴ 级（附魔台可出，不可交易）。
 *
 * <p>投掷命中实体额外造成 {@code 1.5 + 0.5×(等级-1)} 点伤害（Ⅰ~Ⅴ：1.5/2.0/2.5/3.0/3.5）。
 * 与激流互斥（trident_riptide 组）。</p>
 *
 * <p>「搭配忠诚时牵引命中目标」的物理效果由 {@code TridentPullMixin} 实现。</p>
 */
public final class PenetrationEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":penetration";

	public static final String EXCLUSIVE_SET = ExclusiveGroupBuilder
		.create(PracticalEnchantments.MOD_ID, "trident_riptide")
		.getTagReference();

	private PenetrationEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("贯穿")
			.supportedItems("#minecraft:enchantable/trident")
			.weight(6)
			.maxLevel(5)
			.minCost(6, 8)
			.maxCost(28, 8)
			.anvilCost(3)
			.exclusiveSet(EXCLUSIVE_SET)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.PROJECTILE_HIT, PenetrationEnchantment::onProjectileHit);
	}

	private static void onProjectileHit(BuiltInEvents.ProjectileHitEvent event, EnchantmentContext ctx) {
		// 仅三叉戟投掷触发（近战持有不触发）
		if (!event.weapon().is(Items.TRIDENT)) {
			return;
		}
		float extra = 1.5F + 0.5F * (ctx.level() - 1);

		// 弹射物本体伤害此刻已结算（mixin 注入点在 onHitEntity 之后），目标的受伤无敌帧
		// （26.2 的 invulnerableTime > 10）会把"额外伤害 ≤ lastHurt"的再次 hurtServer
		// 直接吞掉——表现为 1.5 + 0.5×(等级-1) 完全没生效。先清零无敌帧，让额外伤害全额结算。
		event.target().invulnerableTime = 0;
		event.target().hurtServer(event.level(),
			event.attacker().damageSources().mobAttack(event.attacker()), extra);

		// 贯穿 × 忠诚：把命中目标交给牵引状态中心（TridentPullMixin 的回归 tick 消费）
		if (DeathSaveSupport.getLevelFull(event.weapon(), "minecraft:loyalty") > 0) {
			TridentPullSupport.captureMob(event.attacker().getUUID(), event.target());
		}
	}
}
