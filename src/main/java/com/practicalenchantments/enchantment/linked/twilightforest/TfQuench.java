// 炽血之淬：挖掘方块时直接掉落熔炼产物。暮色森林专属，只从该模组的战利品箱产出。

package com.practicalenchantments.enchantment.linked.twilightforest;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.util.SmeltingLookup;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class TfQuench {
	public static final String PATH = "tf_quench";
	public static final String ID = PracticalEnchantments.MOD_ID + ":" + PATH;

	private static final String[] LOOT_TABLES = {
			"twilightforest:chests/hollow_hill",
			"twilightforest:chests/labyrinth",
			"twilightforest:chests/dark_tower",
			"twilightforest:chests/aurora_palace",
	};

	public static void registerContent() {
		MerlinApi.enchantments().register(ResourceLocation.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, PATH))
				.maxLevel(1)
				.weight(1)
				.anvilCost(4)
				.slots("mainhand")
				.supportedItems("#minecraft:enchantable/mining")
				.cost(20, 0, 50, 0)
				.acquisition(Acquisition.TREASURE_ONLY)
				.submit();
		for (String table : LOOT_TABLES) {
			MerlinApi.loot().register(LootInjectionBuilder.create()
					.toTables(table)
					.asBook()
					.withEnchantments(ID)
					.chance(0.12F)
					.weight(1)
					.quality(2)
					.allowModdedTables());
		}
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_BLOCK_DROPS, TfQuench::onBlockDrops);
	}

	private static void onBlockDrops(BuiltInEvents.ModifyBlockDropsEvent event, EnchantmentContext ctx) {
		event.transformDrops(TfQuench::asSmelted);
	}

	private static ItemStack asSmelted(ItemStack drop) {
		return SmeltingLookup.smelt(drop)
				.map(result -> result.copyWithCount(
						Math.min(result.getMaxStackSize(), result.getCount() * drop.getCount())))
				.orElse(drop);
	}
}
