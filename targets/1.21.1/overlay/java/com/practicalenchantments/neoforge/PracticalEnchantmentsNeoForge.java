// NeoForge 平台入口：注册内容、菜单类型、网络包，并把事件总线上的玩法钩子接起来。

package com.practicalenchantments.neoforge;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.VillagerTradeRegistration;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.EvolutionTableMenu;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.PeMenus;
import com.practicalenchantments.content.WrathBrewing;
import com.practicalenchantments.enchantment.weapon.HeavyBlowEnchantment;
import com.practicalenchantments.enchantment.weapon.LifeStealEnchantment;
import com.practicalenchantments.neoforge.config.NeoConfig;
import com.practicalenchantments.network.BagSearchPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {
	private static final DeferredRegister<MenuType<?>> MENUS =
			DeferredRegister.create(Registries.MENU, PracticalEnchantments.MOD_ID);
	private static final DeferredHolder<MenuType<?>, MenuType<EvolutionTableMenu>> EVOLUTION_TABLE_MENU =
			MENUS.register("evolution_table", () -> IMenuTypeExtension.create(
					(windowId, inventory, data) -> new EvolutionTableMenu(windowId, inventory)));
	private static final DeferredHolder<MenuType<?>, MenuType<BookBagMenu>> BOOK_BAG_MENU =
			MENUS.register("book_bag_leather", () -> IMenuTypeExtension.create(
					(windowId, inventory, data) -> new BookBagMenu(windowId, inventory)));

	public PracticalEnchantmentsNeoForge(IEventBus modEventBus, ModContainer container) {
		NeoConfig.preload();
		PeContent.register(modEventBus);
		MENUS.register(modEventBus);
		PracticalEnchantments.bootstrap();
		container.registerConfig(NeoConfig.type(), NeoConfig.SPEC);
		modEventBus.addListener(ModConfigEvent.Loading.class, event -> NeoConfig.apply());
		modEventBus.addListener(ModConfigEvent.Reloading.class, event -> NeoConfig.apply());
		modEventBus.addListener(this::onRegisterPayloads);
		NeoConfig.apply();

		PeMenus.setEvolutionTable(EVOLUTION_TABLE_MENU.get());
		PeMenus.setBookBag(BOOK_BAG_MENU.get());

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

	private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
		event.registrar("1").playToServer(BagSearchPayload.TYPE, BagSearchPayload.STREAM_CODEC,
				(payload, context) -> {
					if (context.player().containerMenu instanceof BookBagMenu menu) {
						menu.setQuery(payload.query());
					}
				});
	}
}
