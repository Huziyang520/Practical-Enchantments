// Fabric 平台入口：启动 common 侧的挂载，并读出原版附魔调整的配置。

package com.practicalenchantments.fabric;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.weapon.LifeStealEnchantment;
import com.practicalenchantments.fabric.config.FabricVanillaTweaks;
import com.practicalenchantments.content.PeContent;
import net.fabricmc.api.ModInitializer;
import com.practicalenchantments.content.RangeWrathBrewing;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class PracticalEnchantmentsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		PracticalEnchantments.bootstrap();
		FabricVanillaTweaks.load();

		CreativeModeTabEvents.modifyOutputEvent(PeContent.tabKey()).register(output -> {
			for (ItemStack stack : PeContent.tabEntries()) {
				output.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			}
		});

		FabricPotionBrewingBuilder.BUILD.register(builder -> RangeWrathBrewing.apply(builder));

		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, blockedDamage, blocked) ->
			LifeStealEnchantment.onAfterDamage(entity, source, amount, blockedDamage, blocked));
	}
}
