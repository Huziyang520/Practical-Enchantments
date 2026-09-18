package com.practicalenchantments.fabric;

import com.practicalenchantments.enchantment.LifeStealEnchantment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

/**
 * Fabric 平台入口。
 *
 * <p>附魔本身通过 {@code fabric.mod.json} 的 {@code enchantlib:enchantments}
 * entrypoint（指向 {@code com.practicalenchantments.PracticalEnchantments}）由 EnchantLib
 * 自动发现；事件回调、战利品注入、村民交易均在 EnchantLib 内部经 {@code IEventBridge}
 * 桥接到 Fabric 原生事件。</p>
 *
 * <p>仅「吸血」需要护甲减免后的实际伤害（AFTER_DAMAGE 语义），enchantlib 的
 * POST_HURT 扫描的是被击者装备，故在此直接把 Fabric 的 AFTER_DAMAGE 桥接到
 * common 的 {@link LifeStealEnchantment#onAfterDamage}（与 examplemod 回敬同款）。</p>
 */
public final class PracticalEnchantmentsFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, blockedDamage, blocked) ->
			LifeStealEnchantment.onAfterDamage(entity, source, amount, blockedDamage, blocked));
	}
}
