// 全部数值配置的运行时快照：配置加载或重载时整体刷新，业务代码只读这里的静态字段。

package com.practicalenchantments.config;

public final class Tunables {
	/** 配置来源：键 → 原始文本，缺键返回 null 时用登记表里的默认值。 */
	@FunctionalInterface
	public interface ValueSource {
		String get(String key);
	}

	private static volatile boolean infinityCrossbow;
	private static volatile boolean infinityNoArrow;
	private static volatile boolean infinitySpectral;
	private static volatile boolean quickChargeBow;
	private static volatile boolean powerCrossbow;
	private static volatile boolean thornsShield;
	private static volatile int brightNightVisionSeconds;
	private static volatile int brightRefreshSeconds;
	private static volatile double brightDurabilityIntervalSeconds;
	private static volatile boolean brightDurabilityOnlyAtNight;
	private static volatile int brightNightStart;
	private static volatile int brightNightEnd;
	private static volatile int demonicPactCooldownSeconds;
	private static volatile int demonicPactEmeraldCostBase;
	private static volatile int demonicPactEmeraldCostStep;
	private static volatile int demonicPactDarknessBaseSeconds;
	private static volatile int demonicPactDarknessStep;
	private static volatile int deathSaveRegenSeconds;
	private static volatile int deathSaveRegenAmplifier;
	private static volatile int deathSaveAbsorptionSeconds;
	private static volatile int deathSaveAbsorptionAmplifier;
	private static volatile int deathSaveFireResistanceSeconds;
	private static volatile int obsidianWalkerRadius;
	private static volatile int obsidianWalkerIntervalTicks;
	private static volatile double aerialHasteSpeedFactor;
	private static volatile boolean hideSkinExcludeLeather;
	private static volatile double amuletPenaltyIron;
	private static volatile double amuletPenaltyGolden;
	private static volatile double amuletPenaltyDiamond;
	private static volatile double amuletPenaltyNetherite;
	private static volatile int amuletRepairCapIron;
	private static volatile int amuletRepairCapGolden;
	private static volatile int amuletRepairCapDiamond;
	private static volatile int amuletRepairCapNetherite;
	private static volatile int amuletAnvilCostCap;
	private static volatile double lifeStealHealRatioPerLevel;
	private static volatile double beheadingChancePerLevel;
	private static volatile double beheadingWitherSkeletonBase;
	private static volatile double executeLowHpThreshold;
	private static volatile double executeMidHpThreshold;
	private static volatile double executeMultiplierLow;
	private static volatile double executeMultiplierLowAxe;
	private static volatile double executeMultiplierMid;
	private static volatile double executeMultiplierMidAxe;
	private static volatile int frenzySecondsL1;
	private static volatile int frenzySecondsL2plus;
	private static volatile int frenzyAxeMultiplier;
	private static volatile int frenzySpeedAmplifierPerLevel;
	private static volatile double heavyBlowHitCooldownFactor;
	private static volatile double heavyBlowMissCooldownFactor;
	private static volatile int heavyBlowExtraDurabilityPerHit;
	private static volatile int incinerateXpMultiplier;
	private static volatile double penetrationDamageBase;
	private static volatile double penetrationDamagePerLevel;
	private static volatile boolean penetrationPullRequiresLoyalty;
	private static volatile double powerfulKnockbackBase;
	private static volatile double powerfulKnockbackPerLevel;
	private static volatile int powerfulSlowSecondsBase;
	private static volatile int powerfulSlowSecondsPerLevel;
	private static volatile int powerfulBleedLayersDivisor;
	private static volatile double soulSiphonXpPerDamage;
	private static volatile double soulSiphonXpCapPerLevel;
	private static volatile double swiftArrowSpeedPerLevel;
	private static volatile double swiftArrowBowSpeedCap;
	private static volatile double quickChargeOnBowSpeedPerLevel;
	private static volatile int traumaSpearBonusLayers;
	private static volatile int venomSecondsL1;
	private static volatile int venomSecondsL2;
	private static volatile int witherAspectSecondsL1;
	private static volatile int witherAspectSecondsL2;
	private static volatile double witherAspectDamagePenaltyL1;
	private static volatile double witherAspectDamagePenaltyL2;
	private static volatile double provocationProvokedDamageFactor;
	private static volatile double provocationAngerBonusBase;
	private static volatile double provocationAngerBonusPerLevel;
	private static volatile int provocationAngerSeconds;
	private static volatile double invisibleAirDamageFactor;
	private static volatile double invisibleAirSweepFactor;
	private static volatile double invisibleAirMobSpawnChance;
	private static volatile int repeatingCrossbowChargeCutBase;
	private static volatile int repeatingCrossbowChargeCutPerLevel;
	private static volatile int repeatingCrossbowExtraAmmoPerLevel;
	private static volatile int destructionXpNormal;
	private static volatile int destructionXpOre;
	private static volatile int lumberjackMaxBlocks;
	private static volatile boolean lumberjackBreakLeaves;
	private static volatile int bleedMaxLayers;
	private static volatile int bleedLayerSeconds;
	private static volatile double bleedDamagePerLayerPerSecond;
	private static volatile int bleedPulseTicks;
	private static volatile int rotLayerSeconds;
	private static volatile int rotDamageIntervalTicks;
	private static volatile double rotDamagePerLayer;
	private static volatile int rotArmorWearPer10Layers;
	private static volatile int wrathNormalSeconds;
	private static volatile int wrathLongSeconds;
	private static volatile double wrathSpeedBonus;
	private static volatile double wrathDamageBonus;
	private static volatile int flawedObsidianStages;
	private static volatile int flawedObsidianDelayBaseTicks;
	private static volatile int flawedObsidianDelayRandomTicks;
	private static volatile double godwoodShardChance;
	private static volatile int godwoodShardCountMax;
	private static volatile double skySlayerDamageBase;
	private static volatile double skySlayerDamagePerLevel;
	private static volatile int skySlayerSlowSeconds;
	private static volatile double tfNightSlayerDamageBase;
	private static volatile double tfNightSlayerDamagePerLevel;
	private static volatile int tfNightSlayerNightStart;
	private static volatile int tfNightSlayerNightEnd;
	private static volatile boolean evolutionTableEnabled;
	private static volatile boolean evolutionTableAbsorbDrops;
	private static volatile int evolutionBaseSuccessPercent;
	private static volatile int evolutionBaseTransmutePercent;
	private static volatile int evolutionLapisCostBase;
	private static volatile int evolutionLapisCostStep;
	private static volatile int evolutionLapisCostCap;
	private static volatile int evolutionTicksPerLapis;
	private static volatile int evolutionDurationCapTicks;
	private static volatile boolean zeusRequireSky;
	private static volatile int zeusStormStrikes;
	private static volatile int zeusStormWindowSeconds;
	private static volatile int zeusStormDurationSeconds;
	private static volatile int zeusStormCooldownSeconds;
	private static volatile int eyeOfJudgmentRadiusBase;
	private static volatile int eyeOfJudgmentRadiusPerLevel;
	private static volatile int eyeOfJudgmentScanIntervalTicks;
	private static volatile int eyeOfJudgmentMaxTargets;
	private static volatile double mutualDestructionExplosionPower;
	private static volatile boolean mutualDestructionFire;
	private static volatile boolean mutualDestructionBlockDestruction;
	private static volatile boolean mutualDestructionKnockback;
	private static volatile int wildfireIntervalTicks;
	private static volatile int wildfireLifetimeTicks;
	private static volatile double wildfireIgniteSeconds;
	private static volatile boolean wildfireBurnBlocks;
	private static volatile double wildfireMinMoveDistance;
	private static volatile boolean intimidationPreventReputationLoss;
	private static volatile boolean intimidationPreventGolemAnger;
	private static volatile int lightningRodRadius;
	private static volatile boolean lightningRodRequireSky;
	private static volatile boolean lightningRodPriorityOverRod;
	private static volatile int flightAirborneDurabilityBase;
	private static volatile int flightDamageStep;
	private static volatile double verdictChanceBasePercent;
	private static volatile double verdictChancePerLevelPercent;
	private static volatile int verdictTriggerHitsBase;
	private static volatile int verdictTriggerHitsBonusAtLevel;
	private static volatile double verdictDamage;
	private static volatile boolean verdictHeadDrop;
	private static volatile double pointBlankMaxDistance;
	private static volatile double pointBlankBonusBasePercent;
	private static volatile double pointBlankBonusPerLevelPercent;
	private static volatile double sniperMinDistance;
	private static volatile double sniperBonusBasePercent;
	private static volatile double sniperBonusPerLevelPercent;
	private static volatile double sniperBonusPer50BlocksPercent;
	private static volatile double sniperMaxBonusPercent;
	private static volatile int arrowfallRadius;
	private static volatile int arrowfallBaseArrows;
	private static volatile int arrowfallArrowsPerLevel;
	private static volatile double arrowfallExtraChanceBasePercent;
	private static volatile double arrowfallExtraChancePerLevelPercent;
	private static volatile double arrowfallArrowRainChancePerLevelPercent;
	private static volatile int arrowfallArrowRainMin;
	private static volatile int arrowfallArrowRainMax;
	private static volatile int arrowfallDelayMinTicks;
	private static volatile int arrowfallDelayMaxTicks;
	private static volatile double arrowfallDamageFactor;
	private static volatile int benevolentMaxLayers;
	private static volatile double benevolentDamageBonusPercent;
	private static volatile int righteousMaxLayers;
	private static volatile double righteousDamageBonusPercent;
	private static volatile double scavengerRadius;
	private static volatile int scavengerDelayTicks;
	private static volatile int disarmPanicSecondsBase;
	private static volatile int disarmPanicSecondsPerLevel;
	private static volatile double disarmDropChanceBasePercent;
	private static volatile double disarmDropChancePerLevelPercent;
	private static volatile double disarmMainhandChancePercent;
	private static volatile boolean disarmBossImmune;


	static {
		applyAll(key -> null);
	}

	private Tunables() {
	}

	public static void applyAll(ValueSource source) {
		infinityCrossbow = bool(source, "vanilla_tweaks.infinity_crossbow");
		infinityNoArrow = bool(source, "vanilla_tweaks.infinity_no_arrow");
		infinitySpectral = bool(source, "vanilla_tweaks.infinity_spectral");
		quickChargeBow = bool(source, "vanilla_tweaks.quick_charge_bow");
		powerCrossbow = bool(source, "vanilla_tweaks.power_crossbow");
		thornsShield = bool(source, "vanilla_tweaks.thorns_shield");
		brightNightVisionSeconds = integer(source, "armor.bright.night_vision_seconds");
		brightRefreshSeconds = integer(source, "armor.bright.refresh_seconds");
		brightDurabilityIntervalSeconds = decimal(source, "armor.bright.durability_interval_seconds");
		brightDurabilityOnlyAtNight = bool(source, "armor.bright.durability_only_at_night");
		brightNightStart = integer(source, "armor.bright.night_start");
		brightNightEnd = integer(source, "armor.bright.night_end");
		demonicPactCooldownSeconds = integer(source, "armor.demonic_pact.cooldown_seconds");
		demonicPactEmeraldCostBase = integer(source, "armor.demonic_pact.emerald_cost_base");
		demonicPactEmeraldCostStep = integer(source, "armor.demonic_pact.emerald_cost_step");
		demonicPactDarknessBaseSeconds = integer(source, "armor.demonic_pact.darkness_base_seconds");
		demonicPactDarknessStep = integer(source, "armor.demonic_pact.darkness_step");
		deathSaveRegenSeconds = integer(source, "armor.death_save.regen_seconds");
		deathSaveRegenAmplifier = integer(source, "armor.death_save.regen_amplifier");
		deathSaveAbsorptionSeconds = integer(source, "armor.death_save.absorption_seconds");
		deathSaveAbsorptionAmplifier = integer(source, "armor.death_save.absorption_amplifier");
		deathSaveFireResistanceSeconds = integer(source, "armor.death_save.fire_resistance_seconds");
		obsidianWalkerRadius = integer(source, "armor.obsidian_walker.radius");
		obsidianWalkerIntervalTicks = integer(source, "armor.obsidian_walker.interval_ticks");
		aerialHasteSpeedFactor = decimal(source, "armor.aerial_haste.speed_factor");
		flightAirborneDurabilityBase = integer(source, "armor.flight.airborne_durability_base");
		flightDamageStep = integer(source, "armor.flight.damage_step");
		hideSkinExcludeLeather = bool(source, "armor.hide_skin.exclude_leather");
		amuletPenaltyIron = decimal(source, "armor.amulet.penalty_iron");
		amuletPenaltyGolden = decimal(source, "armor.amulet.penalty_golden");
		amuletPenaltyDiamond = decimal(source, "armor.amulet.penalty_diamond");
		amuletPenaltyNetherite = decimal(source, "armor.amulet.penalty_netherite");
		amuletRepairCapIron = integer(source, "armor.amulet.repair_cap_iron");
		amuletRepairCapGolden = integer(source, "armor.amulet.repair_cap_golden");
		amuletRepairCapDiamond = integer(source, "armor.amulet.repair_cap_diamond");
		amuletRepairCapNetherite = integer(source, "armor.amulet.repair_cap_netherite");
		amuletAnvilCostCap = integer(source, "armor.amulet.anvil_cost_cap");
		lifeStealHealRatioPerLevel = decimal(source, "weapon.life_steal.heal_ratio_per_level");
		beheadingChancePerLevel = decimal(source, "weapon.beheading.chance_per_level");
		beheadingWitherSkeletonBase = decimal(source, "weapon.beheading.wither_skeleton_base");
		executeLowHpThreshold = decimal(source, "weapon.execute.low_hp_threshold");
		executeMidHpThreshold = decimal(source, "weapon.execute.mid_hp_threshold");
		executeMultiplierLow = decimal(source, "weapon.execute.multiplier_low");
		executeMultiplierLowAxe = decimal(source, "weapon.execute.multiplier_low_axe");
		executeMultiplierMid = decimal(source, "weapon.execute.multiplier_mid");
		executeMultiplierMidAxe = decimal(source, "weapon.execute.multiplier_mid_axe");
		frenzySecondsL1 = integer(source, "weapon.frenzy.seconds_l1");
		frenzySecondsL2plus = integer(source, "weapon.frenzy.seconds_l2plus");
		frenzyAxeMultiplier = integer(source, "weapon.frenzy.axe_multiplier");
		frenzySpeedAmplifierPerLevel = integer(source, "weapon.frenzy.speed_amplifier_per_level");
		heavyBlowHitCooldownFactor = decimal(source, "weapon.heavy_blow.hit_cooldown_factor");
		heavyBlowMissCooldownFactor = decimal(source, "weapon.heavy_blow.miss_cooldown_factor");
		heavyBlowExtraDurabilityPerHit = integer(source, "weapon.heavy_blow.extra_durability_per_hit");
		incinerateXpMultiplier = integer(source, "weapon.incinerate.xp_multiplier");
		penetrationDamageBase = decimal(source, "weapon.penetration.damage_base");
		penetrationDamagePerLevel = decimal(source, "weapon.penetration.damage_per_level");
		penetrationPullRequiresLoyalty = bool(source, "weapon.penetration.pull_requires_loyalty");
		powerfulKnockbackBase = decimal(source, "weapon.powerful.knockback_base");
		powerfulKnockbackPerLevel = decimal(source, "weapon.powerful.knockback_per_level");
		powerfulSlowSecondsBase = integer(source, "weapon.powerful.slow_seconds_base");
		powerfulSlowSecondsPerLevel = integer(source, "weapon.powerful.slow_seconds_per_level");
		powerfulBleedLayersDivisor = integer(source, "weapon.powerful.bleed_layers_divisor");
		soulSiphonXpPerDamage = decimal(source, "weapon.soul_siphon.xp_per_damage");
		soulSiphonXpCapPerLevel = decimal(source, "weapon.soul_siphon.xp_cap_per_level");
		swiftArrowSpeedPerLevel = decimal(source, "weapon.swift_arrow.speed_per_level");
		swiftArrowBowSpeedCap = decimal(source, "weapon.swift_arrow.bow_speed_cap");
		quickChargeOnBowSpeedPerLevel = decimal(source, "weapon.quick_charge_on_bow.speed_per_level");
		traumaSpearBonusLayers = integer(source, "weapon.trauma.spear_bonus_layers");
		venomSecondsL1 = integer(source, "weapon.venom.seconds_l1");
		venomSecondsL2 = integer(source, "weapon.venom.seconds_l2");
		witherAspectSecondsL1 = integer(source, "weapon.wither_aspect.seconds_l1");
		witherAspectSecondsL2 = integer(source, "weapon.wither_aspect.seconds_l2");
		witherAspectDamagePenaltyL1 = decimal(source, "weapon.wither_aspect.damage_penalty_l1");
		witherAspectDamagePenaltyL2 = decimal(source, "weapon.wither_aspect.damage_penalty_l2");
		provocationProvokedDamageFactor = decimal(source, "weapon.provocation.provoked_damage_factor");
		provocationAngerBonusBase = decimal(source, "weapon.provocation.anger_bonus_base");
		provocationAngerBonusPerLevel = decimal(source, "weapon.provocation.anger_bonus_per_level");
		provocationAngerSeconds = integer(source, "weapon.provocation.anger_seconds");
		invisibleAirDamageFactor = decimal(source, "weapon.invisible_air.damage_factor");
		invisibleAirSweepFactor = decimal(source, "weapon.invisible_air.sweep_factor");
		invisibleAirMobSpawnChance = decimal(source, "weapon.invisible_air.mob_spawn_chance");
		repeatingCrossbowChargeCutBase = integer(source, "weapon.repeating_crossbow.charge_cut_base");
		repeatingCrossbowChargeCutPerLevel = integer(source, "weapon.repeating_crossbow.charge_cut_per_level");
		repeatingCrossbowExtraAmmoPerLevel = integer(source, "weapon.repeating_crossbow.extra_ammo_per_level");
		destructionXpNormal = integer(source, "tool.destruction.xp_normal");
		destructionXpOre = integer(source, "tool.destruction.xp_ore");
		lumberjackMaxBlocks = integer(source, "tool.lumberjack.max_blocks");
		lumberjackBreakLeaves = bool(source, "tool.lumberjack.break_leaves");
		bleedMaxLayers = integer(source, "effect.bleed.max_layers");
		bleedLayerSeconds = integer(source, "effect.bleed.layer_seconds");
		bleedDamagePerLayerPerSecond = decimal(source, "effect.bleed.damage_per_layer_per_second");
		bleedPulseTicks = integer(source, "effect.bleed.pulse_ticks");
		rotLayerSeconds = integer(source, "effect.rot.layer_seconds");
		rotDamageIntervalTicks = integer(source, "effect.rot.damage_interval_ticks");
		rotDamagePerLayer = decimal(source, "effect.rot.damage_per_layer");
		rotArmorWearPer10Layers = integer(source, "effect.rot.armor_wear_per_10_layers");
		wrathNormalSeconds = integer(source, "effect.wrath.normal_seconds");
		wrathLongSeconds = integer(source, "effect.wrath.long_seconds");
		wrathSpeedBonus = decimal(source, "effect.wrath.speed_bonus");
		wrathDamageBonus = decimal(source, "effect.wrath.damage_bonus");
		flawedObsidianStages = integer(source, "effect.flawed_obsidian.stages");
		flawedObsidianDelayBaseTicks = integer(source, "effect.flawed_obsidian.delay_base_ticks");
		flawedObsidianDelayRandomTicks = integer(source, "effect.flawed_obsidian.delay_random_ticks");
		godwoodShardChance = decimal(source, "linked.godwood.shard_chance");
		godwoodShardCountMax = integer(source, "linked.godwood.shard_count_max");
		skySlayerDamageBase = decimal(source, "linked.sky_slayer.damage_base");
		skySlayerDamagePerLevel = decimal(source, "linked.sky_slayer.damage_per_level");
		skySlayerSlowSeconds = integer(source, "linked.sky_slayer.slow_seconds");
		tfNightSlayerDamageBase = decimal(source, "linked.tf_night_slayer.damage_base");
		tfNightSlayerDamagePerLevel = decimal(source, "linked.tf_night_slayer.damage_per_level");
		tfNightSlayerNightStart = integer(source, "linked.tf_night_slayer.night_start");
		tfNightSlayerNightEnd = integer(source, "linked.tf_night_slayer.night_end");
		evolutionTableEnabled = bool(source, "evolution_table.enabled");
		evolutionTableAbsorbDrops = bool(source, "evolution_table.absorb_drops");
		evolutionBaseSuccessPercent = integer(source, "evolution_table.base_success_percent");
		evolutionBaseTransmutePercent = integer(source, "evolution_table.base_transmute_percent");
		evolutionLapisCostBase = integer(source, "evolution_table.lapis_cost_base");
		evolutionLapisCostStep = integer(source, "evolution_table.lapis_cost_step");
		evolutionLapisCostCap = integer(source, "evolution_table.lapis_cost_cap");
		evolutionTicksPerLapis = integer(source, "evolution_table.ticks_per_lapis");
		evolutionDurationCapTicks = integer(source, "evolution_table.duration_cap_ticks");
		zeusRequireSky = bool(source, "weapon.zeus_poseidon.require_sky");
		zeusStormStrikes = integer(source, "weapon.zeus_poseidon.storm_strikes");
		zeusStormWindowSeconds = integer(source, "weapon.zeus_poseidon.storm_window_seconds");
		zeusStormDurationSeconds = integer(source, "weapon.zeus_poseidon.storm_duration_seconds");
		zeusStormCooldownSeconds = integer(source, "weapon.zeus_poseidon.storm_cooldown_seconds");
		eyeOfJudgmentRadiusBase = integer(source, "armor.eye_of_judgment.radius_base");
		eyeOfJudgmentRadiusPerLevel = integer(source, "armor.eye_of_judgment.radius_per_level");
		eyeOfJudgmentScanIntervalTicks = integer(source, "armor.eye_of_judgment.scan_interval_ticks");
		eyeOfJudgmentMaxTargets = integer(source, "armor.eye_of_judgment.max_targets");
		mutualDestructionExplosionPower = decimal(source, "armor.mutual_destruction.explosion_power");
		mutualDestructionFire = bool(source, "armor.mutual_destruction.fire");
		mutualDestructionBlockDestruction = bool(source, "armor.mutual_destruction.block_destruction");
		mutualDestructionKnockback = bool(source, "armor.mutual_destruction.knockback");
		wildfireIntervalTicks = integer(source, "armor.wildfire.interval_ticks");
		wildfireLifetimeTicks = integer(source, "armor.wildfire.lifetime_ticks");
		wildfireIgniteSeconds = decimal(source, "armor.wildfire.ignite_seconds");
		wildfireBurnBlocks = bool(source, "armor.wildfire.burn_blocks");
		wildfireMinMoveDistance = decimal(source, "armor.wildfire.min_move_distance");
		intimidationPreventReputationLoss = bool(source, "armor.intimidation.prevent_reputation_loss");
		intimidationPreventGolemAnger = bool(source, "armor.intimidation.prevent_golem_anger");
		lightningRodRadius = integer(source, "armor.lightning_rod.radius");
		lightningRodRequireSky = bool(source, "armor.lightning_rod.require_sky");
		lightningRodPriorityOverRod = bool(source, "armor.lightning_rod.priority_over_rod");
		verdictChanceBasePercent = decimal(source, "weapon.verdict.chance_base_percent");
		verdictChancePerLevelPercent = decimal(source, "weapon.verdict.chance_per_level_percent");
		verdictTriggerHitsBase = integer(source, "weapon.verdict.trigger_hits_base");
		verdictTriggerHitsBonusAtLevel = integer(source, "weapon.verdict.trigger_hits_bonus_at_level");
		verdictDamage = decimal(source, "weapon.verdict.damage");
		verdictHeadDrop = bool(source, "weapon.verdict.head_drop");
		pointBlankMaxDistance = decimal(source, "weapon.point_blank.max_distance");
		pointBlankBonusBasePercent = decimal(source, "weapon.point_blank.bonus_base_percent");
		pointBlankBonusPerLevelPercent = decimal(source, "weapon.point_blank.bonus_per_level_percent");
		sniperMinDistance = decimal(source, "weapon.sniper.min_distance");
		sniperBonusBasePercent = decimal(source, "weapon.sniper.bonus_base_percent");
		sniperBonusPerLevelPercent = decimal(source, "weapon.sniper.bonus_per_level_percent");
		sniperBonusPer50BlocksPercent = decimal(source, "weapon.sniper.bonus_per_50_blocks_percent");
		sniperMaxBonusPercent = decimal(source, "weapon.sniper.max_bonus_percent");
		arrowfallRadius = integer(source, "weapon.arrowfall.radius");
		arrowfallBaseArrows = integer(source, "weapon.arrowfall.base_arrows");
		arrowfallArrowsPerLevel = integer(source, "weapon.arrowfall.arrows_per_level");
		arrowfallExtraChanceBasePercent = decimal(source, "weapon.arrowfall.extra_chance_base_percent");
		arrowfallExtraChancePerLevelPercent = decimal(source, "weapon.arrowfall.extra_chance_per_level_percent");
		arrowfallArrowRainChancePerLevelPercent = decimal(source, "weapon.arrowfall.arrow_rain_chance_per_level_percent");
		arrowfallArrowRainMin = integer(source, "weapon.arrowfall.arrow_rain_min");
		arrowfallArrowRainMax = integer(source, "weapon.arrowfall.arrow_rain_max");
		arrowfallDelayMinTicks = integer(source, "weapon.arrowfall.delay_min_ticks");
		arrowfallDelayMaxTicks = integer(source, "weapon.arrowfall.delay_max_ticks");
		arrowfallDamageFactor = decimal(source, "weapon.arrowfall.damage_factor");
		benevolentMaxLayers = integer(source, "weapon.benevolent_strike.max_layers");
		benevolentDamageBonusPercent = decimal(source, "weapon.benevolent_strike.damage_bonus_percent");
		righteousMaxLayers = integer(source, "weapon.righteous_cleave.max_layers");
		righteousDamageBonusPercent = decimal(source, "weapon.righteous_cleave.damage_bonus_percent");
		scavengerRadius = decimal(source, "weapon.scavenger.radius");
		scavengerDelayTicks = integer(source, "weapon.scavenger.delay_ticks");
		disarmPanicSecondsBase = integer(source, "weapon.disarm.panic_seconds_base");
		disarmPanicSecondsPerLevel = integer(source, "weapon.disarm.panic_seconds_per_level");
		disarmDropChanceBasePercent = decimal(source, "weapon.disarm.drop_chance_base_percent");
		disarmDropChancePerLevelPercent = decimal(source, "weapon.disarm.drop_chance_per_level_percent");
		disarmMainhandChancePercent = decimal(source, "weapon.disarm.mainhand_chance_percent");
		disarmBossImmune = bool(source, "weapon.disarm.boss_immune");
		// 低血线必须低于中血线：配反了就把低血线压到中线以下
		if (executeLowHpThreshold >= executeMidHpThreshold) {
			executeLowHpThreshold = Math.max(0.0D, executeMidHpThreshold - 0.01D);
		}
		if (brightNightEnd <= brightNightStart) {
			brightNightEnd = Math.min(24000, brightNightStart + 1);
		}
		if (tfNightSlayerNightEnd <= tfNightSlayerNightStart) {
			tfNightSlayerNightEnd = Math.min(24000, tfNightSlayerNightStart + 1);
		}
		VanillaTweaks.apply(infinityCrossbow, infinityNoArrow, infinitySpectral);
		VanillaTweaks.applyExtra(quickChargeBow, powerCrossbow, thornsShield);
	}

	/** 秒转 tick，所有时长类配置统一走这里换算。 */
	public static int secondsToTicks(int seconds) {
		return Math.max(0, seconds) * 20;
	}

	/** 明朗的耐久磨损间隔（tick）；默认 27.5 秒正好等于原来的 550 tick。 */
	public static int brightDurabilityIntervalTicks() {
		return Math.max(1, (int) Math.round(brightDurabilityIntervalSeconds * 20.0D));
	}

	/** 流血每层存活 tick。 */
	public static int bleedLayerTicks() {
		return secondsToTicks(bleedLayerSeconds);
	}

	/** 流血每层每 tick 伤害（配置按每秒填写）。 */
	public static float bleedDamagePerLayerTick() {
		return (float) (bleedDamagePerLayerPerSecond / 20.0D);
	}

	/** 腐毒每层存活 tick。 */
	public static int rotLayerTicks() {
		return secondsToTicks(rotLayerSeconds);
	}

	/** 恶魔交易按等级折算的绿宝石花费。 */
	public static int demonicPactCost(int level) {
		return Math.max(0, demonicPactEmeraldCostBase - demonicPactEmeraldCostStep * (level - 1));
	}

	/** 恶魔交易按等级折算的黑暗时长（tick）。 */
	public static int demonicPactDarknessTicks(int level) {
		return secondsToTicks(Math.max(0, demonicPactDarknessBaseSeconds - demonicPactDarknessStep * (level - 1)));
	}

	/** 飞行：空中受击时的额外耐久损耗。 */
	public static int flightAirborneDurability(double damage) {
		return flightAirborneDurabilityBase * Math.max(1, (int) (damage / Math.max(1, flightDamageStep)));
	}

	/** 连弩按等级折算的上弦时间缩减百分比，最低 0。 */
	public static int repeatingCrossbowChargeCut(int level) {
		return Math.max(0, repeatingCrossbowChargeCutBase - repeatingCrossbowChargeCutPerLevel * level);
	}

	/** 有瑕疵的黑曜石下一段裂纹的延迟（tick）。 */
	public static int flawedObsidianDelay(int randomBound) {
		return flawedObsidianDelayBaseTicks + (flawedObsidianDelayRandomTicks > 0 ? randomBound : 0);
	}

	private static boolean bool(ValueSource source, String key) {
		String raw = source.get(key);
		return raw == null ? ConfigSchema.get(key).defaultBool() : Boolean.parseBoolean(raw.trim());
	}

	private static int integer(ValueSource source, String key) {
		ConfigOption option = ConfigSchema.get(key);
		String raw = source.get(key);
		if (raw == null) {
			return option.defaultInt();
		}
		try {
			int value = (int) Math.round(Double.parseDouble(raw.trim()));
			return Math.max(option.intMin(), Math.min(option.intMax(), value));
		} catch (NumberFormatException invalid) {
			return option.defaultInt();
		}
	}

	private static double decimal(ValueSource source, String key) {
		ConfigOption option = ConfigSchema.get(key);
		String raw = source.get(key);
		if (raw == null) {
			return option.defaultDouble();
		}
		try {
			double value = Double.parseDouble(raw.trim());
			return Math.max(option.min(), Math.min(option.max(), value));
		} catch (NumberFormatException invalid) {
			return option.defaultDouble();
		}
	}

	public static boolean infinityCrossbow() {
		return infinityCrossbow;
	}
	public static boolean infinityNoArrow() {
		return infinityNoArrow;
	}
	public static boolean infinitySpectral() {
		return infinitySpectral;
	}
	public static boolean quickChargeBow() {
		return quickChargeBow;
	}
	public static boolean powerCrossbow() {
		return powerCrossbow;
	}
	public static boolean thornsShield() {
		return thornsShield;
	}
	public static int brightNightVisionSeconds() {
		return brightNightVisionSeconds;
	}
	public static int brightRefreshSeconds() {
		return brightRefreshSeconds;
	}
	public static double brightDurabilityIntervalSeconds() {
		return brightDurabilityIntervalSeconds;
	}
	public static boolean brightDurabilityOnlyAtNight() {
		return brightDurabilityOnlyAtNight;
	}
	public static int brightNightStart() {
		return brightNightStart;
	}
	public static int brightNightEnd() {
		return brightNightEnd;
	}
	public static int demonicPactCooldownSeconds() {
		return demonicPactCooldownSeconds;
	}
	public static int demonicPactEmeraldCostBase() {
		return demonicPactEmeraldCostBase;
	}
	public static int demonicPactEmeraldCostStep() {
		return demonicPactEmeraldCostStep;
	}
	public static int demonicPactDarknessBaseSeconds() {
		return demonicPactDarknessBaseSeconds;
	}
	public static int demonicPactDarknessStep() {
		return demonicPactDarknessStep;
	}
	public static int deathSaveRegenSeconds() {
		return deathSaveRegenSeconds;
	}
	public static int deathSaveRegenAmplifier() {
		return deathSaveRegenAmplifier;
	}
	public static int deathSaveAbsorptionSeconds() {
		return deathSaveAbsorptionSeconds;
	}
	public static int deathSaveAbsorptionAmplifier() {
		return deathSaveAbsorptionAmplifier;
	}
	public static int deathSaveFireResistanceSeconds() {
		return deathSaveFireResistanceSeconds;
	}
	public static int obsidianWalkerRadius() {
		return obsidianWalkerRadius;
	}
	public static int obsidianWalkerIntervalTicks() {
		return obsidianWalkerIntervalTicks;
	}
	public static double aerialHasteSpeedFactor() {
		return aerialHasteSpeedFactor;
	}
	public static boolean hideSkinExcludeLeather() {
		return hideSkinExcludeLeather;
	}
	public static double amuletPenaltyIron() {
		return amuletPenaltyIron;
	}
	public static double amuletPenaltyGolden() {
		return amuletPenaltyGolden;
	}
	public static double amuletPenaltyDiamond() {
		return amuletPenaltyDiamond;
	}
	public static double amuletPenaltyNetherite() {
		return amuletPenaltyNetherite;
	}
	public static int amuletRepairCapIron() {
		return amuletRepairCapIron;
	}
	public static int amuletRepairCapGolden() {
		return amuletRepairCapGolden;
	}
	public static int amuletRepairCapDiamond() {
		return amuletRepairCapDiamond;
	}
	public static int amuletRepairCapNetherite() {
		return amuletRepairCapNetherite;
	}
	public static int amuletAnvilCostCap() {
		return amuletAnvilCostCap;
	}
	public static double lifeStealHealRatioPerLevel() {
		return lifeStealHealRatioPerLevel;
	}
	public static double beheadingChancePerLevel() {
		return beheadingChancePerLevel;
	}
	public static double beheadingWitherSkeletonBase() {
		return beheadingWitherSkeletonBase;
	}
	public static double executeLowHpThreshold() {
		return executeLowHpThreshold;
	}
	public static double executeMidHpThreshold() {
		return executeMidHpThreshold;
	}
	public static double executeMultiplierLow() {
		return executeMultiplierLow;
	}
	public static double executeMultiplierLowAxe() {
		return executeMultiplierLowAxe;
	}
	public static double executeMultiplierMid() {
		return executeMultiplierMid;
	}
	public static double executeMultiplierMidAxe() {
		return executeMultiplierMidAxe;
	}
	public static int frenzySecondsL1() {
		return frenzySecondsL1;
	}
	public static int frenzySecondsL2plus() {
		return frenzySecondsL2plus;
	}
	public static int frenzyAxeMultiplier() {
		return frenzyAxeMultiplier;
	}
	public static int frenzySpeedAmplifierPerLevel() {
		return frenzySpeedAmplifierPerLevel;
	}
	public static double heavyBlowHitCooldownFactor() {
		return heavyBlowHitCooldownFactor;
	}
	public static double heavyBlowMissCooldownFactor() {
		return heavyBlowMissCooldownFactor;
	}
	public static int heavyBlowExtraDurabilityPerHit() {
		return heavyBlowExtraDurabilityPerHit;
	}
	public static int incinerateXpMultiplier() {
		return incinerateXpMultiplier;
	}
	public static double penetrationDamageBase() {
		return penetrationDamageBase;
	}
	public static double penetrationDamagePerLevel() {
		return penetrationDamagePerLevel;
	}
	public static boolean penetrationPullRequiresLoyalty() {
		return penetrationPullRequiresLoyalty;
	}
	public static double powerfulKnockbackBase() {
		return powerfulKnockbackBase;
	}
	public static double powerfulKnockbackPerLevel() {
		return powerfulKnockbackPerLevel;
	}
	public static int powerfulSlowSecondsBase() {
		return powerfulSlowSecondsBase;
	}
	public static int powerfulSlowSecondsPerLevel() {
		return powerfulSlowSecondsPerLevel;
	}
	public static int powerfulBleedLayersDivisor() {
		return powerfulBleedLayersDivisor;
	}
	public static double soulSiphonXpPerDamage() {
		return soulSiphonXpPerDamage;
	}
	public static double soulSiphonXpCapPerLevel() {
		return soulSiphonXpCapPerLevel;
	}
	public static double swiftArrowSpeedPerLevel() {
		return swiftArrowSpeedPerLevel;
	}
	public static double swiftArrowBowSpeedCap() {
		return swiftArrowBowSpeedCap;
	}
	public static double quickChargeOnBowSpeedPerLevel() {
		return quickChargeOnBowSpeedPerLevel;
	}
	public static int traumaSpearBonusLayers() {
		return traumaSpearBonusLayers;
	}
	public static int venomSecondsL1() {
		return venomSecondsL1;
	}
	public static int venomSecondsL2() {
		return venomSecondsL2;
	}
	public static int witherAspectSecondsL1() {
		return witherAspectSecondsL1;
	}
	public static int witherAspectSecondsL2() {
		return witherAspectSecondsL2;
	}
	public static double witherAspectDamagePenaltyL1() {
		return witherAspectDamagePenaltyL1;
	}
	public static double witherAspectDamagePenaltyL2() {
		return witherAspectDamagePenaltyL2;
	}
	public static double provocationProvokedDamageFactor() {
		return provocationProvokedDamageFactor;
	}
	public static double provocationAngerBonusBase() {
		return provocationAngerBonusBase;
	}
	public static double provocationAngerBonusPerLevel() {
		return provocationAngerBonusPerLevel;
	}
	public static int provocationAngerSeconds() {
		return provocationAngerSeconds;
	}
	public static double invisibleAirDamageFactor() {
		return invisibleAirDamageFactor;
	}
	public static double invisibleAirSweepFactor() {
		return invisibleAirSweepFactor;
	}
	public static double invisibleAirMobSpawnChance() {
		return invisibleAirMobSpawnChance;
	}
	public static int repeatingCrossbowChargeCutBase() {
		return repeatingCrossbowChargeCutBase;
	}
	public static int repeatingCrossbowChargeCutPerLevel() {
		return repeatingCrossbowChargeCutPerLevel;
	}
	public static int repeatingCrossbowExtraAmmoPerLevel() {
		return repeatingCrossbowExtraAmmoPerLevel;
	}
	public static int destructionXpNormal() {
		return destructionXpNormal;
	}
	public static int destructionXpOre() {
		return destructionXpOre;
	}
	public static int lumberjackMaxBlocks() {
		return lumberjackMaxBlocks;
	}
	public static boolean lumberjackBreakLeaves() {
		return lumberjackBreakLeaves;
	}
	public static int bleedMaxLayers() {
		return bleedMaxLayers;
	}
	public static int bleedLayerSeconds() {
		return bleedLayerSeconds;
	}
	public static double bleedDamagePerLayerPerSecond() {
		return bleedDamagePerLayerPerSecond;
	}
	public static int bleedPulseTicks() {
		return bleedPulseTicks;
	}
	public static int rotLayerSeconds() {
		return rotLayerSeconds;
	}
	public static int rotDamageIntervalTicks() {
		return rotDamageIntervalTicks;
	}
	public static double rotDamagePerLayer() {
		return rotDamagePerLayer;
	}
	public static int rotArmorWearPer10Layers() {
		return rotArmorWearPer10Layers;
	}
	public static int wrathNormalSeconds() {
		return wrathNormalSeconds;
	}
	public static int wrathLongSeconds() {
		return wrathLongSeconds;
	}
	public static double wrathSpeedBonus() {
		return wrathSpeedBonus;
	}
	public static double wrathDamageBonus() {
		return wrathDamageBonus;
	}
	public static int flawedObsidianStages() {
		return flawedObsidianStages;
	}
	public static int flawedObsidianDelayBaseTicks() {
		return flawedObsidianDelayBaseTicks;
	}
	public static int flawedObsidianDelayRandomTicks() {
		return flawedObsidianDelayRandomTicks;
	}
	public static double godwoodShardChance() {
		return godwoodShardChance;
	}
	public static int godwoodShardCountMax() {
		return godwoodShardCountMax;
	}
	public static double skySlayerDamageBase() {
		return skySlayerDamageBase;
	}
	public static double skySlayerDamagePerLevel() {
		return skySlayerDamagePerLevel;
	}
	public static int skySlayerSlowSeconds() {
		return skySlayerSlowSeconds;
	}
	public static double tfNightSlayerDamageBase() {
		return tfNightSlayerDamageBase;
	}
	public static double tfNightSlayerDamagePerLevel() {
		return tfNightSlayerDamagePerLevel;
	}
	public static int tfNightSlayerNightStart() {
		return tfNightSlayerNightStart;
	}
	public static int tfNightSlayerNightEnd() {
		return tfNightSlayerNightEnd;
	}

	public static boolean evolutionTableEnabled() {
		return evolutionTableEnabled;
	}

	public static boolean evolutionTableAbsorbDrops() {
		return evolutionTableAbsorbDrops;
	}

	public static int evolutionBaseSuccessPercent() {
		return evolutionBaseSuccessPercent;
	}

	public static int evolutionBaseTransmutePercent() {
		return evolutionBaseTransmutePercent;
	}

	public static int evolutionLapisCostBase() {
		return evolutionLapisCostBase;
	}

	public static int evolutionLapisCostStep() {
		return evolutionLapisCostStep;
	}

	public static int evolutionLapisCostCap() {
		return evolutionLapisCostCap;
	}

	public static int evolutionTicksPerLapis() {
		return evolutionTicksPerLapis;
	}

	public static int evolutionDurationCapTicks() {
		return evolutionDurationCapTicks;
	}

	public static boolean zeusRequireSky() {
		return zeusRequireSky;
	}

	public static int zeusStormStrikes() {
		return zeusStormStrikes;
	}

	public static int zeusStormWindowSeconds() {
		return zeusStormWindowSeconds;
	}

	public static int zeusStormDurationSeconds() {
		return zeusStormDurationSeconds;
	}

	public static int zeusStormCooldownSeconds() {
		return zeusStormCooldownSeconds;
	}

	public static int eyeOfJudgmentRadius(int level) {
		return eyeOfJudgmentRadiusBase + eyeOfJudgmentRadiusPerLevel * Math.max(0, level - 1);
	}

	public static int eyeOfJudgmentScanIntervalTicks() {
		return eyeOfJudgmentScanIntervalTicks;
	}

	public static int eyeOfJudgmentMaxTargets() {
		return eyeOfJudgmentMaxTargets;
	}

	public static double mutualDestructionExplosionPower() {
		return mutualDestructionExplosionPower;
	}

	public static boolean mutualDestructionFire() {
		return mutualDestructionFire;
	}

	public static boolean mutualDestructionBlockDestruction() {
		return mutualDestructionBlockDestruction;
	}

	public static boolean mutualDestructionKnockback() {
		return mutualDestructionKnockback;
	}

	public static int wildfireIntervalTicks() {
		return wildfireIntervalTicks;
	}

	public static int wildfireLifetimeTicks() {
		return wildfireLifetimeTicks;
	}

	public static double wildfireIgniteSeconds() {
		return wildfireIgniteSeconds;
	}

	public static boolean wildfireBurnBlocks() {
		return wildfireBurnBlocks;
	}

	public static double wildfireMinMoveDistance() {
		return wildfireMinMoveDistance;
	}

	public static boolean intimidationPreventReputationLoss() {
		return intimidationPreventReputationLoss;
	}

	public static boolean intimidationPreventGolemAnger() {
		return intimidationPreventGolemAnger;
	}

	public static int lightningRodRadius() {
		return lightningRodRadius;
	}

	public static boolean lightningRodRequireSky() {
		return lightningRodRequireSky;
	}

	public static boolean lightningRodPriorityOverRod() {
		return lightningRodPriorityOverRod;
	}

	public static double verdictChancePercent(int level) {
		return verdictChanceBasePercent + verdictChancePerLevelPercent * Math.max(0, level - 1);
	}

	public static int verdictTriggerHits(int level) {
		return verdictTriggerHitsBase + (level >= verdictTriggerHitsBonusAtLevel ? 1 : 0);
	}

	public static double verdictDamage() {
		return verdictDamage;
	}

	public static boolean verdictHeadDrop() {
		return verdictHeadDrop;
	}

	public static double pointBlankMaxDistance() {
		return pointBlankMaxDistance;
	}

	public static double pointBlankBonusPercent(int level) {
		return pointBlankBonusBasePercent + pointBlankBonusPerLevelPercent * Math.max(0, level);
	}

	public static double sniperMinDistance() {
		return sniperMinDistance;
	}

	public static double sniperBonusPercent(int level, double distance) {
		double base = sniperBonusBasePercent + sniperBonusPerLevelPercent * Math.max(0, level);
		if (distance <= sniperMinDistance) {
			return 0.0;
		}
		double extra = Math.floor((distance - sniperMinDistance) / 50.0) * sniperBonusPer50BlocksPercent;
		return Math.min(sniperMaxBonusPercent, base + extra);
	}

	public static int arrowfallRadius() {
		return arrowfallRadius;
	}

	public static int arrowfallBaseArrows() {
		return arrowfallBaseArrows;
	}

	public static int arrowfallArrowsPerLevel() {
		return arrowfallArrowsPerLevel;
	}

	public static double arrowfallExtraChancePercent(int level) {
		return arrowfallExtraChanceBasePercent + arrowfallExtraChancePerLevelPercent * Math.max(0, level - 1);
	}

	public static double arrowfallArrowRainChancePercent(int level) {
		return arrowfallArrowRainChancePerLevelPercent * Math.max(0, level);
	}

	public static int arrowfallArrowRainMin() {
		return arrowfallArrowRainMin;
	}

	public static int arrowfallArrowRainMax() {
		return arrowfallArrowRainMax;
	}

	public static int arrowfallDelayMinTicks() {
		return arrowfallDelayMinTicks;
	}

	public static int arrowfallDelayMaxTicks() {
		return arrowfallDelayMaxTicks;
	}

	public static double arrowfallDamageFactor() {
		return arrowfallDamageFactor;
	}

	public static int benevolentMaxLayers() {
		return benevolentMaxLayers;
	}

	public static double benevolentDamageBonusPercent() {
		return benevolentDamageBonusPercent;
	}

	public static int righteousMaxLayers() {
		return righteousMaxLayers;
	}

	public static double righteousDamageBonusPercent() {
		return righteousDamageBonusPercent;
	}

	public static double scavengerRadius() {
		return scavengerRadius;
	}

	public static int scavengerDelayTicks() {
		return scavengerDelayTicks;
	}

	public static int disarmPanicSeconds(int level) {
		return Math.max(0, disarmPanicSecondsBase + disarmPanicSecondsPerLevel * Math.max(0, level));
	}

	public static double disarmDropChancePercent(int level) {
		return disarmDropChanceBasePercent + disarmDropChancePerLevelPercent * Math.max(0, level - 1);
	}

	public static double disarmMainhandChancePercent() {
		return disarmMainhandChancePercent;
	}

	public static boolean disarmBossImmune() {
		return disarmBossImmune;
	}
}
