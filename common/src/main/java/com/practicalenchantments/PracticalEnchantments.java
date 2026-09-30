package com.practicalenchantments;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.LootTables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.config.PracticalConfig;
import com.practicalenchantments.enchantment.*;
import com.practicalenchantments.notice.MerlinLibNotice;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Practical Enchantments 模组入口（前置：MerlinLib）。
 *
 * <p>本类不实现任何加载器接口：两端入口只调用一次 {@link #bootstrap()}，把本模组的三件事挂到
 * MerlinLib 的服务器生命周期上：</p>
 * <ul>
 *   <li><b>战利品注入</b>：必须赶在数据包读战利品表<b>之前</b>登记，所以挂在"服务器开始启动"；</li>
 *   <li><b>事件回调</b>：要解析附魔 Holder，必须等数据包读完（动态注册表就绪），所以挂在"服务器启动完成"；</li>
 *   <li><b>配置与进服预告</b>：与事件回调同一时机。</li>
 * </ul>
 *
 * <p>附魔定义、互斥组、{@code #minecraft:tradeable} 标签与村民交易都已改为数据包资源
 * （{@code data/} 下），本类不再注册它们。</p>
 *
 * <p>业务逻辑一份，位于 {@code common}，不含任何 {@code net.fabricmc} / {@code net.neoforged} 引用。</p>
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

	private PracticalEnchantments() {
	}

	/**
	 * 由两端入口各调用一次，把本模组的工作挂到 MerlinLib 的服务器生命周期上。
	 *
	 * <p>幂等：重复调用只会多挂几个只会执行一次的 lambda，真正的登记由下面的布尔量守住。</p>
	 */
	public static void bootstrap() {
		// 战利品：数据包读表之前就位（ServerStarting 早于数据包加载）。
		MerlinApi.lifecycle().onServerStarting(server -> registerLootInjections());
		// 事件回调与配置：数据包读完之后（动态注册表此时才有附魔）。
		MerlinApi.lifecycle().onServerStarted(server -> {
			PracticalConfig.load();
			MerlinLibNotice.register();
			registerEventCallbacks(server.registryAccess());
		});
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
	 * @param registries 注册表访问，用于把附魔 id 解析成 Holder
	 */
	public static void registerEventCallbacks(HolderLookup.Provider registries) {
		if (callbacksRegistered) {
			return;
		}
		callbacksRegistered = true;
		EnchantmentEventRegistrar registrar = EnchantmentEventRegistrar.INSTANCE;

		ChineseEnchantment.registerCallbacks(registrar, registries);
		LumberjackEnchantment.registerCallbacks(registrar, registries);
		PowerfulEnchantment.registerCallbacks(registrar, registries);
		BrightEnchantment.registerCallbacks(registrar, registries);
		DestructionEnchantment.registerCallbacks(registrar, registries);
		IncinerateEnchantment.registerCallbacks(registrar, registries);
		AerialHasteEnchantment.registerCallbacks(registrar, registries);
		// 第二批事件回调
		// 注：LifeSteal 走两加载器 AFTER_DAMAGE 桥接（在 fabric/neoforge 入口注册）；
		// Smashing 与免死两附魔走本模组 Mixin；DemonicPact/UndyingDrop 无事件回调
		BeheadingEnchantment.registerCallbacks(registrar, registries);
		VenomEnchantment.registerCallbacks(registrar, registries);
		WitherAspectEnchantment.registerCallbacks(registrar, registries);
		FrenzyEnchantment.registerCallbacks(registrar, registries);
		GentleDescentEnchantment.registerCallbacks(registrar, registries);
		ExecuteEnchantment.registerCallbacks(registrar, registries);
		SoulSiphonEnchantment.registerCallbacks(registrar, registries);
		PenetrationEnchantment.registerCallbacks(registrar, registries);
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
	 * 通过注册表解析附魔 Holder。
	 */
	public static Holder<Enchantment> resolveEnchantment(HolderLookup.Provider registries, String id) {
		Identifier enchantId = Identifier.parse(id);
		ResourceKey<Enchantment> resourceKey = ResourceKey.create(Registries.ENCHANTMENT, enchantId);
		return registries.lookupOrThrow(Registries.ENCHANTMENT)
			.get(resourceKey)
			.orElseThrow(() -> new IllegalStateException("Practical Enchantments: 附魔未注册: " + id));
	}
}
