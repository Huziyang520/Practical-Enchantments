// 暮色杀手：夜晚时剑与斧造成额外伤害，任何维度都按该维度自己的世界时间判定。

package com.practicalenchantments.enchantment.linked.twilightforest;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;

public final class TfNightSlayer {
	public static final String PATH = "tf_night_slayer";
	public static final String ID = PracticalEnchantments.MOD_ID + ":" + PATH;
	private static final long NIGHT_START = 13000L;
	private static final long NIGHT_END = 23000L;
	private static final long DAY_LENGTH = 24000L;

	private static final String[] LOOT_TABLES = {
			"twilightforest:chests/hollow_hill",
			"twilightforest:chests/labyrinth",
			"twilightforest:chests/dark_tower",
			"twilightforest:chests/aurora_palace",
	};

	private TfNightSlayer() {
	}

	public static void registerContent() {
		MerlinApi.enchantments().register(Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, PATH))
				.maxLevel(5)
				.weight(2)
				.anvilCost(8)
				.slots("mainhand")
				.supportedItems("#minecraft:enchantable/sharp_weapon")
				.cost(10, 8, 40, 10)
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
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, TfNightSlayer::onDamage);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		if (!isNight(event.level())) {
			return;
		}
		event.damage().add(2.0F + 1.5F * (ctx.level() - 1));
	}

	private static boolean isNight(ServerLevel level) {
		long time = level.getDefaultClockTime() % DAY_LENGTH;
		return time >= NIGHT_START && time <= NIGHT_END;
	}
}
