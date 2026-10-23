package com.practicalenchantments.config;

import com.practicalenchantments.PracticalEnchantments;

/**
 * 原版附魔调整的开关。
 */
public final class VanillaTweaks {

	/** 界面页签名，与配置文件里的节名一致。 */
	public static final String SECTION_KEY = "vanilla_tweaks";

	/** 页签在语言文件里的键。 */
	public static final String SECTION_LANG_KEY =
			PracticalEnchantments.MOD_ID + ".configuration." + SECTION_KEY;

	/** 配置界面的键前缀，{@code <section>} 固定为 {@link #SECTION_KEY}。 */
	public static final String LANG_PREFIX = SECTION_LANG_KEY + ".";

	/** 无限可以附在弩上。 */
	public static final String KEY_INFINITY_CROSSBOW = "infinity_crossbow";
	/** 无限不需要背包里有普通箭。 */
	public static final String KEY_INFINITY_NO_ARROW = "infinity_no_arrow";
	/** 无限同样作用于光灵箭。 */
	public static final String KEY_INFINITY_SPECTRAL = "infinity_spectral";

	public static final boolean DEFAULT_INFINITY_CROSSBOW = false;
	public static final boolean DEFAULT_INFINITY_NO_ARROW = true;
	public static final boolean DEFAULT_INFINITY_SPECTRAL = true;

	private static volatile boolean infinityCrossbow = DEFAULT_INFINITY_CROSSBOW;
	private static volatile boolean infinityNoArrow = DEFAULT_INFINITY_NO_ARROW;
	private static volatile boolean infinitySpectral = DEFAULT_INFINITY_SPECTRAL;

	private VanillaTweaks() {
	}

	/**
	 * 覆盖三项开关。配置加载与重载时各端调用一次。
	 *
	 * @param crossbow 无限是否可以附在弩上
	 * @param noArrow  没有普通箭时是否仍然可以射击
	 * @param spectral 无限是否同样作用于光灵箭
	 */
	public static void apply(boolean crossbow, boolean noArrow, boolean spectral) {
		infinityCrossbow = crossbow;
		infinityNoArrow = noArrow;
		infinitySpectral = spectral;
	}

	/** @return 无限是否可以附在弩上 */
	public static boolean infinityCrossbow() {
		return infinityCrossbow;
	}

	/** @return 没有普通箭时是否仍然可以射击 */
	public static boolean infinityNoArrow() {
		return infinityNoArrow;
	}

	/** @return 无限是否同样作用于光灵箭 */
	public static boolean infinitySpectral() {
		return infinitySpectral;
	}

	/**
	 * @param key 选项键，例如 {@link #KEY_INFINITY_CROSSBOW}
	 * @return 该选项在配置界面上的名称键
	 */
	public static String nameKey(String key) {
		return LANG_PREFIX + key;
	}

	/**
	 * @param key 选项键，例如 {@link #KEY_INFINITY_CROSSBOW}
	 * @return 该选项在配置界面上的提示键
	 */
	public static String tooltipKey(String key) {
		return LANG_PREFIX + key + ".tooltip";
	}

	/**
	 * @param key 选项键
	 * @return 该选项此刻的值
	 */
	public static boolean value(String key) {
		return switch (key) {
			case KEY_INFINITY_CROSSBOW -> infinityCrossbow;
			case KEY_INFINITY_NO_ARROW -> infinityNoArrow;
			case KEY_INFINITY_SPECTRAL -> infinitySpectral;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}

	/**
	 * @param key 选项键
	 * @return 该选项的默认值，界面上的「重置」用它
	 */
	public static boolean defaultValue(String key) {
		return switch (key) {
			case KEY_INFINITY_CROSSBOW -> DEFAULT_INFINITY_CROSSBOW;
			case KEY_INFINITY_NO_ARROW -> DEFAULT_INFINITY_NO_ARROW;
			case KEY_INFINITY_SPECTRAL -> DEFAULT_INFINITY_SPECTRAL;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}
}
