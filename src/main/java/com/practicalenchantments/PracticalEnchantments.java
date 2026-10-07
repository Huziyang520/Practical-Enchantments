package com.practicalenchantments;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.LootTables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.config.PracticalConfig;
import com.practicalenchantments.enchantment.*;
import com.practicalenchantments.notice.MerlinLibNotice;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Practical Enchantments 模组入口（前置：MerlinLib）。
 *
 * <p>本模组的三件事挂在 MerlinLib 的服务器生命周期上：</p>
 * <ul>
 *   <li><b>战利品注入</b>：必须赶在数据包读战利品表<b>之前</b>登记，所以挂在"服务器开始启动"；</li>
 *   <li><b>事件回调</b>：要解析附魔对象，所以挂在"服务器启动完成"；</li>
 *   <li><b>配置与进服预告</b>：与事件回调同一时机。</li>
 * </ul>
 *
 * <h2>1.20.1 与 26.3 的关键差异</h2>
 *
 * <p><b>附魔定义不再是数据包，而是代码。</b>26.3 的 22 个附魔住在
 * {@code data/practical_enchantments/enchantment/*.json}，由动态附魔注册表读取；1.20.1 没有这个
 * 注册表，附魔是静态代码注册表。因此 22 个 json 的内容全部落进
 * {@link com.practicalenchantments.enchantment.EnchantmentDefinitions}（见该类），
 * 6 组互斥标签落进 {@link com.practicalenchantments.enchantment.ExclusiveSets}。</p>
 *
 * <p>连带结果有三个，都是必须如实记下的边界：</p>
 * <ol>
 *   <li><b>附魔的注册时机从"任意时刻"变成"模组构造期"。</b>26.3 靠运行期生成数据包，任何时刻登记
 *       都能被读到；1.20.1 的 {@code DeferredRegister} 只在注册表事件期间可写。所以附魔定义在
 *       {@link #registerEnchantments()} 里立刻注册，而不是等到服务器启动。</li>
 *   <li><b>战利品注入仍然可以等到服务器启动</b>，因为 {@code LootTableLoadEvent} 是运行期事件，
 *       这正是下面把它挂在 {@code onServerStarting} 的原因——它与附魔注册的时机要求不同，
 *       不能合并成一个。</li>
 *   <li><b>事件回调只能等到附魔真的进了注册表之后</b>才能解析，所以仍然挂在 {@code onServerStarted}。</li>
 * </ol>
 *
 * <p>业务逻辑一份，位于本工程，不含任何加载器引用。</p>
 */
public final class PracticalEnchantments {

	public static final String MOD_ID = "practical_enchantments";

	/** 战利品注入 quality 档位：常见 */
	private static final int QUALITY_COMMON = 0;
	/** 战利品注入 quality 档位：稀有 */
	private static final int QUALITY_RARE = 1;
	/** 战利品注入 quality 档位：宝藏 */
	private static final int QUALITY_TREASURE = 2;

	/** 战利品规则是否已登记；换世界会再次启动服务器，不能重复登记。 */
	private static boolean lootRegistered;
	/** 事件回调是否已登记；同上。 */
	private static boolean callbacksRegistered;
	/** 附魔是否已注册；只有一个模组构造期，这里只是防御性守卫。 */
	private static boolean enchantmentsRegistered;

	private PracticalEnchantments() {
	}

	/**
	 * 由 Forge 入口调用一次，完成本模组的全部装配。
	 *
	 * <p>幂等：重复调用只会多挂几个只会执行一次的 lambda，真正的登记由下面的布尔量守住。</p>
	 */
	public static void bootstrap() {
		// 附魔注册必须最先做，且必须在模组构造期完成：注册表事件只在构造期之后、且只开一次。
		registerEnchantments();

		// 进服提示先登记一次"默认开启"：MerlinLib 的提示设置界面在客户端就能列出本模组，
		// 不必等到进过世界（真默认值在服务器启动、读完配置后再覆盖一次注册）。
		MerlinLibNotice.registerEarly();

		// 战利品：数据包读表之前就位（ServerStarting 早于数据包加载）。
		MerlinApi.lifecycle().onServerStarting(server -> registerLootInjections());

		// 事件回调与配置：附魔已进注册表之后。1.20.1 没有动态附魔注册表，所以这里不再需要等数据包——
		// 保留 onServerStarted 是因为配置与提示的默认值本来就该在服务器起来之后再定。
		MerlinApi.lifecycle().onServerStarted(server -> {
			PracticalConfig.load();
			MerlinLibNotice.register();
			registerEventCallbacks();
			// 第二批（2.5）三个附魔的生物行为：只向 MerlinLib 登记规则，AI 由库执行
			AiRules.register();
		});
	}

	/**
	 * 注册 22 个附魔定义。
	 *
	 * <p>这是 1.20.1 与 26.3 差别最大的一处：26.3 什么都不注册，附魔是数据包条目；这里每一个附魔
	 * 都要在自己的注册表事件里变成代码对象。定义的具体数值全部集中在
	 * {@link EnchantmentDefinitions}，本方法只负责触发它。</p>
	 */
	private static void registerEnchantments() {
		if (enchantmentsRegistered) {
			return;
		}
		enchantmentsRegistered = true;
		EnchantmentDefinitions.registerAll();
	}

	/**
	 * 注册战利品注入（按《附魔名单（第一批）》的箱子与百分比逐条还原）。
	 *
	 * <p>同一附魔在不同箱子上的百分比不同，无法合并成一条规则，故一张表一条；
	 * 全部以附魔书形式注入。</p>
	 */
	public static void registerLootInjections() {
		if (lootRegistered) {
			return;
		}

		registerLoot(QUALITY_TREASURE, 0.12F, "minecraft:chests/woodland_mansion", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/bastion_treasure", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.10F, "minecraft:chests/stronghold_library", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/end_city_treasure", "practical_enchantments:disguise");
		registerLoot(QUALITY_TREASURE, 0.08F, "minecraft:chests/pillager_outpost", "practical_enchantments:disguise");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/jungle_temple", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.10F, "minecraft:chests/shipwreck_treasure", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.08F, "minecraft:chests/desert_pyramid", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_RARE, 0.06F, "minecraft:chests/simple_dungeon", "practical_enchantments:cat_charm");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/igloo_chest", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.10F, "minecraft:chests/shipwreck_supply", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/simple_dungeon", "practical_enchantments:cat_attraction");
		registerLoot(QUALITY_COMMON, 0.06F, "minecraft:chests/abandoned_mineshaft", "practical_enchantments:cat_attraction");
		lootRegistered = true;
		// 中国人 —— 宝藏，仅远古城市 10%
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.ANCIENT_CITY, ChineseEnchantment.ID);

		// 伐木工 —— 稀有：村庄铁匠铺 8% / 废弃矿井 6% / 掠夺者前哨站 5% / 地牢 4%
		registerLoot(QUALITY_RARE, 0.08F, LootTables.VILLAGE_TOOLSMITH, LumberjackEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.06F, LootTables.ABANDONED_MINESHAFT, LumberjackEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.05F, LootTables.PILLAGER_OUTPOST, LumberjackEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.04F, LootTables.SIMPLE_DUNGEON, LumberjackEnchantment.ID);

		// 强劲 —— 常见：掠夺者前哨站 12% / 村庄制箭师 8% / 地牢 6% / 堡垒遗迹 5%
		registerLoot(QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST, PowerfulEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.VILLAGE_FLETCHER, PowerfulEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.SIMPLE_DUNGEON, PowerfulEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.05F, LootTables.BASTION_TREASURE, PowerfulEnchantment.ID);

		// 明朗 —— 常见：废弃矿井 10% / 地牢 8% / 沉船 7% / 雪原小屋 6%
		registerLoot(QUALITY_COMMON, 0.10F, LootTables.ABANDONED_MINESHAFT, BrightEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.08F, LootTables.SIMPLE_DUNGEON, BrightEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.07F, LootTables.SHIPWRECK_TREASURE, BrightEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.06F, LootTables.IGLOO_CHEST, BrightEnchantment.ID);

		// 毁灭 —— 宝藏：下界要塞 20% / 林地府邸 15% / 堡垒遗迹 10% / 掠夺者前哨站 10%
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.NETHER_BRIDGE, DestructionEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.15F, LootTables.WOODLAND_MANSION, DestructionEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.BASTION_TREASURE, DestructionEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.PILLAGER_OUTPOST, DestructionEnchantment.ID);

		// 焚灭 —— 宝藏：丛林神殿 35% / 沙漠神殿 20% / 海底废墟 20% / 林地府邸 10%
		registerLoot(QUALITY_TREASURE, 0.35F, LootTables.JUNGLE_TEMPLE, IncinerateEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.DESERT_PYRAMID, IncinerateEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.20F, LootTables.UNDERWATER_RUIN_BIG, IncinerateEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, LootTables.WOODLAND_MANSION, IncinerateEnchantment.ID);

		// 浮空速掘 —— 稀有：废弃传送门 30% / 沉船 20% / 末地城 10% / 远古城市 8%
		registerLoot(QUALITY_RARE, 0.30F, LootTables.RUINED_PORTAL, AerialHasteEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.20F, LootTables.SHIPWRECK_TREASURE, AerialHasteEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.10F, LootTables.END_CITY_TREASURE, AerialHasteEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.08F, LootTables.ANCIENT_CITY, AerialHasteEnchantment.ID);

		// ===== 第二批（权重按文档 §3；chance 按稀有度安排，详见仓库 README 百科）=====
		// 夺首 —— 宝藏：林地府邸 12% / 掠夺者前哨站 8%
		registerLoot(QUALITY_TREASURE, 0.12F, 5, LootTables.WOODLAND_MANSION, BeheadingEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.08F, 3, LootTables.PILLAGER_OUTPOST, BeheadingEnchantment.ID);

		// 枯萎 —— 宝藏：下界要塞 22% / 堡垒遗迹 15% / 远古城市 12%
		registerLoot(QUALITY_TREASURE, 0.22F, 4, LootTables.NETHER_BRIDGE, WitherAspectEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.15F, 3, LootTables.BASTION_TREASURE, WitherAspectEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.12F, 2, LootTables.ANCIENT_CITY, WitherAspectEnchantment.ID);

		// 狂暴 —— 宝藏：林地府邸 18% / 掠夺者前哨站 14%
		registerLoot(QUALITY_TREASURE, 0.18F, 4, LootTables.WOODLAND_MANSION, FrenzyEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.14F, 3, LootTables.PILLAGER_OUTPOST, FrenzyEnchantment.ID);

		// 残杀 —— 宝藏：远古城市 20% / 末地城 15% / 林地府邸 10%
		registerLoot(QUALITY_TREASURE, 0.20F, 3, LootTables.ANCIENT_CITY, ExecuteEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.15F, 2, LootTables.END_CITY_TREASURE, ExecuteEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, 2, LootTables.WOODLAND_MANSION, ExecuteEnchantment.ID);

		// 恶魔交易 —— 宝藏：远古城市 14% / 埋藏的宝藏 10%
		registerLoot(QUALITY_TREASURE, 0.14F, 2, LootTables.ANCIENT_CITY, DemonicPactEnchantment.ID);
		registerLoot(QUALITY_TREASURE, 0.10F, 2, LootTables.BURIED_TREASURE, DemonicPactEnchantment.ID);

		// 吸血 —— 常见：要塞图书馆 18%
		registerLoot(QUALITY_COMMON, 0.18F, 2, LootTables.STRONGHOLD_LIBRARY, LifeStealEnchantment.ID);
		// 汲灵 —— 常见：要塞图书馆 24%
		registerLoot(QUALITY_COMMON, 0.24F, 3, LootTables.STRONGHOLD_LIBRARY, SoulSiphonEnchantment.ID);

		// 淬毒 —— 稀有：丛林神庙 28% / 沉船宝藏 16%
		registerLoot(QUALITY_RARE, 0.28F, 5, LootTables.JUNGLE_TEMPLE, VenomEnchantment.ID);
		registerLoot(QUALITY_RARE, 0.16F, 3, LootTables.SHIPWRECK_TREASURE, VenomEnchantment.ID);

		// 贯穿 —— 稀有：沉船宝藏 22%（原计划的海底神殿无宝箱表，已按用户决策删除）
		registerLoot(QUALITY_RARE, 0.22F, 4, LootTables.SHIPWRECK_TREASURE, PenetrationEnchantment.ID);

		// 粉碎 —— 常见：大型海底废墟 20% / 沉船宝藏 14% / 废弃矿井 12%
		registerLoot(QUALITY_COMMON, 0.20F, 3, LootTables.UNDERWATER_RUIN_BIG, SmashingEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.14F, 2, LootTables.SHIPWRECK_TREASURE, SmashingEnchantment.ID);
		registerLoot(QUALITY_COMMON, 0.12F, 2, LootTables.ABANDONED_MINESHAFT, SmashingEnchantment.ID);
	}

	/**
	 * 注册各附魔的事件回调。
	 *
	 * <p>1.20.1 与 26.3 的签名差异：不再需要 {@code HolderLookup.Provider}。26.3 要它是因为附魔住在
	 * 动态注册表里，只有读完数据包才能解析成 Holder；1.20.1 的附魔在模组构造期就进了静态注册表，
	 * 任何时刻都能按 id 直接取到对象。</p>
	 */
	public static void registerEventCallbacks() {
		if (callbacksRegistered) {
			return;
		}
		callbacksRegistered = true;
		EnchantmentEventRegistrar registrar = EnchantmentEventRegistrar.INSTANCE;

		ChineseEnchantment.registerCallbacks(registrar);
		LumberjackEnchantment.registerCallbacks(registrar);
		PowerfulEnchantment.registerCallbacks(registrar);
		BrightEnchantment.registerCallbacks(registrar);
		DestructionEnchantment.registerCallbacks(registrar);
		IncinerateEnchantment.registerCallbacks(registrar);
		AerialHasteEnchantment.registerCallbacks(registrar);
		// 第二批事件回调
		// 注：LifeSteal 走两加载器 AFTER_DAMAGE 桥接；Smashing 与免死两附魔走本模组 Mixin；
		// DemonicPact/UndyingDrop 无事件回调
		BeheadingEnchantment.registerCallbacks(registrar);
		VenomEnchantment.registerCallbacks(registrar);
		WitherAspectEnchantment.registerCallbacks(registrar);
		FrenzyEnchantment.registerCallbacks(registrar);
		GentleDescentEnchantment.registerCallbacks(registrar);
		ExecuteEnchantment.registerCallbacks(registrar);
		SoulSiphonEnchantment.registerCallbacks(registrar);
		PenetrationEnchantment.registerCallbacks(registrar);
	}

	/** 单条战利品注入：一张表、一个附魔、一个百分比，以附魔书形式注入，权重 1。 */
	private static void registerLoot(int quality, float chance, String lootTable, String enchantmentId) {
		registerLoot(quality, chance, 1, lootTable, enchantmentId);
	}

	/** 单条战利品注入（带权重）：一张表、一个附魔、一个百分比、一个权重，以附魔书形式注入。 */
	private static void registerLoot(int quality, float chance, int weight, String lootTable,
									 String enchantmentId) {
		MerlinApi.loot().register(LootInjectionBuilder.create()
			.toTables(lootTable)
			.asBook()
			.withEnchantments(enchantmentId)
			.chance(chance)
			.weight(weight)
			.quality(quality));
	}

	/**
	 * 按 id 取附魔对象。
	 *
	 * <p>1.20.1 的替代品：26.3 这里接受一个 {@code HolderLookup.Provider} 并返回
	 * {@code Holder<Enchantment>}，因为当时附魔在动态注册表里。1.20.1 的附魔是静态注册表的普通对象，
	 * 直接用 id 取即可，没有 Holder 这一层。</p>
	 *
	 * @param id 附魔全名，如 {@code practical_enchantments:venom}
	 * @return 附魔对象
	 * @throws IllegalStateException 附魔未注册时
	 */
	public static Enchantment resolveEnchantment(String id) {
		ResourceLocation enchantId = new ResourceLocation(id);
		Enchantment enchantment = net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.get(enchantId);
		if (enchantment == null) {
			throw new IllegalStateException("Practical Enchantments: 附魔未注册: " + id);
		}
		return enchantment;
	}
}
