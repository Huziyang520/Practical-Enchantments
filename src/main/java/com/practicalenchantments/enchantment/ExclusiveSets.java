// 6 组互斥标签 + 1 组原版挖矿互斥标签，以代码形式提供。

package com.practicalenchantments.enchantment;

import com.google.gson.JsonArray;

public final class ExclusiveSets {
	public static final JsonArray BLADE_EFFECT = array(
		"practical_enchantments:venom",
		"practical_enchantments:wither_aspect",
		"minecraft:fire_aspect");

	public static final JsonArray CROSSBOW = array(
		"practical_enchantments:powerful",
		"minecraft:multishot");

	public static final JsonArray DEATH_SAVE = array(
		"practical_enchantments:demonic_pact",
		"practical_enchantments:undying_drop");

	public static final JsonArray LOOTING = array(
		"practical_enchantments:incinerate",
		"minecraft:looting");

	public static final JsonArray SLOW_FALL = array(
		"practical_enchantments:gentle_descent",
		"minecraft:feather_falling");

	public static final JsonArray TRIDENT_RIPTIDE = array(
		"practical_enchantments:penetration",
		"minecraft:riptide");

	public static final JsonArray MINING = array(
		"minecraft:fortune",
		"minecraft:silk_touch");

	public static final JsonArray BOOTS = array(
		"minecraft:depth_strider",
		"minecraft:frost_walker");

	private ExclusiveSets() {
	}

	private static JsonArray array(String... values) {
		JsonArray array = new JsonArray();
		for (String value : values) {
			array.add(value);
		}
		return array;
	}
}
