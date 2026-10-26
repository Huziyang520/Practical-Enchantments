// 龙晶行者的战利品注入 - 只投放到末地城宝藏箱与堡垒遗迹宝藏箱（宝藏附魔，不参与附魔台与交易）。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.LootTables;
import com.huziyang520.merlinlib.api.MerlinApi;

public final class ObsidianWalkerLoot {
	private static final int QUALITY_TREASURE = 2;
	private static final float CHANCE = 0.10F;
	private static boolean registered;

	private ObsidianWalkerLoot() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		inject(LootTables.END_CITY_TREASURE);
		inject(LootTables.BASTION_TREASURE);
	}

	private static void inject(String table) {
		MerlinApi.loot().register(LootInjectionBuilder.create()
			.toTables(table)
			.asBook()
			.withEnchantments(ObsidianWalkerEnchantment.ID)
			.chance(CHANCE)
			.weight(1)
			.quality(QUALITY_TREASURE));
	}
}
