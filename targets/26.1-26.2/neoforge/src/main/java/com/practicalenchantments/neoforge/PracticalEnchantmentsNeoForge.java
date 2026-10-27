// NeoForge 平台入口：启动 common 侧的挂载，并把服务端配置接到原生配置界面上。

package com.practicalenchantments.neoforge;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.weapon.LifeStealEnchantment;
import com.practicalenchantments.neoforge.config.NeoConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.RangeWrathBrewing;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {
	public PracticalEnchantmentsNeoForge(IEventBus modEventBus, ModContainer container) {
		PracticalEnchantments.bootstrap();
		container.registerConfig(NeoConfig.type(), NeoConfig.SPEC);
		modEventBus.addListener(ModConfigEvent.Loading.class, event -> NeoConfig.apply());
		modEventBus.addListener(ModConfigEvent.Reloading.class, event -> NeoConfig.apply());
		NeoConfig.apply();

		modEventBus.addListener(this::onBuildCreativeTab);
		NeoForge.EVENT_BUS.addListener(RegisterBrewingRecipesEvent.class, event -> RangeWrathBrewing.apply(event.getBuilder()));

		NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, event ->
			LifeStealEnchantment.onAfterDamage(
				event.getEntity(), event.getSource(), event.getOriginalDamage(),
				event.getBlockedDamage(), event.getBlockedDamage() > 0));
	}
	private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (!PeContent.tabKey().equals(event.getTabKey())) {
			return;
		}
		for (ItemStack stack : PeContent.tabEntries()) {
			if (event.getParentEntries().contains(stack) || event.getSearchEntries().contains(stack)) {
				continue;
			}
			event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}
}
