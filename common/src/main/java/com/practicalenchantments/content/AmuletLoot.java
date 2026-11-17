// 护身符进入战利品箱：档次越高越稀有，下界合金只在最深的三张表里出现。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.api.LootTables;
import java.util.List;

public final class AmuletLoot {
	private static final List<String> COMMON_TABLES = List.of(
			LootTables.SIMPLE_DUNGEON,
			LootTables.ABANDONED_MINESHAFT,
			LootTables.VILLAGE_WEAPONSMITH,
			LootTables.SHIPWRECK_TREASURE);
	private static final List<String> RARE_TABLES = List.of(
			LootTables.WOODLAND_MANSION,
			LootTables.BASTION_TREASURE,
			LootTables.END_CITY_TREASURE,
			LootTables.ANCIENT_CITY);
	private static final List<String> DEEP_TABLES = List.of(
			LootTables.BASTION_TREASURE,
			LootTables.END_CITY_TREASURE,
			LootTables.ANCIENT_CITY);
	private static boolean registered;

	private AmuletLoot() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		inject(PeContent.IRON_AMULET, COMMON_TABLES, 0.06F);
		inject(PeContent.GOLDEN_AMULET, COMMON_TABLES, 0.04F);
		inject(PeContent.DIAMOND_AMULET, RARE_TABLES, 0.025F);
		inject(PeContent.NETHERITE_AMULET, DEEP_TABLES, 0.004F);
	}

	private static void inject(net.minecraft.resources.Identifier amulet, List<String> tables, float chance) {
		MerlinApi.loot().register(LootInjectionBuilder.create()
				.toTables(tables.toArray(new String[0]))
				.asPlainItem(PeContent.item(amulet))
				.chance(chance)
				.weight(1)
				.quality(1));
	}
}
