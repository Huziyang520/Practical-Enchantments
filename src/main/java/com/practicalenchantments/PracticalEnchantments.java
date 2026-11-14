// Practical Enchantments 模组入口（前置：MerlinLib）。

package com.practicalenchantments;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.LootTables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.config.PracticalConfig;
import com.practicalenchantments.content.AmuletScanSource;
import com.practicalenchantments.content.BleedContent;
import com.practicalenchantments.content.RotContent;
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
		BleedContent.register();
		RotContent.register();
		AmuletScanSource.register();
		WrathContent.register();
		LinkedEnchantments.registerContent();
		MerlinLibNotice.registerEarly();
		registerLootInjections();

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

		registerLoot(QUALITY_TREASURE, 0.12F, "minecraft:chests/woodland_mansion");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/bastion_treasure");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/stronghold_library");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/end_city_treasure");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/pillager_outpost");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/jungle_temple");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/shipwreck_treasure");
		registerLoot(QUALITY_RARE, 0.08F, "minecraft:chests/desert_pyramid");
		registerLoot(QUALITY_RARE, 0.06F, "minecraft:chests/simple_dungeon");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/igloo_chest");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/shipwreck_supply");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/simple_dungeon");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/abandoned_mineshaft");
		lootRegistered = true;
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.VILLAGE_TOOLSMITH);
		registerLoot(QUALITY_RARE, 0.06F, LootTables.ABANDONED_MINESHAFT);
		registerLoot(QUALITY_RARE, 0.05F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_RARE, 0.04F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.VILLAGE_FLETCHER);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_COMMON, 0.05F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.ABANDONED_MINESHAFT);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_COMMON, 0.07F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.IGLOO_CHEST);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.NETHER_BRIDGE);
		registerLoot(QUALITY_TREASURE, 0.15F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_TREASURE, 0.35F, LootTables.JUNGLE_TEMPLE);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.DESERT_PYRAMID);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.UNDERWATER_RUIN_BIG);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_RARE, 0.30F, LootTables.RUINED_PORTAL);
		registerLoot(QUALITY_RARE, 0.20F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_RARE, 0.10F, LootTables.END_CITY_TREASURE);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.12F, 5, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_TREASURE, 0.08F, 3, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_TREASURE, 0.22F, 4, LootTables.NETHER_BRIDGE);
		registerLoot(QUALITY_TREASURE, 0.15F, 3, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.12F, 2, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.18F, 4, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_TREASURE, 0.14F, 3, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_TREASURE, 0.20F, 3, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.15F, 2, LootTables.END_CITY_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.10F, 2, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_TREASURE, 0.14F, 2, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.10F, 2, LootTables.BURIED_TREASURE);
		registerLoot(QUALITY_COMMON, 0.18F, 2, LootTables.STRONGHOLD_LIBRARY);
		registerLoot(QUALITY_COMMON, 0.24F, 3, LootTables.STRONGHOLD_LIBRARY);
		registerLoot(QUALITY_RARE, 0.28F, 5, LootTables.JUNGLE_TEMPLE);
		registerLoot(QUALITY_RARE, 0.16F, 3, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_RARE, 0.22F, 4, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.20F, 3, LootTables.UNDERWATER_RUIN_BIG);
		registerLoot(QUALITY_COMMON, 0.14F, 2, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.12F, 2, LootTables.ABANDONED_MINESHAFT);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.ABANDONED_MINESHAFT);
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.VILLAGE_WEAPONSMITH);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.DESERT_PYRAMID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.JUNGLE_TEMPLE);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.STRONGHOLD_CORRIDOR);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.VILLAGE_ARMORER);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.RUINED_PORTAL);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.UNDERWATER_RUIN_BIG);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.NETHER_BRIDGE);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_RARE, 0.10F, LootTables.VILLAGE_WEAPONSMITH);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_RARE, 0.06F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_RARE, 0.05F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_RARE, 0.12F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_TREASURE, 0.18F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_TREASURE, 0.14F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_COMMON, 0.18F, LootTables.STRONGHOLD_LIBRARY);
		registerLoot(QUALITY_COMMON, 0.24F, LootTables.STRONGHOLD_LIBRARY);
		registerLoot(QUALITY_COMMON, 0.28F, LootTables.JUNGLE_TEMPLE);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.22F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.20F, LootTables.UNDERWATER_RUIN_BIG);
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.ABANDONED_MINESHAFT);
		registerLoot(QUALITY_TREASURE, 0.22F, LootTables.NETHER_BRIDGE);
		registerLoot(QUALITY_TREASURE, 0.15F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.12F, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.14F, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.BURIED_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.15F, LootTables.END_CITY_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.END_CITY_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.VILLAGE_FLETCHER);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_RARE, 0.06F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_RARE, 0.10F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_COMMON, 0.18F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.ABANDONED_MINESHAFT);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.VILLAGE_ARMORER);
		registerLoot(QUALITY_COMMON, 0.15F, LootTables.DESERT_PYRAMID);
		registerLoot(QUALITY_COMMON, 0.15F, LootTables.JUNGLE_TEMPLE);
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.IGLOO_CHEST);
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.SHIPWRECK_SUPPLY);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.STRONGHOLD_CORRIDOR);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.RUINED_PORTAL);
		registerLoot(QUALITY_TREASURE, 0.15F, LootTables.ANCIENT_CITY);
		registerLoot(QUALITY_TREASURE, 0.12F, LootTables.END_CITY_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.12F, LootTables.NETHER_BRIDGE);
		registerLoot(QUALITY_TREASURE, 0.12F, LootTables.BASTION_TREASURE);
		registerLoot(QUALITY_TREASURE, 0.12F, LootTables.PILLAGER_OUTPOST);
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
		TraumaEnchantment.registerCallbacks(registrar);
		WitherAspectEnchantment.registerCallbacks(registrar);
		FrenzyEnchantment.registerCallbacks(registrar);
		GentleDescentEnchantment.registerCallbacks(registrar);
		ExecuteEnchantment.registerCallbacks(registrar);
		SoulSiphonEnchantment.registerCallbacks(registrar);
		PenetrationEnchantment.registerCallbacks(registrar);
		ObsidianWalkerEnchantment.registerCallbacks(registrar);
		LinkedEnchantments.registerCallbacks(registrar);
	}

	private static void registerLoot(int quality, float chance, String lootTable) {
		registerLoot(quality, chance, 1, lootTable);
	}

	private static void registerLoot(int quality, float chance, int weight, String lootTable) {
		MerlinApi.loot().register(LootInjectionBuilder.create()
			.toTables(lootTable)
			.asRandomBook()
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
