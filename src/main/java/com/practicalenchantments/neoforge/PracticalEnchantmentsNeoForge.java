// NeoForge 平台入口。

package com.practicalenchantments.neoforge;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.VillagerTradeRegistration;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import com.practicalenchantments.neoforge.config.NeoConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

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

		NeoForge.EVENT_BUS.addListener(VillagerTradesEvent.class, VillagerTradeRegistration::onVillagerTrades);
	}
}
