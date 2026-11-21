// 书袋进战利品箱：档次越高越稀有，箱内的袋子带一个标记，第一次打开时才掷出自带附魔书。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.LootTables;
import com.huziyang520.merlinlib.api.MerlinApi;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;

public final class BookBagLoot {
	private static final List<String> SHALLOW = List.of(
			LootTables.SIMPLE_DUNGEON,
			LootTables.ABANDONED_MINESHAFT,
			LootTables.SHIPWRECK_SUPPLY);
	private static final List<String> VILLAGE = List.of(
			LootTables.VILLAGE_WEAPONSMITH,
			LootTables.PILLAGER_OUTPOST,
			LootTables.STRONGHOLD_CORRIDOR);
	private static final List<String> DEEP = List.of(
			LootTables.WOODLAND_MANSION,
			LootTables.JUNGLE_TEMPLE,
			LootTables.DESERT_PYRAMID);
	private static final List<String> TREASURE = List.of(
			LootTables.BASTION_TREASURE,
			LootTables.END_CITY_TREASURE,
			LootTables.ANCIENT_CITY);
	private static final List<String> DEEPEST = List.of(
			LootTables.BASTION_TREASURE,
			LootTables.END_CITY_TREASURE);
	private static boolean registered;

	private BookBagLoot() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		inject(PeContent.BOOK_BAG_LEATHER, SHALLOW, 0.03F);
		inject(PeContent.BOOK_BAG_IRON, VILLAGE, 0.02F);
		inject(PeContent.BOOK_BAG_GOLD, DEEP, 0.02F);
		inject(PeContent.BOOK_BAG_DIAMOND, TREASURE, 0.02F);
		inject(PeContent.BOOK_BAG_NETHERITE, DEEPEST, 0.003F);
	}

	private static void inject(ResourceLocation bag, List<String> tables, float chance) {
		CompoundTag marker = new CompoundTag();
		marker.putBoolean(BookBagStorage.LOOT_MARKER, true);
		MerlinApi.loot().register(LootInjectionBuilder.create()
				.toTables(tables.toArray(new String[0]))
				.asPlainItem(PeContent.item(bag))
				.withComponent(DataComponents.CUSTOM_DATA, CustomData.of(marker))
				.chance(chance)
				.weight(1)
				.quality(1));
	}
}
