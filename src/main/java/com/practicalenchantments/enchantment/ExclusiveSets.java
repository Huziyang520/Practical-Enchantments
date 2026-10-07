package com.practicalenchantments.enchantment;

import com.google.gson.JsonArray;

/**
 * 6 组互斥标签 + 1 组原版挖矿互斥标签，以代码形式提供。
 *
 * <h2>为什么要有这个类</h2>
 *
 * <p>26.3 的互斥组是数据包标签文件（{@code data/practical_enchantments/tags/enchantment/exclusive_set/*.json}
 * 与 {@code data/minecraft/tags/enchantment/exclusive_set/mining.json}）。1.20.1 <b>完全没有
 * {@code tags/enchantment} 这一层</b>：原版附魔标签（{@code in_enchanting_table}、{@code tradeable}、
 * {@code treasure}、{@code exclusive_set/*}）是 1.21（24w18a）才加入的，1.20.1 的附魔互斥全部硬编码在
 * 各自的 {@code checkCompatibility} 里。所以标签文件必须变成代码。</p>
 *
 * <h2>为什么给的是"展开后的成员表"而不是标签名</h2>
 *
 * <p>MerlinLib 的 {@code exclusive_set} 字段读的是<b>附魔 id 列表</b>：{@code EnchantmentRegistry}
 * 把每个元素按 {@code ResourceLocation} 解析，然后在 {@code checkCompatibility} 里和对方附魔的
 * 注册名比对。如果这里把 {@code "#practical_enchantments:exclusive_set/blade_effect"} 原样交给它，
 * 它会把这个<b>标签名</b>当成一个附魔 id——永远匹配不到任何附魔，互斥就静默失效了。标签名到成员
 * 的展开必须在这里完成。</p>
 *
 * <p>数组内容与 26.3 的标签 json 逐字一致（含原版成员），以便与源文件直接对照。由于
 * {@code JsonArray} 是可变的，而每个元素都会被 MerlinLib 保存引用，调用处一律用
 * {@link JsonArray#deepCopy()} 传副本，避免任意一个附魔改动数组牵连其余附魔。</p>
 *
 * <h2>1.20.1 的强制分歧</h2>
 *
 * <p>{@link #MINING} 不是 PE 的标签，而是两个 26.3 附魔（{@code destruction}、{@code lumberjack}）
 * 在 json 里引用的 <b>1.21 原版标签</b> {@code #minecraft:exclusive_set/mining}。该标签在 1.20.1
 * 不存在，MerlinLib 的 {@code matchesTag} 也不翻译互斥标签（它只翻译 {@code supported_items}）。
 * 这里按标签文件 {@code data/minecraft/tags/enchantment/exclusive_set/mining.json} 的实际成员
 * （{@code fortune} 与 {@code silk_touch}）展开——这也正是这一组互斥在 1.21 数据包里的语义。</p>
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
	 * 数据来源，必须由本数组补上，否则 26.3 里"毁灭/伐木工不能与时运/精准采集共存"的规则会丢失。</p>
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
