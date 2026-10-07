package com.practicalenchantments;

import com.practicalenchantments.enchantment.LifeStealEnchantment;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge 入口：Practical Enchantments / Minecraft 1.20.1。
 *
 * <h2>与 26.3 的结构差异</h2>
 *
 * <p>26.3 的 PE 有 {@code common} / {@code fabric} / {@code neoforge} 三个模块，业务逻辑在 common，
 * 两端各有一个薄入口。本工程只出 Forge 端，所以入口与业务逻辑同在一个模块里，这个类就是 26.3 那两个
 * 入口（{@code PracticalEnchantmentsFabric} / {@code PracticalEnchantmentsNeoForge}）塌缩成的一份。</p>
 *
 * <p>它做两件事：把装配交给 {@link PracticalEnchantments#bootstrap()}，以及承载那些
 * <b>住在加载器入口里</b>的平台桥接。真正的顺序纪律写在 {@code bootstrap()} 的 javadoc 里，这里不重复。</p>
 *
 * <h2>为什么桥接写在入口类里，而不是写进附魔的行为类</h2>
 *
 * <p>{@link #onLivingDamage} 是「吸血」附魔的加载器桥接。它<b>必须</b>存在：该附魔的回血量取决于
 * 「本次实际造成的伤害」，而那个数字只有加载器的伤害结算事件提供，MerlinLib 的附魔事件层拿不到
 * （它分发的是"谁打谁"，不是"扣了多少血"）。</p>
 *
 * <p>它写在这里而不是写进 {@code LifeStealEnchantment}，是因为 26.3 的分工就是这样：行为类只回答
 * "被打之后怎么回血"，平台事件到该方法的转发住在加载器入口里。1.20.1 只有一个加载器，所以那两半
 * 塌缩成这一个方法，但<b>位置没有变</b>——一个附魔的行为类不应该顺带订阅全局伤害事件，否则读代码的
 * 人要在 22 个行为类里找出哪些偷偷挂了事件监听。</p>
 *
 * <p>事件的选择也是照搬 26.3 的语义，不是随手挑的：{@code LivingDamageEvent} 与 26.3 用的
 * NeoForge {@code LivingDamageEvent.Post} / Fabric {@code AFTER_DAMAGE} 是同一个阶段。26.3 的桥接
 * 源码里用 {@code javap -c} 核对过 {@code LivingEntity#actuallyHurt} 的字节码顺序：
 * {@code isInvulnerableTo} → {@code ForgeHooks.onLivingHurt}（LivingHurtEvent）→ 护甲吸收 →
 * 魔法吸收 → 吸收效果 → <b>{@code ForgeHooks.onLivingDamage}（LivingDamageEvent）</b> →
 * {@code setHealth}。即它在<b>护甲/魔法减免之后、扣血之前</b>触发，正是"实际造成的伤害"那一刻。
 * 用更早的 {@code LivingHurtEvent} 会拿到未减免的伤害，回血量会偏高，所以没有用它。</p>
 *
 * <p>挂的是<b>游戏事件总线</b>（{@code Bus.FORGE}，也是默认值，这里仍写出来）：伤害结算是游戏事件，
 * 不是模组加载事件。</p>
 */
@Mod(PracticalEnchantments.MOD_ID)
@Mod.EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PracticalEnchantmentsForge {

	public PracticalEnchantmentsForge() {
		PracticalEnchantments.bootstrap();
	}

	/**
	 * 「吸血」附魔的加载器桥接：把 Forge 的 AFTER_DAMAGE 阶段转发给行为类。
	 *
	 * <p>26.3 的 AFTER_DAMAGE 事件带盾牌格挡信息；Forge 的 {@code LivingDamageEvent} 不带
	 * （格挡信息在更早的 {@code LivingHurtEvent} 上）。本附魔<b>本来就不用</b>那两个参数，故传
	 * {@code 0.0F} 与 {@code false}。这不是行为损失，是参数来源的差异。</p>
	 *
	 * @param event 受伤事件；{@code getEntity()} 是被击者，{@code getSource()} 是伤害来源
	 */
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		LifeStealEnchantment.onAfterDamage(event.getEntity(), event.getSource(), event.getAmount(), 0.0F, false);
	}
}