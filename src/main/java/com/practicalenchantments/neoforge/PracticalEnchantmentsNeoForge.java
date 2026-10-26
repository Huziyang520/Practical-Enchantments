// NeoForge 平台入口。

package com.practicalenchantments.neoforge;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.VillagerTradeRegistration;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.WrathBrewing;
import com.practicalenchantments.enchantment.HeavyBlowEnchantment;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import com.practicalenchantments.neoforge.config.NeoConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {
	public PracticalEnchantmentsNeoForge(IEventBus modEventBus, ModContainer container) {
		PeContent.register(modEventBus);
		PracticalEnchantments.bootstrap();
		container.registerConfig(NeoConfig.type(), NeoConfig.SPEC);
		modEventBus.addListener(ModConfigEvent.Loading.class, event -> NeoConfig.apply());
		modEventBus.addListener(ModConfigEvent.Reloading.class, event -> NeoConfig.apply());
		NeoConfig.apply();

		NeoForge.EVENT_BUS.addListener(CriticalHitEvent.class, event -> {
			if (event.getEntity() instanceof Player player && HeavyBlowEnchantment.has(player)) {
				event.setCriticalHit(true);
				event.setDamageMultiplier(1.5F);
			}
		});

		NeoForge.EVENT_BUS.addListener(RegisterBrewingRecipesEvent.class, event ->
			WrathBrewing.register(event.getBuilder(), event.getRegistryAccess()));

		NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, event ->
			LifeStealEnchantment.onAfterDamage(
				event.getEntity(), event.getSource(), event.getOriginalDamage(),
				event.getBlockedDamage(), event.getBlockedDamage() > 0));

		NeoForge.EVENT_BUS.addListener(VillagerTradesEvent.class, VillagerTradeRegistration::onVillagerTrades);
	}
}
