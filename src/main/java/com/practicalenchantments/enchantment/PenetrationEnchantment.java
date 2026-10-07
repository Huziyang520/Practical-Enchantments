package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.item.Items;

/**
 * 贯穿附魔 - 三叉戟 Ⅴ 级（附魔台可出，不可交易）。
 *
 * <p>投掷命中实体额外造成 {@code 1.5 + 0.5×(等级-1)} 点伤害（Ⅰ~Ⅴ：1.5/2.0/2.5/3.0/3.5）。
 * 与激流互斥（trident_riptide 组）。</p>
 *
 * <p>「搭配忠诚时牵引命中目标」的物理效果由 {@code TridentPullMixin} 实现。</p>
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
 * <li><b>⚠️ {@code damageCooldownTime} → {@code invulnerableTime}（1.20.1 没有
 * {@code damageCooldownTime} 这个字段）。</b>26.3 的注释写得很清楚：注入点在弹射物本体伤害
 * 结算<b>之后</b>，而目标的受击冷却会把"额外伤害 ≤ lastHurt"的再次攻击整个吞掉，
 * 所以必须先清零冷却。1.20.1 上承担同一职责的字段是 {@code Entity.invulnerableTime}
 * （public int）。已用 {@code javap -c} 逐行核对 {@code LivingEntity#hurt} 的字节码，确认
 * 两者的判定形状一一对应：
 * <pre>
 * 26.3（注释所述）       1.20.1 字节码实测
 * damageCooldownTime &gt; 10   →   invulnerableTime &gt; 10.0f（i2f 后 fcmpl）
 * 且非 BYPASSES_COOLDOWN     →   且 !source.is(DamageTypeTags.BYPASSES_COOLDOWN)
 * 且 本次伤害 ≤ lastHurt     →   且 amount &lt;= lastHurt（fcmpg → ireturn false）
 * ⇒ 直接返回、不结算            ⇒ 直接 ireturn，不进 actuallyHurt
 * </pre>
 * 把 {@code invulnerableTime} 置 0 之后，{@code 0 &gt; 10.0f} 为 false，{@code hurt} 走另一条
 * 分支（{@code invulnerableTime = 20} 并调用 {@code actuallyHurt}），额外伤害全额结算。
 * <b>行为与 26.3 的目标一致。</b></li>
 *
 * <li><b>⚠️ {@code hurtServer(ServerLevel, DamageSource, float)} → {@code hurt(DamageSource, float)}。</b>
 * 1.20.1 的受击入口只有 {@code LivingEntity#hurt(DamageSource, float)}，没有"带 level 的
 * server 版"（那是 1.21 把客户端/服务端受击拆开之后的事）。参数里的 level 本来就是"当前所在维度"，
 * 而 {@code Entity} 自己知道自己在哪，所以这个实参直接去掉即可，语义不变。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code event.weapon().is(Items.TRIDENT)}、
 * {@code event.attacker().damageSources().mobAttack(event.attacker())}（{@code DamageSources}
 * 在 1.20.1 同样有 {@code mobAttack(LivingEntity)}，已用 javap 确认）、
 * {@code DeathSaveSupport.getLevelFull(weapon, "minecraft:loyalty")}、
 * {@code TridentPullSupport.captureMob(UUID, LivingEntity)} 全部同形。</li>
 *
 * <li><b>本类不移除</b>：{@code event.attacker()} 与 {@code event.target()} 在
 * {@code ProjectileHitEvent} 里都是非空记录组件（弹射物必有人发射、必命中实体才派发），
 * 26.3 也没做 null 检查。</li>
 * </ol>
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
		// （1.20.1 的 invulnerableTime > 10，对应 26.3 的 damageCooldownTime > 10）会把
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