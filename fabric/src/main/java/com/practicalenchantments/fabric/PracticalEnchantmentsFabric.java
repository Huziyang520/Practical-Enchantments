package com.practicalenchantments.fabric;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric 平台入口。
 *
 * <p>附魔本身通过 {@code fabric.mod.json} 的 {@code enchantlib:enchantments}
 * entrypoint（指向 {@code com.practicalenchantments.PracticalEnchantments}）由 EnchantLib
 * 自动发现；事件回调、战利品注入、村民交易均在 EnchantLib 内部经 {@code IEventBridge}
 * 桥接到 Fabric 原生事件。</p>
 *
 * <p>因此本模组<b>不需要</b>在这里注册任何平台事件，本类仅作为 {@code main} entrypoint
 * 占位，保证 mod 被加载、mixin 配置生效。</p>
 */
public final class PracticalEnchantmentsFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		// 无平台特有逻辑：7 个附魔全部走 EnchantLib 的 BuiltInEvents / EnchantLibEvents
	}
}
