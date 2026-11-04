// 原版附魔调整的开关。

package com.practicalenchantments.config;

import com.practicalenchantments.PracticalEnchantments;

public final class VanillaTweaks {
	public static final String SECTION_KEY = "vanilla_tweaks";
	public static final String SECTION_PREFIX = PracticalEnchantments.MOD_ID + ".configuration";

	public static final String SECTION_LANG_KEY =
			PracticalEnchantments.MOD_ID + ".configuration." + SECTION_KEY;

	public static final String LANG_PREFIX = SECTION_LANG_KEY + ".";
	public static final String KEY_INFINITY_CROSSBOW = "infinity_crossbow";
	public static final String KEY_INFINITY_NO_ARROW = "infinity_no_arrow";
	public static final String KEY_INFINITY_SPECTRAL = "infinity_spectral";
	public static final String KEY_QUICK_CHARGE_BOW = "quick_charge_bow";
	public static final String KEY_POWER_CROSSBOW = "power_crossbow";
	public static final String KEY_THORNS_SHIELD = "thorns_shield";
	public static final boolean DEFAULT_INFINITY_CROSSBOW = false;
	public static final boolean DEFAULT_INFINITY_NO_ARROW = true;
	public static final boolean DEFAULT_INFINITY_SPECTRAL = true;
	public static final boolean DEFAULT_QUICK_CHARGE_BOW = false;
	public static final boolean DEFAULT_POWER_CROSSBOW = true;
	public static final boolean DEFAULT_THORNS_SHIELD = true;
	private static volatile boolean infinityCrossbow = DEFAULT_INFINITY_CROSSBOW;
	private static volatile boolean infinityNoArrow = DEFAULT_INFINITY_NO_ARROW;
	private static volatile boolean infinitySpectral = DEFAULT_INFINITY_SPECTRAL;
	private static volatile boolean quickChargeBow = DEFAULT_QUICK_CHARGE_BOW;
	private static volatile boolean powerCrossbow = DEFAULT_POWER_CROSSBOW;
	private static volatile boolean thornsShield = DEFAULT_THORNS_SHIELD;

	private VanillaTweaks() {
	}

	public static void apply(boolean crossbow, boolean noArrow, boolean spectral) {
		infinityCrossbow = crossbow;
		infinityNoArrow = noArrow;
		infinitySpectral = spectral;
	}

	public static void applyExtra(boolean quickCharge, boolean power, boolean thorns) {
		quickChargeBow = quickCharge;
		powerCrossbow = power;
		thornsShield = thorns;
	}

	public static void apply(String key, boolean value) {
		switch (key) {
			case KEY_INFINITY_CROSSBOW -> infinityCrossbow = value;
			case KEY_INFINITY_NO_ARROW -> infinityNoArrow = value;
			case KEY_INFINITY_SPECTRAL -> infinitySpectral = value;
			case KEY_QUICK_CHARGE_BOW -> quickChargeBow = value;
			case KEY_POWER_CROSSBOW -> powerCrossbow = value;
			case KEY_THORNS_SHIELD -> thornsShield = value;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
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

	public static String nameKey(String key) {
		return LANG_PREFIX + key;
	}

	public static String tooltipKey(String key) {
		return LANG_PREFIX + key + ".tooltip";
	}

	public static String descriptionKey(String key) {
		return LANG_PREFIX + key + ".description";
	}

	public static boolean value(String key) {
		return switch (key) {
			case KEY_INFINITY_CROSSBOW -> infinityCrossbow;
			case KEY_INFINITY_NO_ARROW -> infinityNoArrow;
			case KEY_INFINITY_SPECTRAL -> infinitySpectral;
			case KEY_QUICK_CHARGE_BOW -> quickChargeBow;
			case KEY_POWER_CROSSBOW -> powerCrossbow;
			case KEY_THORNS_SHIELD -> thornsShield;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}

	public static boolean defaultValue(String key) {
		return switch (key) {
			case KEY_INFINITY_CROSSBOW -> DEFAULT_INFINITY_CROSSBOW;
			case KEY_INFINITY_NO_ARROW -> DEFAULT_INFINITY_NO_ARROW;
			case KEY_INFINITY_SPECTRAL -> DEFAULT_INFINITY_SPECTRAL;
			case KEY_QUICK_CHARGE_BOW -> DEFAULT_QUICK_CHARGE_BOW;
			case KEY_POWER_CROSSBOW -> DEFAULT_POWER_CROSSBOW;
			case KEY_THORNS_SHIELD -> DEFAULT_THORNS_SHIELD;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}
}
