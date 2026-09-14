package com.practicalenchantments.neoforge;

import com.enchantlib.api.EnchantmentApi;
import com.practicalenchantments.PracticalEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge 平台入口。
 *
 * <p>与 Fabric 不同，NeoForge 没有任意 key 的 entrypoint 扫描，必须在 {@code @Mod}
 * 构造器中显式把附魔入口交给 EnchantLib。</p>
 *
 * <p>事件回调、战利品注入、村民交易均在 EnchantLib 内部经 {@code IEventBridge}
 * 桥接到 NeoForge 事件总线，本模组无需在此注册平台事件。</p>
 *
 * <p>mixin 配置由 {@code META-INF/neoforge.mods.toml} 的 {@code [[mixins]]} 声明
 * （NeoForge 不会自动发现 jar 内的 mixin 配置）。</p>
 */
@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {

	public PracticalEnchantmentsNeoForge(IEventBus modEventBus) {
		// 显式注册附魔入口（NeoForge 无 entrypoint 自动扫描）
		EnchantmentApi.register(new PracticalEnchantments());
	}
}
