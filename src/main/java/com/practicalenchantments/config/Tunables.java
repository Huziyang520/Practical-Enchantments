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
	private static volatile int gentleDescentSeconds;
	private static volatile int gentleDescentRefreshSeconds;
	private static volatile boolean gentleDescentCancelOnSneak;
	private static volatile int demonicPactCooldownSeconds;
	private static volatile int demonicPactEmeraldCostBase;
	private static volatile int demonicPactEmeraldCostStep;
	private static volatile int demonicPactDarknessBaseSeconds;
	private static volatile int demonicPactDarknessStep;
	private static volatile int undyingDropDarknessBaseSeconds;
	private static volatile int undyingDropDarknessStep;
	private static volatile int deathSaveRegenSeconds;
	private static volatile int deathSaveRegenAmplifier;
	private static volatile int deathSaveAbsorptionSeconds;
	private static volatile int deathSaveAbsorptionAmplifier;
	private static volatile int deathSaveFireResistanceSeconds;
	private static volatile int obsidianWalkerRadius;
	private static volatile int obsidianWalkerIntervalTicks;
	private static volatile double aerialHasteSpeedFactor;
	private static volatile int chineseAirborneDurabilityBase;
	private static volatile int chineseDamageStep;
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
		gentleDescentSeconds = integer(source, "armor.gentle_descent.seconds");
		gentleDescentRefreshSeconds = integer(source, "armor.gentle_descent.refresh_seconds");
		gentleDescentCancelOnSneak = bool(source, "armor.gentle_descent.cancel_on_sneak");
		demonicPactCooldownSeconds = integer(source, "armor.demonic_pact.cooldown_seconds");
		demonicPactEmeraldCostBase = integer(source, "armor.demonic_pact.emerald_cost_base");
		demonicPactEmeraldCostStep = integer(source, "armor.demonic_pact.emerald_cost_step");
		demonicPactDarknessBaseSeconds = integer(source, "armor.demonic_pact.darkness_base_seconds");
		demonicPactDarknessStep = integer(source, "armor.demonic_pact.darkness_step");
		undyingDropDarknessBaseSeconds = integer(source, "armor.undying_drop.darkness_base_seconds");
		undyingDropDarknessStep = integer(source, "armor.undying_drop.darkness_step");
		deathSaveRegenSeconds = integer(source, "armor.death_save.regen_seconds");
		deathSaveRegenAmplifier = integer(source, "armor.death_save.regen_amplifier");
		deathSaveAbsorptionSeconds = integer(source, "armor.death_save.absorption_seconds");
		deathSaveAbsorptionAmplifier = integer(source, "armor.death_save.absorption_amplifier");
		deathSaveFireResistanceSeconds = integer(source, "armor.death_save.fire_resistance_seconds");
		obsidianWalkerRadius = integer(source, "armor.obsidian_walker.radius");
		obsidianWalkerIntervalTicks = integer(source, "armor.obsidian_walker.interval_ticks");
		aerialHasteSpeedFactor = decimal(source, "armor.aerial_haste.speed_factor");
		chineseAirborneDurabilityBase = integer(source, "armor.chinese.airborne_durability_base");
		chineseDamageStep = integer(source, "armor.chinese.damage_step");
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

	/** 掉落不死亡按等级折算的黑暗时长（tick）。 */
	public static int undyingDropDarknessTicks(int level) {
		return secondsToTicks(Math.max(0, undyingDropDarknessBaseSeconds - undyingDropDarknessStep * (level - 1)));
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
	public static int gentleDescentSeconds() {
		return gentleDescentSeconds;
	}
	public static int gentleDescentRefreshSeconds() {
		return gentleDescentRefreshSeconds;
	}
	public static boolean gentleDescentCancelOnSneak() {
		return gentleDescentCancelOnSneak;
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
	public static int undyingDropDarknessBaseSeconds() {
		return undyingDropDarknessBaseSeconds;
	}
	public static int undyingDropDarknessStep() {
		return undyingDropDarknessStep;
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
	public static int chineseAirborneDurabilityBase() {
		return chineseAirborneDurabilityBase;
	}
	public static int chineseDamageStep() {
		return chineseDamageStep;
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
}
