package com.enchantlib.examplemod;

import static com.enchantlib.examplemod.enchantment.AutoSmeltEnchantment.AUTO_SMELT_ID;
import static com.enchantlib.examplemod.enchantment.DisguiseEnchantments.AQUATIC_DISGUISE_ID;
import static com.enchantlib.examplemod.enchantment.DisguiseEnchantments.ARTHROPOD_DISGUISE_ID;
import static com.enchantlib.examplemod.enchantment.DisguiseEnchantments.ILLAGER_DISGUISE_ID;
import static com.enchantlib.examplemod.enchantment.DisguiseEnchantments.UNDEAD_DISGUISE_ID;
import static com.enchantlib.examplemod.enchantment.EchoMarkEnchantment.ECHO_MARK_ID;
import static com.enchantlib.examplemod.enchantment.ExecutionerEnchantment.EXECUTIONER_ID;
import static com.enchantlib.examplemod.enchantment.FrostEnchantment.FROST_ID;
import static com.enchantlib.examplemod.enchantment.HeartburnEnchantment.HEARTBURN_ID;
import static com.enchantlib.examplemod.enchantment.RetributionEnchantment.RETRIBUTION_ID;
import static com.enchantlib.examplemod.enchantment.SilentPactEnchantment.SILENT_PACT_ID;
import static com.enchantlib.examplemod.enchantment.SlimeSlayerEnchantment.SLIME_SLAYER_ID;

import com.enchantlib.api.EnchantmentEntrypoint;
import com.enchantlib.api.ExclusiveGroupBuilder;
import com.enchantlib.api.ExclusiveGroupRegistrar;
import com.enchantlib.api.LootInjectionBuilder;
import com.enchantlib.api.LootInjectionRegistrar;
import com.enchantlib.api.LootTables;
import com.enchantlib.api.TradeableEnchantmentsBuilder;
import com.enchantlib.api.VillagerTradeBuilder;
import com.enchantlib.api.VillagerTradeRegistrar;
import com.enchantlib.api.VillagerTrades;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.examplemod.enchantment.AutoSmeltEnchantment;
import com.enchantlib.examplemod.enchantment.DisguiseEnchantments;
import com.enchantlib.examplemod.enchantment.EchoMarkEnchantment;
import com.enchantlib.examplemod.enchantment.ExecutionerEnchantment;
import com.enchantlib.examplemod.enchantment.FrostEnchantment;
import com.enchantlib.examplemod.enchantment.HeartburnEnchantment;
import com.enchantlib.examplemod.enchantment.RetributionEnchantment;
import com.enchantlib.examplemod.enchantment.SilentPactEnchantment;
import com.enchantlib.examplemod.enchantment.SlimeSlayerEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * EnchantLib 官方示例模组入口。
 *
 * <p>展示 EnchantLib API 的完整用法,包括附魔注册、事件回调、玩家分类 API、
 * 互斥组、战利品注入、村民交易等。</p>
 *
 * <p>每个附魔的实现(注册 + 事件回调)拆分到 {@code enchantment/} 子包中各自的文件,
 * 本类仅作为入口,负责统一调度注册流程,以及管理跨多个附魔的获取途径
 * (互斥组、战利品注入、村民交易)。</p>
 *
 * <h2>附魔列表(共 12 个)</h2>
 * <ul>
 *   <li><b>阶段3 简单附魔</b>:
 *     <ul>
 *       <li>亡灵伪装(undead_disguise) - 头盔 I 级,穿戴被视为亡灵</li>
 *       <li>深海伪装(aquatic_disguise) - 头盔 I 级,水生生物不攻击</li>
 *       <li>灾厄伪装(illager_disguise) - 头盔 I 级,灾厄不攻击</li>
 *       <li>节肢伪装(arthropod_disguise) - 头盔 I 级,节肢不攻击</li>
 *       <li>自动烧炼(auto_smelt) - 镐/铲 I 级,破坏方块直接掉落熔炼产物</li>
 *       <li>粘液杀手(slime_slayer) - 剑/斧 V 级,对粘液类(史莱姆/岩浆怪/硫方怪)额外伤害 + 范围溅射</li>
 *     </ul>
 *   </li>
 *   <li><b>阶段4 进阶附魔</b>:
 *     <ul>
 *       <li>处刑人(executioner) - 剑/斧 III 级,高血减伤/低血增伤(阈值与幅度随等级变化)</li>
 *       <li>回敬(retribution) - 剑/斧 II 级,受击5秒内下次攻击附额外伤害</li>
 *       <li>静默契约(silent_pact) - 胸甲 II 级,压制自然回血+击杀回血</li>
 *     </ul>
 *   </li>
 *   <li><b>阶段5 高级附魔</b>:
 *     <ul>
 *       <li>焚心(heartburn) - 剑 II 级,持续真实伤害+火焰粒子,与火焰附加互斥</li>
 *       <li>音痕(echo_mark) - 弓 II 级,箭命中留标记,受伤害引爆造成已损失生命50%伤害+爆炸音效</li>
 *       <li>冰霜附魔(frost) - 剑 II 级,攻击施加缓慢+旋转冰方块展示实体+雪花粒子</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>阶段6 获取途径</h2>
 * <ul>
 *   <li><b>互斥组</b>:
 *     <ul>
 *       <li>disguise - 4 个伪装附魔互相排斥</li>
 *       <li>fire_weapon - 焚心 + minecraft:fire_aspect 互斥</li>
 *     </ul>
 *   </li>
 *   <li><b>战利品注入</b>:9 类战利品表(地下城/矿道/沙漠神殿/丛林神庙/末地城/林地府邸/远古城市/沉船补给/沉船宝藏/海底废墟/掠夺者前哨站),
 *       含伪装系列分散到不同结构</li>
 *   <li><b>村民交易</b>:7 个附魔加入 #minecraft:tradeable(图书管理员自动出售)
 *       + 6 条自定义交易(武器匠/制图师/皮匠/牧师/盔甲匠)</li>
 *   <li><b>宝藏附魔</b>:静默契约仅从远古城市战利品获取,不开放交易与附魔台</li>
 * </ul>
 *
 * @since 1.0.0
 */
public final class ExampleModEnchantments implements EnchantmentEntrypoint {

	/** 示例模组命名空间标识符 */
	public static final String MOD_ID = "enchantlib-examplemod";

	@Override
	public void onRegisterEnchantments(com.enchantlib.api.EnchantmentRegistrar registrar) {
		// 示例附魔已注释，保留代码作为参考
		// DisguiseEnchantments.register(registrar);
		// AutoSmeltEnchantment.register(registrar);
		// SlimeSlayerEnchantment.register(registrar);
		// ExecutionerEnchantment.register(registrar);
		// RetributionEnchantment.register(registrar);
		// SilentPactEnchantment.register(registrar);
		// HeartburnEnchantment.register(registrar);
		// EchoMarkEnchantment.register(registrar);
		// FrostEnchantment.register(registrar);
	}

	/**
	 * 注册互斥组。
	 *
	 * <p>本示例模组定义两个互斥组:</p>
	 * <ul>
	 *   <li><b>disguise(伪装4合一)</b>:4 个伪装附魔互斥,铁砧合并也拒绝共存</li>
	 *   <li><b>fire_weapon(火焰武器)</b>:焚心 + minecraft:fire_aspect,防止持续真伤和点燃叠加</li>
	 * </ul>
	 *
	 * <p>注:伪装附魔天然只能戴1个头盔,此互斥组主要用于铁砧合并场景的边界保护;
	 * fire_weapon 互斥组则直接影响战斗平衡。</p>
	 */
	@Override
	public void onRegisterExclusiveGroups(ExclusiveGroupRegistrar registrar) {
		// 示例互斥组已注释，保留代码作为参考
		// registrar.register(ExclusiveGroupBuilder.create(MOD_ID, "disguise")
		// 	.add(UNDEAD_DISGUISE_ID)
		// 	.add(AQUATIC_DISGUISE_ID)
		// 	.add(ILLAGER_DISGUISE_ID)
		// 	.add(ARTHROPOD_DISGUISE_ID));
		//
		// registrar.register(ExclusiveGroupBuilder.create(MOD_ID, "fire_weapon")
		// 	.add(HEARTBURN_ID)
		// 	.add("minecraft:fire_aspect"));
	}

	/**
	 * 注册战利品注入规则。
	 *
	 * <p>将自定义附魔以附魔书形式注入原版战利品表,按附魔稀有度分配到不同箱子:</p>
	 * <ul>
	 *   <li><b>普通战斗附魔</b>(粘液杀手、处刑人):简单地下城、废弃矿道,30%</li>
	 *   <li><b>中级战斗附魔</b>(回敬):沙漠神殿、丛林神庙,25%</li>
	 *   <li><b>稀有战斗附魔</b>(焚心、冰霜):末地城宝藏,40%</li>
	 *   <li><b>罕见附魔</b>(音痕):林地府邸,30%</li>
	 *   <li><b>稀有防御附魔</b>(静默契约):远古城市,35%</li>
	 *   <li><b>工具附魔</b>(自动烧炼):沉船补给,20%</li>
	 *   <li><b>伪装系列</b>(亡灵伪装、节肢伪装):沙漠神殿、丛林神庙,25%</li>
	 *   <li><b>伪装系列</b>(深海伪装):沉船宝藏、海底废墟,30%</li>
	 *   <li><b>伪装系列</b>(灾厄伪装):掠夺者前哨站,25%</li>
	 * </ul>
	 *
	 * <p>伪装系列4个互斥,分散到不同结构箱子,玩家按需探索对应分类。</p>
	 */
	@Override
	public void onRegisterLootInjections(LootInjectionRegistrar registrar) {
		// 示例战利品注入已注释，保留代码作为参考
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.SIMPLE_DUNGEON, LootTables.ABANDONED_MINESHAFT)
		// 	.asBook()
		// 	.withEnchantments(SLIME_SLAYER_ID, EXECUTIONER_ID)
		// 	.chance(0.30F)
		// 	.weight(2)
		// 	.quality(0));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.DESERT_PYRAMID, LootTables.JUNGLE_TEMPLE)
		// 	.asBook()
		// 	.withEnchantments(RETRIBUTION_ID)
		// 	.chance(0.25F)
		// 	.weight(1)
		// 	.quality(1));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.END_CITY_TREASURE)
		// 	.asBook()
		// 	.withEnchantments(HEARTBURN_ID, FROST_ID)
		// 	.chance(0.40F)
		// 	.weight(1)
		// 	.quality(2));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.WOODLAND_MANSION)
		// 	.asBook()
		// 	.withEnchantments(ECHO_MARK_ID)
		// 	.chance(0.30F)
		// 	.weight(1)
		// 	.quality(2));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.ANCIENT_CITY)
		// 	.asBook()
		// 	.withEnchantments(SILENT_PACT_ID)
		// 	.chance(0.35F)
		// 	.weight(1)
		// 	.quality(2));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.SHIPWRECK_SUPPLY)
		// 	.asBook()
		// 	.withEnchantments(AUTO_SMELT_ID)
		// 	.chance(0.20F)
		// 	.weight(1)
		// 	.quality(0));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.DESERT_PYRAMID, LootTables.JUNGLE_TEMPLE)
		// 	.asBook()
		// 	.withEnchantments(UNDEAD_DISGUISE_ID, ARTHROPOD_DISGUISE_ID)
		// 	.chance(0.25F)
		// 	.weight(1)
		// 	.quality(1));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.SHIPWRECK_TREASURE, LootTables.UNDERWATER_RUIN_BIG)
		// 	.asBook()
		// 	.withEnchantments(AQUATIC_DISGUISE_ID)
		// 	.chance(0.30F)
		// 	.weight(1)
		// 	.quality(1));
		//
		// registrar.register(LootInjectionBuilder.create()
		// 	.toTables(LootTables.PILLAGER_OUTPOST)
		// 	.asBook()
		// 	.withEnchantments(ILLAGER_DISGUISE_ID)
		// 	.chance(0.25F)
		// 	.weight(1)
		// 	.quality(1));
	}

	/**
	 * 注册村民交易。
	 *
	 * <p>分两类:</p>
	 * <ul>
	 *   <li><b>自动可交易</b>:7 个战斗/工具附魔加入 {@code #minecraft:tradeable} 标签,
	 *       原版图书管理员 Level 1~4 自动出售(价格由附魔 min_cost/max_cost 决定)</li>
	 *   <li><b>自定义交易</b>:6 条手工定价的村民交易,覆盖武器匠/制图师/皮匠/牧师/盔甲匠
	 *       多个职业,演示 VillagerTradeBuilder 的完整用法</li>
	 * </ul>
	 *
	 * <p>伪装系列由盔甲匠 Level 2 出售附魔铁头盔(随机4选1)。
	 * 静默契约为<b>古城宝藏附魔</b>,仅从远古城市战利品获取,不开放村民交易与附魔台。</p>
	 */
	@Override
	public void onRegisterVillagerTrades(VillagerTradeRegistrar registrar) {
		// 示例村民交易已注释，保留代码作为参考
		// registrar.registerTradeableEnchantments(
		// 	TradeableEnchantmentsBuilder.create()
		// 		.addEnchantments(
		// 			SLIME_SLAYER_ID,
		// 			EXECUTIONER_ID,
		// 			RETRIBUTION_ID,
		// 			HEARTBURN_ID,
		// 			FROST_ID,
		// 			ECHO_MARK_ID,
		// 			AUTO_SMELT_ID
		// 		));
		//
		// registrar.registerTrade(VillagerTradeBuilder
		// 	.create(MOD_ID, "weaponsmith/3/heartburn_sword")
		// 	.profession(VillagerTrades.WEAPONSMITH)
		// 	.level(VillagerTrades.LEVEL_3)
		// 	.asItem(Items.DIAMOND_SWORD)
		// 	.withEnchantments(HEARTBURN_ID)
		// 	.emeralds(15)
		// 	.noAdditionalItem()
		// 	.maxUses(3)
		// 	.xp(10)
		// 	.priceMultiplier(0.2F));
		//
		// registrar.registerTrade(VillagerTradeBuilder
		// 	.create(MOD_ID, "weaponsmith/4/executioner_sword")
		// 	.profession(VillagerTrades.WEAPONSMITH)
		// 	.level(VillagerTrades.LEVEL_4)
		// 	.asItem(Items.DIAMOND_SWORD)
		// 	.withEnchantments(EXECUTIONER_ID)
		// 	.emeralds(20)
		// 	.noAdditionalItem()
		// 	.maxUses(2)
		// 	.xp(15)
		// 	.priceMultiplier(0.2F));
		//
		// registrar.registerTrade(VillagerTradeBuilder
		// 	.create(MOD_ID, "cartographer/3/echo_mark_book")
		// 	.profession(VillagerTrades.CARTOGRAPHER)
		// 	.level(VillagerTrades.LEVEL_3)
		// 	.asBook()
		// 	.withEnchantments(ECHO_MARK_ID)
		// 	.emeralds(15)
		// 	.maxUses(5)
		// 	.xp(8)
		// 	.priceMultiplier(0.2F));
		//
		// registrar.registerTrade(VillagerTradeBuilder
		// 	.create(MOD_ID, "leatherworker/2/auto_smelt_book")
		// 	.profession(VillagerTrades.LEATHERWORKER)
		// 	.level(VillagerTrades.LEVEL_2)
		// 	.asBook()
		// 	.withEnchantments(AUTO_SMELT_ID)
		// 	.emeralds(8)
		// 	.maxUses(8)
		// 	.xp(5)
		// 	.priceMultiplier(0.2F));
		//
		// registrar.registerTrade(VillagerTradeBuilder
		// 	.create(MOD_ID, "cleric/4/frost_book")
		// 	.profession(VillagerTrades.CLERIC)
		// 	.level(VillagerTrades.LEVEL_4)
		// 	.asBook()
		// 	.withEnchantments(FROST_ID)
		// 	.emeralds(18)
		// 	.maxUses(5)
		// 	.xp(12)
		// 	.priceMultiplier(0.2F));
		//
		// registrar.registerTrade(VillagerTradeBuilder
		// 	.create(MOD_ID, "armorer/2/disguise_helmet")
		// 	.profession(VillagerTrades.ARMORER)
		// 	.level(VillagerTrades.LEVEL_2)
		// 	.asItem(Items.IRON_HELMET)
		// 	.withEnchantments(UNDEAD_DISGUISE_ID, AQUATIC_DISGUISE_ID,
		// 		ILLAGER_DISGUISE_ID, ARTHROPOD_DISGUISE_ID)
		// 	.emeralds(15)
		// 	.noAdditionalItem()
		// 	.maxUses(4)
		// 	.xp(6)
		// 	.priceMultiplier(0.2F));
	}

	@Override
	public void onRegisterEventCallbacks(EnchantmentEventRegistrar registrar,
										 HolderLookup.Provider registries) {
		// 示例事件回调已注释，保留代码作为参考
		// EnchantLibEvents.enableLivingEntityTick();
		//
		// DisguiseEnchantments.registerCallbacks(registrar, registries);
		// AutoSmeltEnchantment.registerCallbacks(registrar, registries);
		// SlimeSlayerEnchantment.registerCallbacks(registrar, registries);
		// ExecutionerEnchantment.registerCallbacks(registrar, registries);
		// RetributionEnchantment.registerCallbacks(registrar, registries);
		// SilentPactEnchantment.registerCallbacks(registrar, registries);
		// HeartburnEnchantment.registerCallbacks(registrar, registries);
		// EchoMarkEnchantment.registerCallbacks(registrar, registries);
		// FrostEnchantment.registerCallbacks(registrar, registries);
	}

	/**
	 * 通过注册表解析附魔 Holder。
	 *
	 * @param registries 注册表访问器
	 * @param id         附魔 ID 字符串
	 * @return 附魔 Holder
	 * @throws IllegalStateException 若附魔未注册
	 */
	public static Holder<Enchantment> resolveEnchantment(HolderLookup.Provider registries, String id) {
		Identifier enchantId = Identifier.parse(id);
		ResourceKey<Enchantment> resourceKey = ResourceKey.create(Registries.ENCHANTMENT, enchantId);
		return registries.lookupOrThrow(Registries.ENCHANTMENT)
			.get(resourceKey)
			.orElseThrow(() -> new IllegalStateException("EnchantLib ExampleMod: 附魔未注册: " + id));
	}
}
