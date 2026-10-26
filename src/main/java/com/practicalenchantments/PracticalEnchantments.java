// Practical Enchantments 模组入口（前置：MerlinLib）。

package com.practicalenchantments;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.LootTables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.config.PracticalConfig;
import com.practicalenchantments.content.AmuletScanSource;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.WrathContent;
import com.practicalenchantments.enchantment.*;
import com.practicalenchantments.enchantment.linked.LinkedEnchantments;
import com.practicalenchantments.notice.MerlinLibNotice;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public final class PracticalEnchantments {
	public static final String MOD_ID = "practical_enchantments";
	private static final int QUALITY_COMMON = 0;
	private static final int QUALITY_RARE = 1;
	private static final int QUALITY_TREASURE = 2;
	private static boolean lootRegistered;
	private static boolean callbacksRegistered;
	private static boolean enchantmentsRegistered;

	private PracticalEnchantments() {
	}

	public static void bootstrap() {
		registerEnchantments();
		PeContent.register();
		AmuletScanSource.register();
		WrathContent.register();
		LinkedEnchantments.registerContent();
		MerlinLibNotice.registerEarly();
		MerlinApi.lifecycle().onServerStarting(server -> registerLootInjections());

		MerlinApi.lifecycle().onServerStarted(server -> {
			PracticalConfig.load();
			MerlinLibNotice.register();
			registerEventCallbacks();
			AiRules.register();
		});
	}

	private static void registerEnchantments() {
		if (enchantmentsRegistered) {
			return;
		}
		enchantmentsRegistered = true;
		EnchantmentDefinitions.registerAll();
	}

	public static void registerLootInjections() {
		if (lootRegistered) {
			return;
		}

		registerLoot(QUALITY_TREASURE, 0.12F, "minecraft:chests/woodland_mansion", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/bastion_treasure", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/stronghold_library", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/end_city_treasure", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/pillager_outpost", "practical_enchantments:disguise");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/jungle_temple", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/shipwreck_treasure", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.08F, "minecraft:chests/desert_pyramid", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.06F, "minecraft:chests/simple_dungeon", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/igloo_chest", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/shipwreck_supply", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/simple_dungeon", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/abandoned_mineshaft", "practical_enchantments:cat_attraction");
		lootRegistered = true;
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.ANCIENT_CITY, ChineseEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.VILLAGE_TOOLSMITH, LumberjackEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.06F, LootTables.ABANDONED_MINESHAFT, LumberjackEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.05F, LootTables.PILLAGER_OUTPOST, LumberjackEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.04F, LootTables.SIMPLE_DUNGEON, LumberjackEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST, PowerfulEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.VILLAGE_FLETCHER, PowerfulEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.SIMPLE_DUNGEON, PowerfulEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.05F, LootTables.BASTION_TREASURE, PowerfulEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.ABANDONED_MINESHAFT, BrightEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.SIMPLE_DUNGEON, BrightEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.07F, LootTables.SHIPWRECK_TREASURE, BrightEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.IGLOO_CHEST, BrightEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.NETHER_BRIDGE, DestructionEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.15F, LootTables.WOODLAND_MANSION, DestructionEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.BASTION_TREASURE, DestructionEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.PILLAGER_OUTPOST, DestructionEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.35F, LootTables.JUNGLE_TEMPLE, IncinerateEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.DESERT_PYRAMID, IncinerateEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.UNDERWATER_RUIN_BIG, IncinerateEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.WOODLAND_MANSION, IncinerateEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.30F, LootTables.RUINED_PORTAL, AerialHasteEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.20F, LootTables.SHIPWRECK_TREASURE, AerialHasteEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.10F, LootTables.END_CITY_TREASURE, AerialHasteEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.ANCIENT_CITY, AerialHasteEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.12F, 5, LootTables.WOODLAND_MANSION, BeheadingEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.08F, 3, LootTables.PILLAGER_OUTPOST, BeheadingEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.22F, 4, LootTables.NETHER_BRIDGE, WitherAspectEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.15F, 3, LootTables.BASTION_TREASURE, WitherAspectEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.12F, 2, LootTables.ANCIENT_CITY, WitherAspectEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.18F, 4, LootTables.WOODLAND_MANSION, FrenzyEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.14F, 3, LootTables.PILLAGER_OUTPOST, FrenzyEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.20F, 3, LootTables.ANCIENT_CITY, ExecuteEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.15F, 2, LootTables.END_CITY_TREASURE, ExecuteEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, 2, LootTables.WOODLAND_MANSION, ExecuteEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.14F, 2, LootTables.ANCIENT_CITY, DemonicPactEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, 2, LootTables.BURIED_TREASURE, DemonicPactEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.18F, 2, LootTables.STRONGHOLD_LIBRARY, LifeStealEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.24F, 3, LootTables.STRONGHOLD_LIBRARY, SoulSiphonEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.28F, 5, LootTables.JUNGLE_TEMPLE, VenomEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.16F, 3, LootTables.SHIPWRECK_TREASURE, VenomEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.22F, 4, LootTables.SHIPWRECK_TREASURE, PenetrationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.20F, 3, LootTables.UNDERWATER_RUIN_BIG, SmashingEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.14F, 2, LootTables.SHIPWRECK_TREASURE, SmashingEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.12F, 2, LootTables.ABANDONED_MINESHAFT, SmashingEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.SIMPLE_DUNGEON, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.ABANDONED_MINESHAFT, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.PILLAGER_OUTPOST, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.VILLAGE_WEAPONSMITH, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.BASTION_TREASURE, ProvocationEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.END_CITY_TREASURE, ObsidianWalkerEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.BASTION_TREASURE, ObsidianWalkerEnchantment.ID);
	}

	public static void registerEventCallbacks() {
		if (callbacksRegistered) {
			return;
		}
		callbacksRegistered = true;
		EnchantmentEventRegistrar registrar = EnchantmentEventRegistrar.INSTANCE;
		ChineseEnchantment.registerCallbacks(registrar);
		LumberjackEnchantment.registerCallbacks(registrar);
		PowerfulEnchantment.registerCallbacks(registrar);
		BrightEnchantment.registerCallbacks(registrar);
		DestructionEnchantment.registerCallbacks(registrar);
		IncinerateEnchantment.registerCallbacks(registrar);
		AerialHasteEnchantment.registerCallbacks(registrar);
		BeheadingEnchantment.registerCallbacks(registrar);
		VenomEnchantment.registerCallbacks(registrar);
		WitherAspectEnchantment.registerCallbacks(registrar);
		FrenzyEnchantment.registerCallbacks(registrar);
		GentleDescentEnchantment.registerCallbacks(registrar);
		ExecuteEnchantment.registerCallbacks(registrar);
		SoulSiphonEnchantment.registerCallbacks(registrar);
		PenetrationEnchantment.registerCallbacks(registrar);
		ObsidianWalkerEnchantment.registerCallbacks(registrar);
		LinkedEnchantments.registerCallbacks(registrar);
	}

	private static void registerLoot(int quality, float chance, String lootTable, String enchantmentId) {
		registerLoot(quality, chance, 1, lootTable, enchantmentId);
	}

	private static void registerLoot(int quality, float chance, int weight, String lootTable,
									 String enchantmentId) {
		MerlinApi.loot().register(LootInjectionBuilder.create()
			.toTables(lootTable)
			.asBook()
			.withEnchantments(enchantmentId)
			.chance(chance)
			.weight(weight)
			.quality(quality));
	}

	public static Enchantment resolveEnchantment(String id) {
		ResourceLocation enchantId = new ResourceLocation(id);
		Enchantment enchantment = net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.get(enchantId);
		if (enchantment == null) {
			throw new IllegalStateException("Practical Enchantments: 附魔未注册: " + id);
		}
		return enchantment;
	}
}
