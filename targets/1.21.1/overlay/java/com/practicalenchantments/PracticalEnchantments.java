// Practical Enchantments 模组入口（前置：MerlinLib）。

package com.practicalenchantments;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.LootTables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.platform.Services;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.config.PracticalConfig;
import com.practicalenchantments.content.AmuletScanSource;
import com.practicalenchantments.content.AmuletLoot;
import com.practicalenchantments.content.BleedContent;
import com.practicalenchantments.content.BookBagLoot;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.content.PeEvolution;
import com.practicalenchantments.content.RotContent;
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
import com.practicalenchantments.enchantment.weapon.IncinerateEnchantment;
import com.practicalenchantments.enchantment.weapon.LifeStealEnchantment;
import com.practicalenchantments.enchantment.weapon.PenetrationEnchantment;
import com.practicalenchantments.enchantment.weapon.PowerfulEnchantment;
import com.practicalenchantments.enchantment.weapon.ProvocationEnchantment;
import com.practicalenchantments.enchantment.weapon.SmashingEnchantment;
import com.practicalenchantments.enchantment.weapon.SoulSiphonEnchantment;
import com.practicalenchantments.enchantment.weapon.TraumaEnchantment;
import com.practicalenchantments.enchantment.weapon.VenomEnchantment;
import com.practicalenchantments.enchantment.weapon.WitherAspectEnchantment;
import com.practicalenchantments.enchantment.weapon.ZeusPoseidonEnchantment;
import com.practicalenchantments.notice.MerlinLibNotice;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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

	public static void info(String message, Object... arguments) {
		LOGGER.info(message, arguments);
	}

	public static void bootstrap() {
		MerlinLibNotice.registerEarly();
		PeContent.register();
		BleedContent.register();
		RotContent.register();
		AmuletScanSource.register();
		LinkedEnchantments.registerContent();
		if (Services.PLATFORM.isModLoaded(LinkedEnchantments.TWILIGHT_FOREST)) {
			com.practicalenchantments.enchantment.linked.twilightforest.HuntingArrow.registerContent();
		}
		WrathContent.register();
		PeEvolution.register();
		AmuletLoot.register();
		BookBagLoot.register();
		registerLootInjections();

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
		registerLoot(QUALITY_TREASURE, 0.12F, "minecraft:chests/woodland_mansion");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/bastion_treasure");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/stronghold_library");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/end_city_treasure");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/pillager_outpost");
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.SIMPLE_DUNGEON);
		registerLoot(QUALITY_COMMON, 0.16F, LootTables.ABANDONED_MINESHAFT);
		registerLoot(QUALITY_COMMON, 0.14F, LootTables.VILLAGE_WEAPONSMITH);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.DESERT_PYRAMID);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.JUNGLE_TEMPLE);
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.WOODLAND_MANSION);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.SHIPWRECK_TREASURE);
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.TRIAL_CHAMBERS_REWARD_COMMON);
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
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/jungle_temple");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/shipwreck_treasure");
		registerLoot(QUALITY_RARE, 0.08F, "minecraft:chests/desert_pyramid");
		registerLoot(QUALITY_RARE, 0.06F, "minecraft:chests/simple_dungeon");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/igloo_chest");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/shipwreck_supply");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/simple_dungeon");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/abandoned_mineshaft");
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
		registerLoot(QUALITY_TREASURE, 0.15F, "minecraft:chests/abandoned_camp_common_chest");
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
		TraumaEnchantment.registerCallbacks(registrar, registries);
		WitherAspectEnchantment.registerCallbacks(registrar, registries);
		FrenzyEnchantment.registerCallbacks(registrar, registries);
		GentleDescentEnchantment.registerCallbacks(registrar, registries);
		ExecuteEnchantment.registerCallbacks(registrar, registries);
		SoulSiphonEnchantment.registerCallbacks(registrar, registries);
		PenetrationEnchantment.registerCallbacks(registrar, registries);
		ObsidianWalkerEnchantment.registerCallbacks(registrar, registries);
		HeavyBlowEnchantment.registerCallbacks(registries);
		DemonicPactEnchantment.register(registries);
		UndyingDropEnchantment.register(registries);
		LinkedEnchantments.registerCallbacks(registrar, registries);
		ZeusPoseidonEnchantment.registerCallbacks(registrar, registries);
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

	public static Holder<Enchantment> resolveEnchantment(HolderLookup.Provider registries, String id) {
		ResourceLocation enchantId = ResourceLocation.parse(id);
		ResourceKey<Enchantment> resourceKey = ResourceKey.create(Registries.ENCHANTMENT, enchantId);
		return registries.lookupOrThrow(Registries.ENCHANTMENT)
			.get(resourceKey)
			.orElseThrow(() -> new IllegalStateException("Practical Enchantments: 附魔未注册: " + id));
	}
}
