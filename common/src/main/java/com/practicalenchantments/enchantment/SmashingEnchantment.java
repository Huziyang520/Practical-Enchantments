package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.practicalenchantments.PracticalEnchantments;

/**
 * 粉碎附魔 - 三叉戟 Ⅴ 级（附魔台可出，不可交易）。
 *
 * <p>投掷三叉戟击中方块时瞬间破坏该方块，等级对应镐的挖掘等级，低于对应等级的
 * 可采集方块不掉落（与错用工具一致）：</p>
 * <pre>
 * Ⅰ 木/金镐（石头、煤矿等）  Ⅱ 石镐（铁矿、青金石等）
 * Ⅲ 铁镐（金矿、钻石矿、红石等）  Ⅳ 钻石镐（黑曜石、远古残骸）
 * Ⅴ 下界合金镐（全部可采集方块）
 * </pre>
 *
 * <p>破坏逻辑（含与「贯穿+忠诚」联动的掉落物牵引）由 {@code TridentSmashMixin} /
 * {@code TridentPullMixin} 实现；本类只负责注册。</p>
 */
public final class SmashingEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":smashing";

	private SmashingEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("粉碎")
			.supportedItems("#minecraft:enchantable/trident")
			.weight(6)
			.maxLevel(5)
			.minCost(5, 8)
			.maxCost(28, 8)
			.anvilCost(3)
			.slots("mainhand"));
	}
}
