package com.practicalenchantments;

import com.enchantlib.api.EnchantmentEntrypoint;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.ExclusiveGroupBuilder;
import com.enchantlib.api.ExclusiveGroupRegistrar;
import com.enchantlib.api.LootInjectionBuilder;
import com.enchantlib.api.LootInjectionRegistrar;
import com.enchantlib.api.LootTables;
import com.enchantlib.api.TradeableEnchantmentsBuilder;
import com.enchantlib.api.VillagerTradeBuilder;
import com.enchantlib.api.VillagerTradeRegistrar;
import com.enchantlib.api.VillagerTrades;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.enchantment.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Practical Enchantments 模组入口。
 *
 * <p>本类不实现任何加载器接口：Fabric 通过 {@code fabric.mod.json} 的
 * {@code enchantlib:enchantments} entrypoint 自动发现；NeoForge 由
 * {@code PracticalEnchantmentsNeoForge} 在 {@code @Mod} 构造器中调用
 * {@link com.enchantlib.api.EnchantmentApi#register(Object...)} 显式注册。</p>
 *
 * <p>业务逻辑一份，位于 {@code common}，不含任何 {@code net.fabricmc} /
 * {@code net.neoforged} 引用。</p>
 */
public final class PracticalEnchantments implements EnchantmentEntrypoint {

	public static final String MOD_ID = "practical_enchantments";

	/** 战利品注入 quality 档位：常见 */
	private static final int QUALITY_COMMON = 0;
	/** 战利品注入 quality 档位：稀有 */
	private static final int QUALITY_RARE = 1;
	/** 战利品注入 quality 档位：宝藏 */
	private static final int QUALITY_TREASURE = 2;

	@Override
	public void onRegisterEnchantments(EnchantmentRegistrar registrar) {
		// 第一批
		ChineseEnchantment.register(registrar);
		LumberjackEnchantment.register(registrar);
		PowerfulEnchantment.register(registrar);
		BrightEnchantment.register(registrar);
		DestructionEnchantment.register(registrar);
		IncinerateEnchantment.register(registrar);
		AerialHasteEnchantment.register(registrar);
		// 第二批
		BeheadingEnchantment.register(registrar);
		LifeStealEnchantment.register(registrar);
		VenomEnchantment.register(registrar);
		WitherAspectEnchantment.register(registrar);
		FrenzyEnchantment.register(registrar);
		DemonicPactEnchantment.register(registrar);
		UndyingDropEnchantment.register(registrar);
		GentleDescentEnchantment.register(registrar);
		ExecuteEnchantment.register(registrar);
		SoulSiphonEnchantment.register(registrar);
		PenetrationEnchantment.register(registrar);
		SmashingEnchantment.register(registrar);
	}

	/**
	 * 注册自建互斥组。
	 *
	 * <p>伐木工与毁灭直接引用原版 {@code #minecraft:exclusive_set/mining}
	 * （成员恰为时运、精准采集），无需在此重复声明。这里只处理原版标签粒度不匹配的两项：</p>
	 * <ul>
	 *   <li><b>crossbow</b>：强劲 ↔ 多重射击。原版 {@code #minecraft:exclusive_set/crossbow}
	 *       同时含穿透，而强劲按设计要求可与穿透叠加，故自建只含多重射击的组。</li>
	 *   <li><b>looting</b>：焚灭 ↔ 抢夺。原版无对应粒度标签，自建。</li>
	 * </ul>
	 */
	@Override
	public void onRegisterExclusiveGroups(ExclusiveGroupRegistrar registrar) {
		registrar.register(ExclusiveGroupBuilder.create(MOD_ID, PowerfulEnchantment.GROUP_NAME)
			.add(PowerfulEnchantment.ID)
			.add("minecraft:multishot"));

		registrar.register(ExclusiveGroupBuilder.create(MOD_ID, IncinerateEnchantment.GROUP_NAME)
			.add(IncinerateEnchantment.ID)
			.add("minecraft:looting"));

		// 第二批互斥组
		// 淬毒 / 枯萎 / 火焰附加（三向互斥，组标签在 VenomEnchantment 派生）
		registrar.register(ExclusiveGroupBuilder.create(MOD_ID, "blade_effect")
			.add(VenomEnchantment.ID)
			.add(WitherAspectEnchantment.ID)
			.add("minecraft:fire_aspect"));
		// 恶魔交易 / 掉落不死亡
		registrar.register(ExclusiveGroupBuilder.create(MOD_ID, "death_save")
			.add(DemonicPactEnchantment.ID)
			.add(UndyingDropEnchantment.ID));
		// 羽落 / 摔落缓冲
		registrar.register(ExclusiveGroupBuilder.create(MOD_ID, "slow_fall")
			.add(GentleDescentEnchantment.ID)
			.add("minecraft:feather_falling"));
		// 贯穿 / 激流
		registrar.register(ExclusiveGroupBuilder.create(MOD_ID, "trident_riptide")
			.add(PenetrationEnchantment.ID)
			.add("minecraft:riptide"));
	}

	/**
	 * 注册战利品注入（按《附魔名单（第一批）》的箱子与百分比逐条还原）。
	 *
	 * <p>同一附魔在不同箱子上的百分比不同，无法合并成一条规则，故一张表一条；
	 * 全部以附魔书形式注入。</p>
	 */
	@Override
	public void onRegisterLootInjections(LootInjectionRegistrar registrar) {
		// 中国人 —— 宝藏，仅远古城市 10%
		registerLoot(registrar, QUALITY_TREASURE, 0.10F, LootTables.ANCIENT_CITY, ChineseEnchantment.ID);

		// 伐木工 —— 稀有：村庄铁匠铺 8% / 废弃矿井 6% / 掠夺者前哨站 5% / 地牢 4%
		registerLoot(registrar, QUALITY_RARE, 0.08F, LootTables.VILLAGE_TOOLSMITH, LumberjackEnchantment.ID);
		registerLoot(registrar, QUALITY_RARE, 0.06F, LootTables.ABANDONED_MINESHAFT, LumberjackEnchantment.ID);
		registerLoot(registrar, QUALITY_RARE, 0.05F, LootTables.PILLAGER_OUTPOST, LumberjackEnchantment.ID);
		registerLoot(registrar, QUALITY_RARE, 0.04F, LootTables.SIMPLE_DUNGEON, LumberjackEnchantment.ID);

		// 强劲 —— 常见：掠夺者前哨站 12% / 村庄制箭师 8% / 地牢 6% / 堡垒遗迹 5%
		registerLoot(registrar, QUALITY_COMMON, 0.12F, LootTables.PILLAGER_OUTPOST, PowerfulEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.08F, LootTables.VILLAGE_FLETCHER, PowerfulEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.06F, LootTables.SIMPLE_DUNGEON, PowerfulEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.05F, LootTables.BASTION_TREASURE, PowerfulEnchantment.ID);

		// 明朗 —— 常见：废弃矿井 10% / 地牢 8% / 沉船 7% / 雪原小屋 6%
		registerLoot(registrar, QUALITY_COMMON, 0.10F, LootTables.ABANDONED_MINESHAFT, BrightEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.08F, LootTables.SIMPLE_DUNGEON, BrightEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.07F, LootTables.SHIPWRECK_TREASURE, BrightEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.06F, LootTables.IGLOO_CHEST, BrightEnchantment.ID);

		// 毁灭 —— 宝藏：下界要塞 20% / 林地府邸 15% / 堡垒遗迹 10% / 掠夺者前哨站 10%
		registerLoot(registrar, QUALITY_TREASURE, 0.20F, LootTables.NETHER_BRIDGE, DestructionEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.15F, LootTables.WOODLAND_MANSION, DestructionEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.10F, LootTables.BASTION_TREASURE, DestructionEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.10F, LootTables.PILLAGER_OUTPOST, DestructionEnchantment.ID);

		// 焚灭 —— 宝藏：丛林神殿 35% / 沙漠神殿 20% / 海底废墟 20% / 林地府邸 10%
		registerLoot(registrar, QUALITY_TREASURE, 0.35F, LootTables.JUNGLE_TEMPLE, IncinerateEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.20F, LootTables.DESERT_PYRAMID, IncinerateEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.20F, LootTables.UNDERWATER_RUIN_BIG, IncinerateEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.10F, LootTables.WOODLAND_MANSION, IncinerateEnchantment.ID);

		// 浮空速掘 —— 稀有：废弃传送门 30% / 沉船 20% / 末地城 10% / 远古城市 8%
		registerLoot(registrar, QUALITY_RARE, 0.30F, LootTables.RUINED_PORTAL, AerialHasteEnchantment.ID);
		registerLoot(registrar, QUALITY_RARE, 0.20F, LootTables.SHIPWRECK_TREASURE, AerialHasteEnchantment.ID);
		registerLoot(registrar, QUALITY_RARE, 0.10F, LootTables.END_CITY_TREASURE, AerialHasteEnchantment.ID);
		registerLoot(registrar, QUALITY_RARE, 0.08F, LootTables.ANCIENT_CITY, AerialHasteEnchantment.ID);

		// ===== 第二批（权重按文档 §3；chance 按稀有度安排，详见仓库 README 百科）=====
		// 夺首 —— 宝藏：林地府邸 12% / 掠夺者前哨站 8%
		registerLoot(registrar, QUALITY_TREASURE, 0.12F, 5, LootTables.WOODLAND_MANSION, BeheadingEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.08F, 3, LootTables.PILLAGER_OUTPOST, BeheadingEnchantment.ID);

		// 枯萎 —— 宝藏：下界要塞 22% / 堡垒遗迹 15% / 远古城市 12%
		registerLoot(registrar, QUALITY_TREASURE, 0.22F, 4, LootTables.NETHER_BRIDGE, WitherAspectEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.15F, 3, LootTables.BASTION_TREASURE, WitherAspectEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.12F, 2, LootTables.ANCIENT_CITY, WitherAspectEnchantment.ID);

		// 狂暴 —— 宝藏：林地府邸 18% / 掠夺者前哨站 14%
		registerLoot(registrar, QUALITY_TREASURE, 0.18F, 4, LootTables.WOODLAND_MANSION, FrenzyEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.14F, 3, LootTables.PILLAGER_OUTPOST, FrenzyEnchantment.ID);

		// 残杀 —— 宝藏：远古城市 20% / 末地城 15% / 林地府邸 10%
		registerLoot(registrar, QUALITY_TREASURE, 0.20F, 3, LootTables.ANCIENT_CITY, ExecuteEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.15F, 2, LootTables.END_CITY_TREASURE, ExecuteEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.10F, 2, LootTables.WOODLAND_MANSION, ExecuteEnchantment.ID);

		// 恶魔交易 —— 宝藏：远古城市 14% / 埋藏的宝藏 10%
		registerLoot(registrar, QUALITY_TREASURE, 0.14F, 2, LootTables.ANCIENT_CITY, DemonicPactEnchantment.ID);
		registerLoot(registrar, QUALITY_TREASURE, 0.10F, 2, LootTables.BURIED_TREASURE, DemonicPactEnchantment.ID);

		// 吸血 —— 常见：要塞图书馆 18%
		registerLoot(registrar, QUALITY_COMMON, 0.18F, 2, LootTables.STRONGHOLD_LIBRARY, LifeStealEnchantment.ID);
		// 汲灵 —— 常见：要塞图书馆 24%
		registerLoot(registrar, QUALITY_COMMON, 0.24F, 3, LootTables.STRONGHOLD_LIBRARY, SoulSiphonEnchantment.ID);

		// 淬毒 —— 稀有：丛林神庙 28% / 沉船宝藏 16%
		registerLoot(registrar, QUALITY_RARE, 0.28F, 5, LootTables.JUNGLE_TEMPLE, VenomEnchantment.ID);
		registerLoot(registrar, QUALITY_RARE, 0.16F, 3, LootTables.SHIPWRECK_TREASURE, VenomEnchantment.ID);

		// 贯穿 —— 稀有：沉船宝藏 22%（原计划的海底神殿无宝箱表，已按用户决策删除）
		registerLoot(registrar, QUALITY_RARE, 0.22F, 4, LootTables.SHIPWRECK_TREASURE, PenetrationEnchantment.ID);

		// 粉碎 —— 常见：大型海底废墟 20% / 沉船宝藏 14% / 废弃矿井 12%
		registerLoot(registrar, QUALITY_COMMON, 0.20F, 3, LootTables.UNDERWATER_RUIN_BIG, SmashingEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.14F, 2, LootTables.SHIPWRECK_TREASURE, SmashingEnchantment.ID);
		registerLoot(registrar, QUALITY_COMMON, 0.12F, 2, LootTables.ABANDONED_MINESHAFT, SmashingEnchantment.ID);
	}

	/**
	 * 注册村民交易。
	 *
	 * <p>除中国人（不可交易，仅远古城市战利品）外的 6 个附魔加入
	 * {@code #minecraft:tradeable}，由原版图书管理员按 min_cost/max_cost 自动定价出售；
	 * 明朗额外由盔甲商出售附魔钻石头盔。</p>
	 */
	@Override
	public void onRegisterVillagerTrades(VillagerTradeRegistrar registrar) {
		registrar.registerTradeableEnchantments(TradeableEnchantmentsBuilder.create()
			.addEnchantments(
				LumberjackEnchantment.ID,
				PowerfulEnchantment.ID,
				BrightEnchantment.ID,
				DestructionEnchantment.ID,
				IncinerateEnchantment.ID,
				AerialHasteEnchantment.ID,
				// 第二批可交易（6 个）
				LifeStealEnchantment.ID,
				VenomEnchantment.ID,
				SoulSiphonEnchantment.ID,
				BeheadingEnchantment.ID,
				WitherAspectEnchantment.ID,
				FrenzyEnchantment.ID));

		// 盔甲商：附魔「明朗」的钻石头盔
		registrar.registerTrade(VillagerTradeBuilder
			.create(MOD_ID, "armorer/3/bright_diamond_helmet")
			.profession(VillagerTrades.ARMORER)
			.level(VillagerTrades.LEVEL_3)
			.asItem(Items.DIAMOND_HELMET)
			.withEnchantments(BrightEnchantment.ID)
			.emeralds(18)
			.noAdditionalItem()
			.maxUses(3)
			.xp(10)
			.priceMultiplier(0.2F));
	}

	@Override
	public void onRegisterEventCallbacks(EnchantmentEventRegistrar registrar,
										 HolderLookup.Provider registries) {
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
	private static void registerLoot(LootInjectionRegistrar registrar, int quality, float chance,
									 String lootTable, String enchantmentId) {
		registerLoot(registrar, quality, chance, 1, lootTable, enchantmentId);
	}

	/** 单条战利品注入（带权重）：一张表、一个附魔、一个百分比、一个权重，以附魔书形式注入。 */
	private static void registerLoot(LootInjectionRegistrar registrar, int quality, float chance, int weight,
									 String lootTable, String enchantmentId) {
		registrar.register(LootInjectionBuilder.create()
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
