package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.practicalenchantments.PracticalEnchantments;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Practical Enchantments 的 22 个附魔定义（1.20.1）。
 *
 * <h2>为什么是"一个注册类 + 22 个私有静态方法"，而不是 22 个类</h2>
 *
 * <p>26.3 的 22 个附魔是数据包 json，1.20.1 必须变成代码注册。选择一个注册类，理由有三条：</p>
 * <ol>
 *   <li><b>定义是纯数据，行为不在这里。</b>22 个附魔的触发逻辑由 26.3 的行为类
 *       （{@code VenomEnchantment} 等，各自带 {@code registerCallbacks}）承担，它们是<b>另一件事</b>，
 *       会单独移植。若把定义也塞进那些类，每个类就会同时承担"注册数值"与"事件回调"两种职责，
 *       而后者在 1.20.1 上还要等注册表就绪，时机与前者不同（定义必须在模组构造期完成）。</li>
 *   <li><b>注册必须一次性、在模组构造期完成。</b>1.20.1 的附魔是静态注册表，注册表事件只开一次。
 *       集中在一个 {@link #registerAll()} 里，最容易保证"全部注册、且只注册一次"，也最容易和
 *       {@code PracticalEnchantments.registerEnchantments()} 的守卫对齐。</li>
 *   <li><b>可审计性。</b>22 个 json 是规格说明书。把 22 段定义并排放在一个文件里，可以和
 *       {@code 26.3/.../enchantment/*.json} 逐条对照，而不用在 22 个文件之间来回跳。</li>
 * </ol>
 *
 * <h2>1.20.1 的强制分歧（逐条）</h2>
 *
 * <ul>
 *   <li><b>supported_items 原样透传。</b>json 里的 {@code #minecraft:enchantable/*} 是 1.21 的标签族，
 *       1.20.1 一个都不存在。翻译统一由 MerlinLib 的
 *       {@code EnchantmentRegistry.MerlinEnchantment.matchesTag} 完成，本类<b>不改写</b>这些字符串——
 *       翻译只该有一个地方。</li>
 *   <li><b>acquisition 来自两个标签文件。</b>26.3 用 {@code data/minecraft/tags/enchantment/} 下的
 *       {@code in_enchanting_table.json} 与 {@code tradeable.json} 表达"能否从附魔台/村民处获得"。
 *       1.20.1 没有 {@code tags/enchantment} 这一层，改由 {@link Acquisition} 的布尔量落到
 *       {@code isDiscoverable()} / {@code isTradeable()} 上。</li>
 *   <li><b>fishing / lootChest 一律 false。</b>PE 的宝箱战利品是<b>代码注入</b>
 *       （{@code MerlinApi.loot()}，见 {@code PracticalEnchantments.registerLootInjections()}），
 *       而不是 {@code minecraft:on_random_loot} 标签；26.3 也没有那两个标签文件。填 true 会记录一条
 *       本模组并未通过该渠道注册的来源，所以留 false，让 {@link Acquisition} 只陈述事实。</li>
 *   <li><b>treasureOnly / curse 一律 false。</b>26.3 没有 {@code treasure.json} 或 {@code curse.json}，
 *       原版默认即"非宝藏、非诅咒"。特别地 {@code destruction}/{@code incinerate} 虽在注释里被称作
 *       "宝藏"，但它们出现在 {@code tradeable.json} 里；而 {@code treasureOnly=true} 会把
 *       {@code isTradeable()} 压成 false，与标签直接矛盾，所以不能按注释推断。</li>
 *   <li><b>category 只在"附魔台必须筛"时设置。</b>1.20.1 的附魔台对<b>非书本</b>物品只调用
 *       {@code EnchantmentCategory.canEnchant(Item)}，<b>不看</b> {@code supported_items}
 *       （那条路径只在附魔书/战利品上走）。所以"能否在附魔台出现"由两件事共同决定：
 *       {@code isDiscoverable()}（来自 {@code in_enchanting_table.json}）与 category。
 *       凡在附魔台标签里的附魔，本类都设了与之匹配的 category；不在标签里的则一律留默认
 *       {@code VANISHABLE}，因为附魔台根本不会考虑它们，category 对它们没有意义。</li>
 * </ul>
 *
 * <h2>ID 常量放在这里</h2>
 *
 * <p>26.3 的每个行为类自带 {@code ID}（{@code String}）。移植到本工程时，那些类<b>还不存在</b>
 * （目标目录当前只有入口、Forge 入口与村民交易）。为免重复定义，本类拥有 22 个
 * {@code public static final ResourceLocation} 常量；行为类移植时应<b>引用</b>它们，而不是各写一份。
 * 注意类型差异：26.3 是 {@code String}，这里是 {@link ResourceLocation}，因为 1.20.1 的注册表按键就是它。</p>
 */
public final class EnchantmentDefinitions {

	/** 可在附魔台获得，也可由村民出售（两个标签文件都列出）。 */
	private static final Acquisition TABLE_AND_TRADE = new Acquisition(true, true, false, false, false, false);
	/** 只出现在 {@code in_enchanting_table.json}。 */
	private static final Acquisition TABLE_ONLY = new Acquisition(true, false, false, false, false, false);
	/** 只出现在 {@code tradeable.json}。 */
	private static final Acquisition TRADE_ONLY = new Acquisition(false, true, false, false, false, false);
	/** 两个标签文件都没有；获取途径完全由本模组的代码登记（战利品注入 / 村民交易）。 */
	private static final Acquisition NEITHER = new Acquisition(false, false, false, false, false, false);

	/**
	 * 「伪装」的 7 个头颅物品，逐字取自 {@code disguise.json} 的 {@code supported_items} 数组。
	 *
	 * <p>写成 {@link JsonArray} 而不是 7 个字符串拼接：{@code supported_items} 在原生格式里允许数组，
	 * 而 builder 的 {@code supportedItems(JsonElement)} 重载正是为这种多元素声明准备的。用
	 * {@link JsonParser} 解析一段字面 json，是为了让这 7 个 id 与 26.3 的 json 文件保持<b>肉眼可比</b>的
	 * 同一形状——如果写成 {@code List<String>} 再转换，日后核对时就要多绕一层。</p>
	 *
	 * <p><b>为什么必须是数组而不是标签。</b>曾经用 {@code #forge:heads} 近似替代过，但那会漏掉
	 * {@code minecraft:piglin_head}：该物品在 1.20.1 存在，只是没被 Forge 的 {@code forge:heads}
	 * 收录。数组原文没有这个缺口。库侧早期"只取数组第一个元素"的缺陷已修（见
	 * {@code EnchantmentJson.references}），所以这里可以放心用数组。</p>
	 */
	private static final JsonArray DISGUISE_ITEMS = JsonParser.parseString(
		"[\"minecraft:skeleton_skull\",\"minecraft:wither_skeleton_skull\",\"minecraft:zombie_head\","
			+ "\"minecraft:creeper_head\",\"minecraft:piglin_head\",\"minecraft:dragon_head\","
			+ "\"minecraft:player_head\"]").getAsJsonArray();

	// ==================== 22 个附魔 id ====================
	// 供行为类、战利品注入与村民交易复用，避免同一字符串在多处硬编码。

	/** 浮空速掘：头饰，取消浮空时的挖掘减速。 */
	public static final ResourceLocation AERIAL_HASTE = id("aerial_haste");
	/** 夺首：锋刃，提高头颅掉落率。 */
	public static final ResourceLocation BEHEADING = id("beheading");
	/** 明朗：头饰，永久夜视。 */
	public static final ResourceLocation BRIGHT = id("bright");
	/** 吸猫体质：腿甲，猫不再避开你。 */
	public static final ResourceLocation CAT_ATTRACTION = id("cat_attraction");
	/** 猫咪护符：头饰，苦力怕与幻翼不主动攻击。 */
	public static final ResourceLocation CAT_CHARM = id("cat_charm");
	/** 中国人：胸甲。 */
	public static final ResourceLocation CHINESE = id("chinese");
	/** 恶魔交易：胸甲，绿宝石免死。 */
	public static final ResourceLocation DEMONIC_PACT = id("demonic_pact");
	/** 毁灭：挖掘工具，摧毁方块并产出经验。 */
	public static final ResourceLocation DESTRUCTION = id("destruction");
	/** 伪装：头颅，对应生物不再主动攻击。 */
	public static final ResourceLocation DISGUISE = id("disguise");
	/** 残杀：锋刃，对残血目标加伤。 */
	public static final ResourceLocation EXECUTE = id("execute");
	/** 狂暴：锋刃，击杀后获得加速。 */
	public static final ResourceLocation FRENZY = id("frenzy");
	/** 羽落：靴子，永久缓降。 */
	public static final ResourceLocation GENTLE_DESCENT = id("gentle_descent");
	/** 焚灭：锋刃，彻底焚毁生物。 */
	public static final ResourceLocation INCINERATE = id("incinerate");
	/** 吸血：锋刃，按目标实际损失生命值回复。 */
	public static final ResourceLocation LIFE_STEAL = id("life_steal");
	/** 伐木工：斧，一次砍倒整棵树。 */
	public static final ResourceLocation LUMBERJACK = id("lumberjack");
	/** 贯穿：三叉戟，投掷加伤。 */
	public static final ResourceLocation PENETRATION = id("penetration");
	/** 强劲：弩，加伤。 */
	public static final ResourceLocation POWERFUL = id("powerful");
	/** 粉碎：三叉戟，投掷破坏方块。 */
	public static final ResourceLocation SMASHING = id("smashing");
	/** 汲灵：锋刃，把伤害转化为经验。 */
	public static final ResourceLocation SOUL_SIPHON = id("soul_siphon");
	/** 掉落不死亡：全套护甲，以掉落背包为代价免死。 */
	public static final ResourceLocation UNDYING_DROP = id("undying_drop");
	/** 淬毒：剑，命中附加中毒。 */
	public static final ResourceLocation VENOM = id("venom");
	/** 枯萎：剑，命中附加凋零。 */
	public static final ResourceLocation WITHER_ASPECT = id("wither_aspect");

	private EnchantmentDefinitions() {
	}

	/**
	 * 注册全部 22 个附魔。
	 *
	 * <p><b>必须在模组构造期调用</b>：1.20.1 的附魔是 {@code DeferredRegister} 条目，注册表在模组
	 * 构造期之后即冻结，之后再也加不进去。这也是 26.3 与本版本最大的行为差异——那边靠运行期生成
	 * 数据包，任何时刻登记都算数。</p>
	 *
	 * <p>幂等性不由本方法负责：重复调用会重复向 MerlinLib 提交，后者会对同一 id 告警并以后者为准。
	 * 真正的守卫在 {@code PracticalEnchantments.registerEnchantments()}。</p>
	 */
	public static void registerAll() {
		aerialHaste();
		beheading();
		bright();
		catAttraction();
		catCharm();
		chinese();
		demonicPact();
		destruction();
		disguise();
		execute();
		frenzy();
		gentleDescent();
		incinerate();
		lifeSteal();
		lumberjack();
		penetration();
		powerful();
		smashing();
		soulSiphon();
		undyingDrop();
		venom();
		witherAspect();
	}

	/**
	 * 浮空速掘 —— {@code aerial_haste.json}。
	 *
	 * <p>supported_items {@code #minecraft:enchantable/head_armor} → 1.20.1 无此标签，由 MerlinLib
	 * 翻译为"头盔槽的 {@code ArmorItem}"。category 设为 {@code ARMOR_HEAD}：它在
	 * {@code in_enchanting_table.json} 里，附魔台真的会提供它，而附魔台对非书本物品只问 category。</p>
	 */
	private static void aerialHaste() {
		MerlinApi.enchantments()
			.register(AERIAL_HASTE)
			.translationKey(key("aerial_haste"))
			.weight(4)
			.maxLevel(1)
			.cost(8, 0, 25, 0)
			.anvilCost(2)
			.slots("head")
			.supportedItems("#minecraft:enchantable/head_armor")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.ARMOR_HEAD)
			.submit();
	}

	/**
	 * 夺首 —— {@code beheading.json}。
	 *
	 * <p>cost 全 0：json 里 min/max 的 base 与 per_level 都是 0，即"不受附魔台等级门槛约束"。
	 * category 留空（默认 {@code VANISHABLE}）：它不在 {@code in_enchanting_table.json} 里，
	 * {@code isDiscoverable()} 为 false，附魔台根本不会考虑它，category 对它没有意义。</p>
	 */
	private static void beheading() {
		MerlinApi.enchantments()
			.register(BEHEADING)
			.translationKey(key("beheading"))
			.weight(1)
			.maxLevel(3)
			.cost(0, 0, 0, 0)
			.anvilCost(4)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * 明朗 —— {@code bright.json}。
	 *
	 * <p>category {@code ARMOR_HEAD}：在附魔台标签里，理由同 {@link #aerialHaste()}。</p>
	 */
	private static void bright() {
		MerlinApi.enchantments()
			.register(BRIGHT)
			.translationKey(key("bright"))
			.weight(10)
			.maxLevel(1)
			.cost(10, 0, 25, 0)
			.anvilCost(2)
			.slots("head")
			.supportedItems("#minecraft:enchantable/head_armor")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.ARMOR_HEAD)
			.submit();
	}

	/**
	 * 吸猫体质 —— {@code cat_attraction.json}。
	 *
	 * <p>不在附魔台标签里（只可被村民出售、以及本模组的战利品注入），category 留空。</p>
	 */
	private static void catAttraction() {
		MerlinApi.enchantments()
			.register(CAT_ATTRACTION)
			.translationKey(key("cat_attraction"))
			.weight(1)
			.maxLevel(1)
			.cost(8, 0, 28, 0)
			.anvilCost(2)
			.slots("legs")
			.supportedItems("#minecraft:enchantable/leg_armor")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * 猫咪护符 —— {@code cat_charm.json}。
	 *
	 * <p>不在附魔台标签里，category 留空。</p>
	 */
	private static void catCharm() {
		MerlinApi.enchantments()
			.register(CAT_CHARM)
			.translationKey(key("cat_charm"))
			.weight(1)
			.maxLevel(1)
			.cost(12, 0, 35, 0)
			.anvilCost(3)
			.slots("head")
			.supportedItems("#minecraft:enchantable/head_armor")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * 中国人 —— {@code chinese.json}。
	 *
	 * <p>两个标签文件都没有它（宝箱战利品由本模组代码注入），category 留空。
	 * cost 与 anvil_cost 全 0，与 json 一致。</p>
	 */
	private static void chinese() {
		MerlinApi.enchantments()
			.register(CHINESE)
			.translationKey(key("chinese"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(0)
			.slots("chest")
			.supportedItems("#minecraft:enchantable/chest_armor")
			.acquisition(NEITHER)
			.submit();
	}

	/**
	 * 恶魔交易 —— {@code demonic_pact.json}。
	 *
	 * <p>与掉落不死亡互斥（{@code exclusive_set/death_save}）。不在任何获取标签里，category 留空。</p>
	 */
	private static void demonicPact() {
		MerlinApi.enchantments()
			.register(DEMONIC_PACT)
			.translationKey(key("demonic_pact"))
			.weight(1)
			.maxLevel(3)
			.cost(0, 0, 0, 0)
			.anvilCost(6)
			.slots("chest")
			.supportedItems("#minecraft:enchantable/chest_armor")
			.exclusiveSet(ExclusiveSets.DEATH_SAVE.deepCopy())
			.acquisition(NEITHER)
			.submit();
	}

	/**
	 * 毁灭 —— {@code destruction.json}。
	 *
	 * <p>互斥引用的是 1.21 原版标签 {@code #minecraft:exclusive_set/mining}，1.20.1 没有这个标签层，
	 * 故用 {@link ExclusiveSets#MINING} 的展开成员（{@code fortune} / {@code silk_touch}）替代。</p>
	 *
	 * <p>它<b>不在</b> {@code in_enchanting_table.json} 里（虽然注释称其为"宝藏"），
	 * 只在 {@code tradeable.json} 里，所以 category 留空。</p>
	 */
	private static void destruction() {
		MerlinApi.enchantments()
			.register(DESTRUCTION)
			.translationKey(key("destruction"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(0)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/mining")
			.exclusiveSet(ExclusiveSets.MINING.deepCopy())
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * 伪装 —— {@code disguise.json}。
	 *
	 * <h2>1.20.1 分歧：7 元素数组需要显式的 {@link JsonArray}</h2>
	 *
	 * <p>json 的 supported_items 是 7 个头颅物品的<b>数组</b>，而 builder 的
	 * {@code supportedItems(String)} 只接受单个引用串，装不下数组。所以这里走
	 * {@link EnchantmentBuilder#supportedItems(JsonElement)} 重载，传入按 json 原文顺序、逐字不差的
	 * 7 个元素。</p>
	 *
	 * <p>值得记下来的是：<b>数组形式曾经是坏的</b>。MerlinLib 早期的
	 * {@code EnchantmentJson.reference(...)} 对数组只取第一个元素，于是其余 6 个头颅会静默失效——
	 * 附魔照常注册、处处可见，却只在一种头颅上生效。该缺陷已在库侧的
	 * {@code EnchantmentJson.references(...)} 修好（返回全部引用，{@code MerlinEnchantment} 取并集），
	 * 因此这里可以放心地用数组原文，而不必退化成 {@code #forge:heads} 之类的近似标签。</p>
	 *
	 * <p>曾经考虑过的替代方案，记录在此以免日后有人再走一遍：{@code #forge:heads} 覆盖其中 6 项，
	 * 但<b>漏掉 {@code minecraft:piglin_head}</b>（该物品在 1.20.1 存在，只是没被 Forge 标签收录）。
	 * 数组原文没有这个缺口。</p>
	 */
	private static void disguise() {
		MerlinApi.enchantments()
			.register(DISGUISE)
			.translationKey(key("disguise"))
			.weight(1)
			.maxLevel(1)
			.cost(12, 0, 35, 0)
			.anvilCost(3)
			.slots("head")
			.supportedItems(DISGUISE_ITEMS)
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * 残杀 —— {@code execute.json}。
	 *
	 * <p>不在任何获取标签里，category 留空。</p>
	 */
	private static void execute() {
		MerlinApi.enchantments()
			.register(EXECUTE)
			.translationKey(key("execute"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(5)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(NEITHER)
			.submit();
	}

	/**
	 * 狂暴 —— {@code frenzy.json}。
	 *
	 * <p>不在附魔台标签里（只可村民出售 + 代码注入），category 留空。</p>
	 */
	private static void frenzy() {
		MerlinApi.enchantments()
			.register(FRENZY)
			.translationKey(key("frenzy"))
			.weight(1)
			.maxLevel(2)
			.cost(0, 0, 0, 0)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * 羽落 —— {@code gentle_descent.json}。
	 *
	 * <p>与原版羽毛掉落（{@code minecraft:feather_falling}）互斥。不在任何获取标签里，category 留空。</p>
	 */
	private static void gentleDescent() {
		MerlinApi.enchantments()
			.register(GENTLE_DESCENT)
			.translationKey(key("gentle_descent"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(2)
			.slots("feet")
			.supportedItems("#minecraft:enchantable/foot_armor")
			.exclusiveSet(ExclusiveSets.SLOW_FALL.deepCopy())
			.acquisition(NEITHER)
			.submit();
	}

	/**
	 * 焚灭 —— {@code incinerate.json}。
	 *
	 * <p>与原版抢夺（{@code minecraft:looting}）互斥。不在附魔台标签里，category 留空。</p>
	 */
	private static void incinerate() {
		MerlinApi.enchantments()
			.register(INCINERATE)
			.translationKey(key("incinerate"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(0)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.exclusiveSet(ExclusiveSets.LOOTING.deepCopy())
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * 吸血 —— {@code life_steal.json}。
	 *
	 * <p>category {@code WEAPON}：它在附魔台标签里，且 supported_items 是
	 * {@code enchantable/sharp_weapon} → 1.20.1 的 {@code #minecraft:swords}。
	 * 1.20.1 的 {@code WEAPON} 判定正是 {@code item instanceof SwordItem}，与该标签集合一致。</p>
	 */
	private static void lifeSteal() {
		MerlinApi.enchantments()
			.register(LIFE_STEAL)
			.translationKey(key("life_steal"))
			.weight(8)
			.maxLevel(5)
			.cost(4, 8, 24, 8)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.WEAPON)
			.submit();
	}

	/**
	 * 伐木工 —— {@code lumberjack.json}。
	 *
	 * <p>互斥引用 1.21 的 {@code #minecraft:exclusive_set/mining}，用 {@link ExclusiveSets#MINING} 代替。</p>
	 *
	 * <p>category {@code DIGGER}：它在附魔台标签里，而附魔台对非书本物品只问 category。
	 * {@code DIGGER} 的判定是 {@code item instanceof DiggerItem}，即斧/镐/锹/锄——比 json 的
	 * {@code #minecraft:axes} 宽。1.20.1 没有"斧"这一 category，这是本版本能做到的最接近值；
	 * 若不设，VANISHABLE 会把几乎所有可损毁物品都放进来，比 DIGGER 更糟。</p>
	 */
	private static void lumberjack() {
		MerlinApi.enchantments()
			.register(LUMBERJACK)
			.translationKey(key("lumberjack"))
			.weight(4)
			.maxLevel(1)
			.cost(8, 0, 25, 0)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("#minecraft:axes")
			.exclusiveSet(ExclusiveSets.MINING.deepCopy())
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.DIGGER)
			.submit();
	}

	/**
	 * 贯穿 —— {@code penetration.json}。
	 *
	 * <p>与原版激流（{@code minecraft:riptide}）互斥。category {@code TRIDENT}：在附魔台标签里，
	 * 且 {@code TRIDENT} 的判定是 {@code item instanceof TridentItem}，与 supported_items 一致。</p>
	 */
	private static void penetration() {
		MerlinApi.enchantments()
			.register(PENETRATION)
			.translationKey(key("penetration"))
			.weight(6)
			.maxLevel(5)
			.cost(6, 8, 28, 8)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/trident")
			.exclusiveSet(ExclusiveSets.TRIDENT_RIPTIDE.deepCopy())
			.acquisition(TABLE_ONLY)
			.category(EnchantmentCategory.TRIDENT)
			.submit();
	}

	/**
	 * 强劲 —— {@code powerful.json}。
	 *
	 * <p>supported_items 是单个物品 id {@code minecraft:crossbow}（不是标签）。
	 * category {@code CROSSBOW}：在附魔台标签里，且 {@code CROSSBOW} 的判定正是
	 * {@code item instanceof CrossbowItem}。</p>
	 */
	private static void powerful() {
		MerlinApi.enchantments()
			.register(POWERFUL)
			.translationKey(key("powerful"))
			.weight(10)
			.maxLevel(5)
			.cost(1, 10, 15, 10)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("minecraft:crossbow")
			.exclusiveSet(ExclusiveSets.CROSSBOW.deepCopy())
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.CROSSBOW)
			.submit();
	}

	/**
	 * 粉碎 —— {@code smashing.json}。
	 *
	 * <p>无互斥组。category {@code TRIDENT}：在附魔台标签里，理由同 {@link #penetration()}。</p>
	 */
	private static void smashing() {
		MerlinApi.enchantments()
			.register(SMASHING)
			.translationKey(key("smashing"))
			.weight(6)
			.maxLevel(5)
			.cost(5, 8, 28, 8)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/trident")
			.acquisition(TABLE_ONLY)
			.category(EnchantmentCategory.TRIDENT)
			.submit();
	}

	/**
	 * 汲灵 —— {@code soul_siphon.json}。
	 *
	 * <p>category {@code WEAPON}：在附魔台标签里，理由同 {@link #lifeSteal()}。</p>
	 */
	private static void soulSiphon() {
		MerlinApi.enchantments()
			.register(SOUL_SIPHON)
			.translationKey(key("soul_siphon"))
			.weight(7)
			.maxLevel(5)
			.cost(5, 7, 25, 7)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.WEAPON)
			.submit();
	}

	/**
	 * 掉落不死亡 —— {@code undying_drop.json}。
	 *
	 * <p>与恶魔交易互斥（{@code exclusive_set/death_save}）。slots 是 4 个槽位的数组，
	 * MerlinLib 会逐一解析成 {@code HEAD/CHEST/LEGS/FEET}。不在任何获取标签里，category 留空。</p>
	 */
	private static void undyingDrop() {
		MerlinApi.enchantments()
			.register(UNDYING_DROP)
			.translationKey(key("undying_drop"))
			.weight(1)
			.maxLevel(3)
			.cost(0, 0, 0, 0)
			.anvilCost(3)
			.slots("head", "chest", "legs", "feet")
			.supportedItems("#minecraft:enchantable/armor")
			.exclusiveSet(ExclusiveSets.DEATH_SAVE.deepCopy())
			.acquisition(NEITHER)
			.submit();
	}

	/**
	 * 淬毒 —— {@code venom.json}。
	 *
	 * <p>与枯萎、原版火焰附加三向互斥（{@code exclusive_set/blade_effect}）。
	 * category {@code WEAPON}：在附魔台标签里，理由同 {@link #lifeSteal()}。</p>
	 */
	private static void venom() {
		MerlinApi.enchantments()
			.register(VENOM)
			.translationKey(key("venom"))
			.weight(6)
			.maxLevel(2)
			.cost(8, 8, 28, 8)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:swords")
			.exclusiveSet(ExclusiveSets.BLADE_EFFECT.deepCopy())
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.WEAPON)
			.submit();
	}

	/**
	 * 枯萎 —— {@code wither_aspect.json}。
	 *
	 * <p>与淬毒、原版火焰附加三向互斥（{@code exclusive_set/blade_effect}）。
	 * 不在附魔台标签里（只可村民出售 + 代码注入），category 留空。</p>
	 */
	private static void witherAspect() {
		MerlinApi.enchantments()
			.register(WITHER_ASPECT)
			.translationKey(key("wither_aspect"))
			.weight(1)
			.maxLevel(2)
			.cost(0, 0, 0, 0)
			.anvilCost(4)
			.slots("mainhand")
			.supportedItems("#minecraft:swords")
			.exclusiveSet(ExclusiveSets.BLADE_EFFECT.deepCopy())
			.acquisition(TRADE_ONLY)
			.submit();
	}

	/**
	 * {@code enchantment.<命名空间>.<路径>}，即 json 里 {@code description.translate} 的值。
	 *
	 * @param path 附魔路径
	 * @return 语言键
	 */
	private static String key(String path) {
		return "enchantment." + PracticalEnchantments.MOD_ID + "." + path;
	}

	/**
	 * 构造本模组命名空间下的附魔 id。
	 *
	 * @param path 附魔路径
	 * @return 全名
	 */
	private static ResourceLocation id(String path) {
		return new ResourceLocation(PracticalEnchantments.MOD_ID, path);
	}
}
