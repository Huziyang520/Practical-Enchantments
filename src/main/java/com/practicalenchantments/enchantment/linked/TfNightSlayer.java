package com.practicalenchantments.enchantment.linked;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

/**
 * 暮色杀手：暮色森林维度处于夜晚时，剑与斧造成额外伤害。
 */
public final class TfNightSlayer {

	public static final String PATH = "tf_night_slayer";
	public static final String ID = PracticalEnchantments.MOD_ID + ":" + PATH;

	/** 暮色森林维度的 id。 */
	private static final ResourceLocation DIMENSION = new ResourceLocation("twilightforest", "twilight_forest");
	/** 夜晚在一天中的区间，与原版睡觉判定同一口径。 */
	private static final long NIGHT_START = 13000L;
	private static final long NIGHT_END = 23000L;
	private static final long DAY_LENGTH = 24000L;

	/** 战利品箱候选表；缺失的表只会记一条日志。 */
	private static final String[] LOOT_TABLES = {
			"twilightforest:chests/hollow_hill",
			"twilightforest:chests/labyrinth",
			"twilightforest:chests/dark_tower",
			"twilightforest:chests/aurora_palace",
	};

	private TfNightSlayer() {
	}

	static void registerContent() {
		MerlinApi.enchantments().register(new ResourceLocation(PracticalEnchantments.MOD_ID, PATH))
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

	static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
				BuiltInEvents.MODIFY_DAMAGE, TfNightSlayer::onDamage);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		if (!isNight(event.level())) {
			return;
		}
		event.damage().add(2.0F + 1.5F * (ctx.level() - 1));
	}

	private static boolean isNight(ServerLevel level) {
		if (!level.dimension().location().equals(DIMENSION)) {
			return false;
		}
		long time = level.getDayTime() % DAY_LENGTH;
		return time >= NIGHT_START && time <= NIGHT_END;
	}
}
