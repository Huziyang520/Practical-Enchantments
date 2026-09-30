package com.practicalenchantments.neoforge;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * NeoForge 平台入口。
 *
 * <p>与 Fabric 不同，NeoForge 没有任意 key 的 entrypoint 扫描，必须在 {@code @Mod}
 * 构造器中显式把附魔入口交给 EnchantLib。</p>
 *
 * <p>「吸血」需要护甲减免后的实际伤害，直接订阅 {@link LivingDamageEvent.Post}
 * 桥接到 common 的 {@code LifeStealEnchantment.onAfterDamage}。</p>
 *
 * <p>mixin 配置由 {@code META-INF/neoforge.mods.toml} 的 {@code [[mixins]]} 声明
 * （NeoForge 不会自动发现 jar 内的 mixin 配置）。</p>
 */
@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {

	public PracticalEnchantmentsNeoForge(IEventBus modEventBus) {
		// 附魔定义/互斥组/tradeable/村民交易都已是数据包资源，这里只启动 common 侧的三件事：
		// 战利品注入、事件回调、配置与进服预告（由 MerlinLib 的服务器生命周期驱动）。
		PracticalEnchantments.bootstrap();

		// LivingDamageEvent.Post → AFTER_DAMAGE 桥接（吸血）
		NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, event ->
			LifeStealEnchantment.onAfterDamage(
				event.getEntity(), event.getSource(), event.getOriginalDamage(),
				event.getBlockedDamage(), event.getBlockedDamage() > 0));
	}
}
