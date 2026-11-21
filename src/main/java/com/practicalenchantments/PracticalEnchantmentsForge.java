// Forge 入口：装配本模组，并承载「吸血」的伤害事件桥接。

package com.practicalenchantments;

import com.huziyang520.merlinlib.platform.Services;
import com.practicalenchantments.config.ConfigUi;
import com.practicalenchantments.config.ForgeConfig;
import com.practicalenchantments.config.MezzConfigBridge;
import com.practicalenchantments.content.AmuletLoot;
import com.practicalenchantments.content.BookBagLoot;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.EvolutionTableMenu;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.PeMenus;
import com.practicalenchantments.content.WrathBrewing;
import com.practicalenchantments.network.PeNetwork;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import com.practicalenchantments.enchantment.HeavyBlowEnchantment;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import com.practicalenchantments.support.InvisibleAirMobGear;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(PracticalEnchantments.MOD_ID)
@Mod.EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PracticalEnchantmentsForge {
	public PracticalEnchantmentsForge() {
		ForgeConfig.preload();
		PracticalEnchantments.bootstrap();
		registerMenus();
		PeNetwork.register();
		ModLoadingContext.get().registerConfig(ForgeConfig.type(), ForgeConfig.SPEC);

		FMLJavaModLoadingContext.get().getModEventBus()
				.addListener((ModConfigEvent event) -> ForgeConfig.apply());
		FMLJavaModLoadingContext.get().getModEventBus()
				.addListener(this::onCommonSetup);
		ForgeConfig.apply();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ConfigUi::install);
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MezzConfigBridge::install);
		}
	}

	public void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			WrathBrewing.register();
			// 物品由 DeferredRegister 在注册事件里创建，战利品注入必须等这一步之后才能取到物品。
			AmuletLoot.register();
			BookBagLoot.register();
		});
	}

	private static void registerMenus() {
		MenuType<EvolutionTableMenu> evolutionTable = new MenuType<>(EvolutionTableMenu::new, FeatureFlags.VANILLA_SET);
		Services.REGISTRATIONS.register(BuiltInRegistries.MENU, PeContent.EVOLUTION_TABLE, () -> evolutionTable);
		PeMenus.setEvolutionTable(evolutionTable);
		MenuType<BookBagMenu> bookBag = new MenuType<>(BookBagMenu::new, FeatureFlags.VANILLA_SET);
		Services.REGISTRATIONS.register(BuiltInRegistries.MENU, PeContent.BOOK_BAG_LEATHER, () -> bookBag);
		PeMenus.setBookBag(bookBag);
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		LifeStealEnchantment.onAfterDamage(event.getEntity(), event.getSource(), event.getAmount(), 0.0F, false);
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		InvisibleAirMobGear.tryApply(event.getEntity(), event.getLevel(), event.loadedFromDisk());
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		if (event.isVanillaCritical() || event.getResult() != Event.Result.DEFAULT) {
			return;
		}
		Player player = event.getEntity();
		if (!HeavyBlowEnchantment.has(player)) {
			return;
		}
		event.setDamageModifier(1.5F);
		event.setResult(Event.Result.ALLOW);
	}
}
