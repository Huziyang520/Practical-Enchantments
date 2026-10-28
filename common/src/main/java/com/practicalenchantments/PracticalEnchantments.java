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
import com.practicalenchantments.enchantment.armor.AerialHasteEnchantment;
import com.practicalenchantments.enchantment.armor.BrightEnchantment;
import com.practicalenchantments.enchantment.armor.ChineseEnchantment;
import com.practicalenchantments.enchantment.armor.DemonicPactEnchantment;
import com.practicalenchantments.enchantment.armor.GentleDescentEnchantment;
import com.practicalenchantments.enchantment.armor.ObsidianWalkerEnchantment;
import com.practicalenchantments.enchantment.armor.ObsidianWalkerLoot;
import com.practicalenchantments.enchantment.armor.UndyingDropEnchantment;
import com.practicalenchantments.enchantment.linked.LinkedEnchantments;
import com.practicalenchantments.enchantment.support.AiRules;
import com.practicalenchantments.enchantment.tool.DestructionEnchantment;
import com.practicalenchantments.enchantment.tool.LumberjackEnchantment;
import com.practicalenchantments.enchantment.weapon.BeheadingEnchantment;
import com.practicalenchantments.enchantment.weapon.ExecuteEnchantment;
import com.practicalenchantments.enchantment.weapon.FrenzyEnchantment;
import com.practicalenchantments.enchantment.weapon.HeavyBlowEnchantment;
import com.practicalenchantments.enchantment.weapon.InvisibleAirEnchantment;
import com.practicalenchantments.enchantment.weapon.IncinerateEnchantment;
import com.practicalenchantments.enchantment.weapon.LifeStealEnchantment;
import com.practicalenchantments.enchantment.weapon.PenetrationEnchantment;
import com.practicalenchantments.enchantment.weapon.PowerfulEnchantment;
import com.practicalenchantments.enchantment.weapon.ProvocationEnchantment;
import com.practicalenchantments.enchantment.weapon.SmashingEnchantment;
import com.practicalenchantments.enchantment.weapon.SoulSiphonEnchantment;
import com.practicalenchantments.enchantment.weapon.VenomEnchantment;
import com.practicalenchantments.enchantment.weapon.WitherAspectEnchantment;
import com.practicalenchantments.notice.MerlinLibNotice;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PracticalEnchantments {
	public static final String MOD_ID = "practical_enchantments";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final int QUALITY_COMMON = 0;
	private static final int QUALITY_RARE = 1;
	private static final int QUALITY_TREASURE = 2;
	private static boolean lootRegistered;
	private static boolean callbacksRegistered;

	private PracticalEnchantments() {
	}

	public static void bootstrap() {
		MerlinLibNotice.registerEarly();
		PeContent.register();
		AmuletScanSource.register();
		LinkedEnchantments.registerContent();
		WrathContent.register();
		MerlinApi.lifecycle().onServerStarting(server -> registerLootInjections());

		MerlinApi.lifecycle().onServerStarted(server -> {
			PracticalConfig.load();
			MerlinLibNotice.register();
			registerEventCallbacks(server.registryAccess());
			AiRules.register(server.registryAccess());
		});
	}

	public static void registerLootInjections() {
		if (lootRegistered) {
			return;
		}
		try {
			registerAllLoot();
			lootRegistered = true;
		} catch (RuntimeException error) {
			LOGGER.error("[PracticalEnchantments] 战利品注入注册失败，下次开服会重试", error);
		}
	}

	private static void registerAllLoot() {
		registerLoot(QUALITY_TREASURE, 0.12F, "minecraft:chests/woodland_mansion", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/bastion_treasure", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/stronghold_library", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/end_city_treasure", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/pillager_outpost", "practical_enchantments:disguise");
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.SIMPLE_DUNGEON, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.ABANDONED_MINESHAFT, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.VILLAGE_WEAPONSMITH, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.DESERT_PYRAMID, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.JUNGLE_TEMPLE, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.WOODLAND_MANSION, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.SHIPWRECK_TREASURE, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.TRIAL_CHAMBERS_REWARD_COMMON, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.STRONGHOLD_CORRIDOR, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.VILLAGE_ARMORER, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.RUINED_PORTAL, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.UNDERWATER_RUIN_BIG, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.NETHER_BRIDGE, ProvocationEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.BASTION_TREASURE, ProvocationEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.10F, LootTables.VILLAGE_WEAPONSMITH, HeavyBlowEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.PILLAGER_OUTPOST, HeavyBlowEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.06F, LootTables.SIMPLE_DUNGEON, HeavyBlowEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.05F, LootTables.BASTION_TREASURE, HeavyBlowEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.12F, LootTables.WOODLAND_MANSION, "practical_enchantments:beheading");
		registerLoot(QUALITY_RARE, 0.08F, LootTables.PILLAGER_OUTPOST, "practical_enchantments:beheading");
		registerLoot(QUALITY_TREASURE, 0.18F, LootTables.WOODLAND_MANSION, "practical_enchantments:frenzy");
		registerLoot(QUALITY_TREASURE, 0.14F, LootTables.PILLAGER_OUTPOST, "practical_enchantments:frenzy");
		registerLoot(QUALITY_COMMON, 0.18F, LootTables.STRONGHOLD_LIBRARY, "practical_enchantments:life_steal");
		registerLoot(QUALITY_COMMON, 0.24F, LootTables.STRONGHOLD_LIBRARY, "practical_enchantments:soul_siphon");
		registerLoot(QUALITY_COMMON, 0.28F, LootTables.JUNGLE_TEMPLE, "practical_enchantments:venom");
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.SHIPWRECK_TREASURE, "practical_enchantments:venom");
		registerLoot(QUALITY_COMMON, 0.22F, LootTables.SHIPWRECK_TREASURE, "practical_enchantments:penetration");
		registerLoot(QUALITY_COMMON, 0.20F, LootTables.UNDERWATER_RUIN_BIG, "practical_enchantments:smashing");
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.SHIPWRECK_TREASURE, "practical_enchantments:smashing");
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.ABANDONED_MINESHAFT, "practical_enchantments:smashing");
		registerLoot(QUALITY_TREASURE, 0.22F, LootTables.NETHER_BRIDGE, "practical_enchantments:wither_aspect");
		registerLoot(QUALITY_TREASURE, 0.15F, LootTables.BASTION_TREASURE, "practical_enchantments:wither_aspect");
		registerLoot(QUALITY_TREASURE, 0.12F, LootTables.ANCIENT_CITY, "practical_enchantments:wither_aspect");
		registerLoot(QUALITY_TREASURE, 0.14F, LootTables.ANCIENT_CITY, "practical_enchantments:demonic_pact");
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.BURIED_TREASURE, "practical_enchantments:demonic_pact");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/jungle_temple", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/shipwreck_treasure", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.08F, "minecraft:chests/desert_pyramid", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.06F, "minecraft:chests/simple_dungeon", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/igloo_chest", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/shipwreck_supply", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/simple_dungeon", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/abandoned_mineshaft", "practical_enchantments:cat_attraction");
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
		ObsidianWalkerLoot.register();
	}

	public static void registerEventCallbacks(HolderLookup.Provider registries) {
		if (callbacksRegistered) {
			return;
		}
		callbacksRegistered = true;
		EnchantmentEventRegistrar registrar = EnchantmentEventRegistrar.INSTANCE;
		ChineseEnchantment.registerCallbacks(registrar, registries);
		LumberjackEnchantment.registerCallbacks(registrar, registries);
		PowerfulEnchantment.registerCallbacks(registrar, registries);
		BrightEnchantment.registerCallbacks(registrar, registries);
		DestructionEnchantment.registerCallbacks(registrar, registries);
		IncinerateEnchantment.registerCallbacks(registrar, registries);
		AerialHasteEnchantment.registerCallbacks(registrar, registries);
		BeheadingEnchantment.registerCallbacks(registrar, registries);
		VenomEnchantment.registerCallbacks(registrar, registries);
		WitherAspectEnchantment.registerCallbacks(registrar, registries);
		FrenzyEnchantment.registerCallbacks(registrar, registries);
		GentleDescentEnchantment.registerCallbacks(registrar, registries);
		ExecuteEnchantment.registerCallbacks(registrar, registries);
		SoulSiphonEnchantment.registerCallbacks(registrar, registries);
		PenetrationEnchantment.registerCallbacks(registrar, registries);
		ObsidianWalkerEnchantment.registerCallbacks(registrar, registries);
		InvisibleAirEnchantment.registerCallbacks(registrar, registries);
		HeavyBlowEnchantment.registerCallbacks(registries);
		DemonicPactEnchantment.register(registries);
		UndyingDropEnchantment.register(registries);
		LinkedEnchantments.registerCallbacks(registrar, registries);
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

	public static Holder<Enchantment> resolveEnchantment(HolderLookup.Provider registries, String id) {
		Identifier enchantId = Identifier.parse(id);
		ResourceKey<Enchantment> resourceKey = ResourceKey.create(Registries.ENCHANTMENT, enchantId);
		return registries.lookupOrThrow(Registries.ENCHANTMENT)
			.get(resourceKey)
			.orElseThrow(() -> new IllegalStateException("Practical Enchantments: 附魔未注册: " + id));
	}
}
