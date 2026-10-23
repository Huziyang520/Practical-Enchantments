package com.practicalenchantments.enchantment;

import com.google.gson.JsonArray;

/**
 * 6 组互斥标签 + 1 组原版挖矿互斥标签，以代码形式提供。
 */
public final class ExclusiveSets {

	/** {@code data/practical_enchantments/tags/enchantment/exclusive_set/blade_effect.json} */
	public static final JsonArray BLADE_EFFECT = array(
		"practical_enchantments:venom",
		"practical_enchantments:wither_aspect",
		"minecraft:fire_aspect");

	/** {@code .../exclusive_set/crossbow.json} */
	public static final JsonArray CROSSBOW = array(
		"practical_enchantments:powerful",
		"minecraft:multishot");

	/** {@code .../exclusive_set/death_save.json} */
	public static final JsonArray DEATH_SAVE = array(
		"practical_enchantments:demonic_pact",
		"practical_enchantments:undying_drop");

	/** {@code .../exclusive_set/looting.json} */
	public static final JsonArray LOOTING = array(
		"practical_enchantments:incinerate",
		"practical_enchantments:aether_godwood",
		"minecraft:looting");

	/** {@code .../exclusive_set/slow_fall.json} */
	public static final JsonArray SLOW_FALL = array(
		"practical_enchantments:gentle_descent",
		"minecraft:feather_falling");

	/** {@code .../exclusive_set/trident_riptide.json} */
	public static final JsonArray TRIDENT_RIPTIDE = array(
		"practical_enchantments:penetration",
		"minecraft:riptide");

	/**
	 * 1.21 原版标签 {@code #minecraft:exclusive_set/mining} 的展开，1.20.1 无此标签。
	 *
	 * <p>被 {@code destruction} 与 {@code lumberjack} 引用。原版在 1.20.1 只硬编码了
	 * {@code fortune} ↔ {@code silk_touch} 的互斥，PE 的这两个附魔与它们的互斥在 1.20.1 上没有任何
	 * 数据来源，必须由本数组补上，否则 上游版本里"毁灭/伐木工不能与时运/精准采集共存"的规则会丢失。</p>
	 */
	public static final JsonArray MINING = array(
		"minecraft:fortune",
		"minecraft:silk_touch");

	private ExclusiveSets() {
	}

	/**
	 * 按原版标签 json 的 {@code values} 数组拼一个 {@link JsonArray}。
	 *
	 * @param values 成员 id，按原文件顺序
	 * @return 新的数组，调用方拥有
	 */
	private static JsonArray array(String... values) {
		JsonArray array = new JsonArray();
		for (String value : values) {
			array.add(value);
		}
		return array;
	}
}
