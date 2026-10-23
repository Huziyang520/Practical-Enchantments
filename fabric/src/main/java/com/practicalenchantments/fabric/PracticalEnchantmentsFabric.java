package com.practicalenchantments.fabric;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import com.practicalenchantments.fabric.config.FabricVanillaTweaks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

/**
 * Fabric 平台入口：启动 common 侧的挂载，并读出原版附魔调整的配置。
 */
public final class PracticalEnchantmentsFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		// 附魔定义/互斥组/tradeable/村民交易都已是数据包资源，这里只启动 common 侧的三件事：
		// 战利品注入、事件回调、配置与进服预告（由 MerlinLib 的服务器生命周期驱动）。
		PracticalEnchantments.bootstrap();
		FabricVanillaTweaks.load();

		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, blockedDamage, blocked) ->
			LifeStealEnchantment.onAfterDamage(entity, source, amount, blockedDamage, blocked));
	}
}
