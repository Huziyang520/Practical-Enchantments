// 全部配置项的唯一登记表：NeoForge / Forge / Fabric 三端都从这里生成界面与默认值。

package com.practicalenchantments.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConfigSchema {
	public static final String SECTION_VANILLA_TWEAKS = "vanilla_tweaks";
	public static final String SECTION_ARMOR = "armor";
	public static final String SECTION_WEAPON = "weapon";
	public static final String SECTION_TOOL = "tool";
	public static final String SECTION_EFFECT = "effect";
	public static final String SECTION_LINKED = "linked";
	public static final String SECTION_EVOLUTION = "evolution_table";

	private static final List<ConfigOption> ALL = List.of(
			ConfigOption.bool("vanilla_tweaks", "infinity_crossbow", false),
			ConfigOption.bool("vanilla_tweaks", "infinity_no_arrow", true),
			ConfigOption.bool("vanilla_tweaks", "infinity_spectral", true),
			ConfigOption.bool("vanilla_tweaks", "quick_charge_bow", false),
			ConfigOption.bool("vanilla_tweaks", "power_crossbow", true),
			ConfigOption.bool("vanilla_tweaks", "thorns_shield", true),
			ConfigOption.integer("armor", "bright.night_vision_seconds", 20, 1, 120),
			ConfigOption.integer("armor", "bright.refresh_seconds", 1, 1, 10),
			ConfigOption.decimal("armor", "bright.durability_interval_seconds", 27.5, 1, 600),
			ConfigOption.bool("armor", "bright.durability_only_at_night", true),
			ConfigOption.integer("armor", "bright.night_start", 13000, 0, 24000),
			ConfigOption.integer("armor", "bright.night_end", 23000, 0, 24000),
			ConfigOption.integer("armor", "demonic_pact.cooldown_seconds", 60, 0, 3600),
			ConfigOption.integer("armor", "demonic_pact.emerald_cost_base", 64, 0, 64),
			ConfigOption.integer("armor", "demonic_pact.emerald_cost_step", 8, 0, 64),
			ConfigOption.integer("armor", "demonic_pact.darkness_base_seconds", 20, 0, 120),
			ConfigOption.integer("armor", "demonic_pact.darkness_step", 4, 0, 20),
			ConfigOption.integer("armor", "death_save.regen_seconds", 45, 0, 300),
			ConfigOption.integer("armor", "death_save.regen_amplifier", 1, 0, 4),
			ConfigOption.integer("armor", "death_save.absorption_seconds", 5, 0, 300),
			ConfigOption.integer("armor", "death_save.absorption_amplifier", 1, 0, 4),
			ConfigOption.integer("armor", "death_save.fire_resistance_seconds", 40, 0, 300),
			ConfigOption.integer("armor", "obsidian_walker.radius", 3, 1, 7),
			ConfigOption.integer("armor", "obsidian_walker.interval_ticks", 4, 1, 20),
			ConfigOption.decimal("armor", "aerial_haste.speed_factor", 5.0, 1, 10),
			ConfigOption.bool("armor", "hide_skin.exclude_leather", true),
			ConfigOption.decimal("armor", "amulet.penalty_iron", 0.75, 0.01, 1),
			ConfigOption.decimal("armor", "amulet.penalty_golden", 0.5, 0.01, 1),
			ConfigOption.decimal("armor", "amulet.penalty_diamond", 0.25, 0.01, 1),
			ConfigOption.decimal("armor", "amulet.penalty_netherite", 0.05, 0.01, 1),
			ConfigOption.integer("armor", "amulet.repair_cap_iron", 4, 0, 40),
			ConfigOption.integer("armor", "amulet.repair_cap_golden", 3, 0, 40),
			ConfigOption.integer("armor", "amulet.repair_cap_diamond", 2, 0, 40),
			ConfigOption.integer("armor", "amulet.repair_cap_netherite", 1, 0, 40),
			ConfigOption.integer("armor", "amulet.anvil_cost_cap", 39, 1, 40),
			ConfigOption.decimal("weapon", "life_steal.heal_ratio_per_level", 0.04, 0, 1),
			ConfigOption.decimal("weapon", "beheading.chance_per_level", 0.025, 0, 1),
			ConfigOption.decimal("weapon", "beheading.wither_skeleton_base", 0.025, 0, 1),
			ConfigOption.decimal("weapon", "execute.low_hp_threshold", 0.10, 0, 1),
			ConfigOption.decimal("weapon", "execute.mid_hp_threshold", 0.35, 0, 1),
			ConfigOption.decimal("weapon", "execute.multiplier_low", 3.0, 1, 10),
			ConfigOption.decimal("weapon", "execute.multiplier_low_axe", 4.0, 1, 10),
			ConfigOption.decimal("weapon", "execute.multiplier_mid", 1.5, 1, 5),
			ConfigOption.decimal("weapon", "execute.multiplier_mid_axe", 2.0, 1, 5),
			ConfigOption.integer("weapon", "frenzy.seconds_l1", 6, 0, 60),
			ConfigOption.integer("weapon", "frenzy.seconds_l2plus", 8, 0, 60),
			ConfigOption.integer("weapon", "frenzy.axe_multiplier", 2, 1, 5),
			ConfigOption.integer("weapon", "frenzy.speed_amplifier_per_level", 1, 0, 4),
			ConfigOption.decimal("weapon", "heavy_blow.hit_cooldown_factor", 0.9, 0.1, 1),
			ConfigOption.decimal("weapon", "heavy_blow.miss_cooldown_factor", 1.6, 1, 3),
			ConfigOption.integer("weapon", "heavy_blow.extra_durability_per_hit", 1, 0, 10),
			ConfigOption.integer("weapon", "incinerate.xp_multiplier", 2, 0, 5),
			ConfigOption.decimal("weapon", "penetration.damage_base", 1.5, 0, 20),
			ConfigOption.decimal("weapon", "penetration.damage_per_level", 0.5, 0, 10),
			ConfigOption.bool("weapon", "penetration.pull_requires_loyalty", true),
			ConfigOption.decimal("weapon", "powerful.knockback_base", 0.25, 0, 2),
			ConfigOption.decimal("weapon", "powerful.knockback_per_level", 0.05, 0, 1),
			ConfigOption.integer("weapon", "powerful.slow_seconds_base", 2, 0, 30),
			ConfigOption.integer("weapon", "powerful.slow_seconds_per_level", 2, 0, 10),
			ConfigOption.integer("weapon", "powerful.bleed_layers_divisor", 2, 1, 5),
			ConfigOption.decimal("weapon", "soul_siphon.xp_per_damage", 0.2, 0, 2),
			ConfigOption.decimal("weapon", "soul_siphon.xp_cap_per_level", 2.0, 0, 20),
			ConfigOption.decimal("weapon", "swift_arrow.speed_per_level", 0.25, 0, 2),
			ConfigOption.decimal("weapon", "swift_arrow.bow_speed_cap", 3.0, 1, 10),
			ConfigOption.decimal("weapon", "quick_charge_on_bow.speed_per_level", 0.25, 0, 1),
			ConfigOption.integer("weapon", "trauma.spear_bonus_layers", 1, 0, 5),
			ConfigOption.integer("weapon", "venom.seconds_l1", 6, 0, 60),
			ConfigOption.integer("weapon", "venom.seconds_l2", 12, 0, 60),
			ConfigOption.integer("weapon", "wither_aspect.seconds_l1", 6, 0, 60),
			ConfigOption.integer("weapon", "wither_aspect.seconds_l2", 12, 0, 60),
			ConfigOption.decimal("weapon", "wither_aspect.damage_penalty_l1", 1.0, 0, 10),
			ConfigOption.decimal("weapon", "wither_aspect.damage_penalty_l2", 0.5, 0, 10),
			ConfigOption.decimal("weapon", "provocation.provoked_damage_factor", 0.10, 0, 1),
			ConfigOption.decimal("weapon", "provocation.anger_bonus_base", 1.15, 1, 5),
			ConfigOption.decimal("weapon", "provocation.anger_bonus_per_level", 0.20, 0, 2),
			ConfigOption.integer("weapon", "provocation.anger_seconds", 10, 0, 120),
			ConfigOption.decimal("weapon", "invisible_air.damage_factor", 2.0, 1, 10),
			ConfigOption.decimal("weapon", "invisible_air.sweep_factor", 2.0, 1, 10),
			ConfigOption.decimal("weapon", "invisible_air.mob_spawn_chance", 0.02, 0, 1),
			ConfigOption.integer("weapon", "repeating_crossbow.charge_cut_base", 15, 0, 100),
			ConfigOption.integer("weapon", "repeating_crossbow.charge_cut_per_level", 3, 0, 25),
			ConfigOption.integer("weapon", "repeating_crossbow.extra_ammo_per_level", 1, 0, 5),
			ConfigOption.integer("armor", "eye_of_judgment.radius_base", 12, 1, 64),
			ConfigOption.integer("armor", "eye_of_judgment.radius_per_level", 8, 0, 32),
			ConfigOption.integer("armor", "eye_of_judgment.scan_interval_ticks", 5, 1, 20),
			ConfigOption.integer("armor", "eye_of_judgment.max_targets", 256, 1, 512),
			ConfigOption.decimal("armor", "mutual_destruction.explosion_power", 6.0, 0, 10),
			ConfigOption.bool("armor", "mutual_destruction.fire", true),
			ConfigOption.bool("armor", "mutual_destruction.block_destruction", true),
			ConfigOption.bool("armor", "mutual_destruction.knockback", true),
			ConfigOption.integer("armor", "wildfire.interval_ticks", 4, 1, 20),
			ConfigOption.integer("armor", "wildfire.lifetime_ticks", 60, 10, 200),
			ConfigOption.decimal("armor", "wildfire.ignite_seconds", 2.0, 0, 10),
			ConfigOption.bool("armor", "wildfire.burn_blocks", false),
			ConfigOption.decimal("armor", "wildfire.min_move_distance", 0.05, 0.01, 1.0),
			ConfigOption.bool("armor", "intimidation.prevent_reputation_loss", true),
			ConfigOption.bool("armor", "intimidation.prevent_golem_anger", true),
			ConfigOption.integer("armor", "lightning_rod.radius", 32, 1, 128),
			ConfigOption.bool("armor", "lightning_rod.require_sky", true),
			ConfigOption.bool("armor", "lightning_rod.priority_over_rod", true),
			ConfigOption.integer("armor", "flight.airborne_durability_base", 2, 0, 20),
			ConfigOption.integer("armor", "flight.damage_step", 4, 1, 20),
			ConfigOption.decimal("weapon", "verdict.chance_base_percent", 1.0, 0, 50),
			ConfigOption.decimal("weapon", "verdict.chance_per_level_percent", 0.4, 0, 10),
			ConfigOption.integer("weapon", "verdict.trigger_hits_base", 3, 1, 20),
			ConfigOption.integer("weapon", "verdict.trigger_hits_bonus_at_level", 4, 1, 5),
			ConfigOption.decimal("weapon", "verdict.damage", 200.0, 1, 1000),
			ConfigOption.bool("weapon", "verdict.head_drop", true),
			ConfigOption.decimal("weapon", "point_blank.max_distance", 8.0, 1, 32),
			ConfigOption.decimal("weapon", "point_blank.bonus_base_percent", 10.0, 0, 100),
			ConfigOption.decimal("weapon", "point_blank.bonus_per_level_percent", 15.0, 0, 50),
			ConfigOption.decimal("weapon", "sniper.min_distance", 50.0, 10, 128),
			ConfigOption.decimal("weapon", "sniper.bonus_base_percent", 20.0, 0, 100),
			ConfigOption.decimal("weapon", "sniper.bonus_per_level_percent", 20.0, 0, 50),
			ConfigOption.decimal("weapon", "sniper.bonus_per_50_blocks_percent", 20.0, 0, 50),
			ConfigOption.decimal("weapon", "sniper.max_bonus_percent", 300.0, 0, 1000),
			ConfigOption.integer("weapon", "arrowfall.radius", 3, 1, 8),
			ConfigOption.integer("weapon", "arrowfall.base_arrows", 1, 0, 5),
			ConfigOption.integer("weapon", "arrowfall.arrows_per_level", 1, 0, 3),
			ConfigOption.decimal("weapon", "arrowfall.extra_chance_base_percent", 10.0, 0, 100),
			ConfigOption.decimal("weapon", "arrowfall.extra_chance_per_level_percent", 10.0, 0, 50),
			ConfigOption.decimal("weapon", "arrowfall.arrow_rain_chance_per_level_percent", 1.0, 0, 20),
			ConfigOption.integer("weapon", "arrowfall.arrow_rain_min", 20, 1, 100),
			ConfigOption.integer("weapon", "arrowfall.arrow_rain_max", 30, 1, 100),
			ConfigOption.integer("weapon", "arrowfall.delay_min_ticks", 10, 1, 40),
			ConfigOption.integer("weapon", "arrowfall.delay_max_ticks", 20, 1, 60),
			ConfigOption.decimal("weapon", "arrowfall.damage_factor", 0.5, 0.1, 1.0),
			ConfigOption.integer("weapon", "benevolent_strike.max_layers", 5, 1, 10),
			ConfigOption.decimal("weapon", "benevolent_strike.damage_bonus_percent", 60.0, 0, 200),
			ConfigOption.integer("weapon", "righteous_cleave.max_layers", 5, 1, 10),
			ConfigOption.decimal("weapon", "righteous_cleave.damage_bonus_percent", 70.0, 0, 200),
			ConfigOption.decimal("weapon", "scavenger.radius", 0.6, 0.1, 2.0),
			ConfigOption.integer("weapon", "scavenger.delay_ticks", 1, 0, 20),
			ConfigOption.integer("weapon", "disarm.panic_seconds_base", 4, 0, 20),
			ConfigOption.integer("weapon", "disarm.panic_seconds_per_level", 3, 0, 10),
			ConfigOption.decimal("weapon", "disarm.drop_chance_base_percent", 20.0, 0, 100),
			ConfigOption.decimal("weapon", "disarm.drop_chance_per_level_percent", 8.0, 0, 50),
			ConfigOption.decimal("weapon", "disarm.mainhand_chance_percent", 75.0, 0, 100),
			ConfigOption.bool("weapon", "disarm.boss_immune", true),
			ConfigOption.integer("tool", "destruction.xp_normal", 2, 0, 20),
			ConfigOption.integer("tool", "destruction.xp_ore", 5, 0, 40),
			ConfigOption.integer("tool", "lumberjack.max_blocks", 64, 1, 512),
			ConfigOption.bool("tool", "lumberjack.break_leaves", true),
			ConfigOption.integer("effect", "bleed.max_layers", 5, 1, 20),
			ConfigOption.integer("effect", "bleed.layer_seconds", 5, 1, 60),
			ConfigOption.decimal("effect", "bleed.damage_per_layer_per_second", 0.4, 0, 5),
			ConfigOption.integer("effect", "bleed.pulse_ticks", 10, 1, 40),
			ConfigOption.integer("effect", "rot.layer_seconds", 25, 1, 300),
			ConfigOption.integer("effect", "rot.damage_interval_ticks", 12, 1, 60),
			ConfigOption.decimal("effect", "rot.damage_per_layer", 0.25, 0, 5),
			ConfigOption.integer("effect", "rot.armor_wear_per_10_layers", 1, 0, 10),
			ConfigOption.integer("effect", "wrath.normal_seconds", 45, 1, 600),
			ConfigOption.integer("effect", "wrath.long_seconds", 75, 1, 600),
			ConfigOption.decimal("effect", "wrath.speed_bonus", 0.10, 0, 1),
			ConfigOption.decimal("effect", "wrath.damage_bonus", 0.20, 0, 1),
			ConfigOption.integer("effect", "flawed_obsidian.stages", 3, 1, 3),
			ConfigOption.integer("effect", "flawed_obsidian.delay_base_ticks", 20, 1, 200),
			ConfigOption.integer("effect", "flawed_obsidian.delay_random_ticks", 20, 0, 200),
			ConfigOption.decimal("linked", "godwood.shard_chance", 0.25, 0, 1),
			ConfigOption.integer("linked", "godwood.shard_count_max", 2, 1, 8),
			ConfigOption.decimal("linked", "sky_slayer.damage_base", 1.0, 0, 10),
			ConfigOption.decimal("linked", "sky_slayer.damage_per_level", 1.0, 0, 10),
			ConfigOption.integer("linked", "sky_slayer.slow_seconds", 5, 0, 30),
			ConfigOption.decimal("linked", "tf_night_slayer.damage_base", 2.0, 0, 10),
			ConfigOption.decimal("linked", "tf_night_slayer.damage_per_level", 1.5, 0, 10),
			ConfigOption.integer("linked", "tf_night_slayer.night_start", 13000, 0, 24000),
			ConfigOption.integer("linked", "tf_night_slayer.night_end", 23000, 0, 24000),
			ConfigOption.bool("weapon", "zeus_poseidon.require_sky", true),
			ConfigOption.integer("weapon", "zeus_poseidon.storm_strikes", 5, 1, 20),
			ConfigOption.integer("weapon", "zeus_poseidon.storm_window_seconds", 90, 5, 600),
			ConfigOption.integer("weapon", "zeus_poseidon.storm_duration_seconds", 300, 10, 3600),
			ConfigOption.integer("weapon", "zeus_poseidon.storm_cooldown_seconds", 12000, 0, 86400),
			ConfigOption.bool("evolution_table", "enabled", true),
			ConfigOption.bool("evolution_table", "absorb_drops", true),
			ConfigOption.integer("evolution_table", "base_success_percent", 75, 0, 100),
			ConfigOption.integer("evolution_table", "base_transmute_percent", 5, 0, 100),
			ConfigOption.integer("evolution_table", "lapis_cost_base", 8, 0, 64),
			ConfigOption.integer("evolution_table", "lapis_cost_step", 4, 0, 64),
			ConfigOption.integer("evolution_table", "lapis_cost_cap", 64, 1, 64),
			ConfigOption.integer("evolution_table", "ticks_per_lapis", 20, 1, 200),
			ConfigOption.integer("evolution_table", "duration_cap_ticks", 320, 20, 72000)
	);

	private static final Map<String, ConfigOption> BY_KEY;
	private static final Map<String, List<ConfigOption>> BY_SECTION = new LinkedHashMap<>();

	static {
		Map<String, ConfigOption> byKey = new LinkedHashMap<>();
		for (ConfigOption option : ALL) {
			byKey.put(option.key(), option);
			BY_SECTION.computeIfAbsent(option.section(), section -> new ArrayList<>()).add(option);
		}
		BY_KEY = Collections.unmodifiableMap(byKey);
	}

	private ConfigSchema() {
	}

	public static List<ConfigOption> all() {
		return ALL;
	}

	public static List<String> sections() {
		return List.copyOf(BY_SECTION.keySet());
	}

	public static List<ConfigOption> in(String section) {
		return BY_SECTION.getOrDefault(section, List.of());
	}

	/** 段标题的语言键。 */
	public static String langKey(String path) {
		return "practical_enchantments.configuration." + path;
	}

	/** 单项名称的语言键。 */
	public static String nameKey(ConfigOption option) {
		return langKey(option.key());
	}

	/** 单项说明的语言键。 */
	public static String tooltipKey(ConfigOption option) {
		return langKey(option.key()) + ".tooltip";
	}

	public static ConfigOption get(String key) {
		ConfigOption option = BY_KEY.get(key);
		if (option == null) {
			throw new IllegalArgumentException("未知的配置项: " + key);
		}
		return option;
	}
}
