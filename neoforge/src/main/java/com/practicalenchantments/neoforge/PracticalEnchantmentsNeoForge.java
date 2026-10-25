// NeoForge 平台入口：启动 common 侧的挂载，并把服务端配置接到原生配置界面上。

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

@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {
	public PracticalEnchantmentsNeoForge(IEventBus modEventBus, ModContainer container) {
		PracticalEnchantments.bootstrap();
		container.registerConfig(NeoConfig.type(), NeoConfig.SPEC);
		modEventBus.addListener(ModConfigEvent.Loading.class, event -> NeoConfig.apply());
		modEventBus.addListener(ModConfigEvent.Reloading.class, event -> NeoConfig.apply());
		NeoConfig.apply();

		NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, event ->
			LifeStealEnchantment.onAfterDamage(
				event.getEntity(), event.getSource(), event.getOriginalDamage(),
				event.getBlockedDamage(), event.getBlockedDamage() > 0));
	}
}
