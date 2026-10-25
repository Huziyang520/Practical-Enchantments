// Forge 入口：装配本模组，并承载「吸血」的伤害事件桥接。

package com.practicalenchantments;

import com.practicalenchantments.config.ConfigUi;
import com.practicalenchantments.config.ForgeConfig;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(PracticalEnchantments.MOD_ID)
@Mod.EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PracticalEnchantmentsForge {
	public PracticalEnchantmentsForge() {
		PracticalEnchantments.bootstrap();
		ModLoadingContext.get().registerConfig(ForgeConfig.type(), ForgeConfig.SPEC);

		FMLJavaModLoadingContext.get().getModEventBus()
				.addListener((ModConfigEvent event) -> ForgeConfig.apply());
		ForgeConfig.apply();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ConfigUi::install);
		}
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		LifeStealEnchantment.onAfterDamage(event.getEntity(), event.getSource(), event.getAmount(), 0.0F, false);
	}
}
