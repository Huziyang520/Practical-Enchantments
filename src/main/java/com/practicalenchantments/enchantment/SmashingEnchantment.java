package com.practicalenchantments.enchantment;

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
 *
 * <h2>1.20.1 移植说明</h2>
 * <p>本类<b>逐字直接移植</b>，没有任何需要改写的 API：26.3 版本里它本身就只有 id 常量与私有构造器，
 * 既没有 {@code registerCallbacks}，也没有引用任何 Minecraft 类型，因此 {@code Holder} /
 * {@code HolderLookup.Provider} 这一整套签名变化与它无关。</p>
 *
 * <p>需要留意的是：{@code PracticalEnchantments.registerEventCallbacks()} 不会调用本类
 * （26.3 亦然），所以「本类没有 {@code registerCallbacks}」是刻意保持的形状，不是漏写。
 * 移植后行为完全落在两个 Mixin 上，它们读的是
 * {@code DeathSaveSupport.getLevel(weapon, "smashing")}。</p>
 */
public final class SmashingEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":smashing";

	private SmashingEnchantment() {
	}

}