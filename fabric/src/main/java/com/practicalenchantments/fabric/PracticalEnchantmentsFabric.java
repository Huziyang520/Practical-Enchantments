// Fabric 平台入口：启动 common 侧的挂载，并读出原版附魔调整的配置。

package com.practicalenchantments.fabric;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import com.practicalenchantments.fabric.config.FabricVanillaTweaks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public final class PracticalEnchantmentsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		PracticalEnchantments.bootstrap();
		FabricVanillaTweaks.load();

		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, blockedDamage, blocked) ->
			LifeStealEnchantment.onAfterDamage(entity, source, amount, blockedDamage, blocked));
	}
}
