package com.practicalenchantments.neoforge;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import com.practicalenchantments.neoforge.config.NeoConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * NeoForge 平台入口：启动 common 侧的挂载，并把服务端配置接到原生配置界面上。
 */
@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {

	public PracticalEnchantmentsNeoForge(IEventBus modEventBus, ModContainer container) {
		// 附魔定义/互斥组/tradeable/村民交易都已是数据包资源，这里只启动 common 侧的三件事：
		// 战利品注入、事件回调、配置与进服预告（由 MerlinLib 的服务器生命周期驱动）。
		PracticalEnchantments.bootstrap();

		container.registerConfig(NeoConfig.type(), NeoConfig.SPEC);
		modEventBus.addListener(ModConfigEvent.Loading.class, event -> NeoConfig.apply());
		modEventBus.addListener(ModConfigEvent.Reloading.class, event -> NeoConfig.apply());
		NeoConfig.apply();

		// LivingDamageEvent.Post → AFTER_DAMAGE 桥接（吸血）
		NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, event ->
			LifeStealEnchantment.onAfterDamage(
				event.getEntity(), event.getSource(), event.getOriginalDamage(),
				event.getBlockedDamage(), event.getBlockedDamage() > 0));
	}
}
