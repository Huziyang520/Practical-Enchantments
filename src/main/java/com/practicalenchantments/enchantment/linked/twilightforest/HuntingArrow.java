// 追踪箭矢：暮色森林联动附魔，仅在装了该模组时注册，战利品只出在冰雪女王相关宝箱。

package com.practicalenchantments.enchantment.linked.twilightforest;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.huziyang520.merlinlib.content.EnchantmentJson;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import java.util.List;

public final class HuntingArrow {
	public static final String PATH = "hunting_arrow";
	public static final String ID = PracticalEnchantments.MOD_ID + ":" + PATH;
	private static final List<String> ITEMS = List.of("minecraft:bow", "minecraft:crossbow");

	private static final String[] LOOT_TABLES = {
			"twilightforest:chests/aurora_cache",
			"twilightforest:chests/aurora_room",
	};

	private HuntingArrow() {
	}

	public static void registerContent() {
		MerlinApi.enchantments().register(ResourceLocation.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, PATH))
				.translationKey("enchantment." + PracticalEnchantments.MOD_ID + "." + PATH)
				.weight(1)
				.maxLevel(1)
				.cost(20, 0, 50, 0)
				.anvilCost(4)
				.slots("mainhand")
				.supportedItems(EnchantmentJson.stringArray(ITEMS))
				.acquisition(Acquisition.TREASURE_ONLY)
				.category(EnchantmentCategory.BOW)
				.submit();
		for (String table : LOOT_TABLES) {
			MerlinApi.loot().register(LootInjectionBuilder.create()
					.toTables(table)
					.asBook()
					.withEnchantments(ID)
					.chance(0.20F)
					.weight(1)
					.quality(2)
					.allowModdedTables());
		}
		MerlinApi.loot().register(LootInjectionBuilder.create()
				.toTables("twilightforest:entities/snow_queen")
				.asBook()
				.withEnchantments(ID)
				.chance(0.65F)
				.weight(1)
				.quality(2)
				.allowModdedTables());
	}
}
