// 原版附魔调整的开关。

package com.practicalenchantments.config;

import com.practicalenchantments.PracticalEnchantments;

public final class VanillaTweaks {
	public static final String SECTION_KEY = "vanilla_tweaks";

	public static final String SECTION_LANG_KEY =
			PracticalEnchantments.MOD_ID + ".configuration." + SECTION_KEY;

	public static final String LANG_PREFIX = SECTION_LANG_KEY + ".";
	public static final String KEY_INFINITY_CROSSBOW = "infinity_crossbow";
	public static final String KEY_INFINITY_NO_ARROW = "infinity_no_arrow";
	public static final String KEY_INFINITY_SPECTRAL = "infinity_spectral";
	public static final boolean DEFAULT_INFINITY_CROSSBOW = false;
	public static final boolean DEFAULT_INFINITY_NO_ARROW = true;
	public static final boolean DEFAULT_INFINITY_SPECTRAL = true;
	private static volatile boolean infinityCrossbow = DEFAULT_INFINITY_CROSSBOW;
	private static volatile boolean infinityNoArrow = DEFAULT_INFINITY_NO_ARROW;
	private static volatile boolean infinitySpectral = DEFAULT_INFINITY_SPECTRAL;

	private VanillaTweaks() {
	}

	public static void apply(boolean crossbow, boolean noArrow, boolean spectral) {
		infinityCrossbow = crossbow;
		infinityNoArrow = noArrow;
		infinitySpectral = spectral;
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

	public static String nameKey(String key) {
		return LANG_PREFIX + key;
	}

	public static String tooltipKey(String key) {
		return LANG_PREFIX + key + ".tooltip";
	}

	public static boolean value(String key) {
		return switch (key) {
			case KEY_INFINITY_CROSSBOW -> infinityCrossbow;
			case KEY_INFINITY_NO_ARROW -> infinityNoArrow;
			case KEY_INFINITY_SPECTRAL -> infinitySpectral;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}

	public static boolean defaultValue(String key) {
		return switch (key) {
			case KEY_INFINITY_CROSSBOW -> DEFAULT_INFINITY_CROSSBOW;
			case KEY_INFINITY_NO_ARROW -> DEFAULT_INFINITY_NO_ARROW;
			case KEY_INFINITY_SPECTRAL -> DEFAULT_INFINITY_SPECTRAL;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}
}
