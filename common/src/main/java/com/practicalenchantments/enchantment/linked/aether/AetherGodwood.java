package com.practicalenchantments.enchantment.linked.aether;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.huziyang520.merlinlib.content.EnchantmentJson;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.IncinerateEnchantment;
import com.practicalenchantments.enchantment.KillMarks;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 神木气息：击杀掉落翻倍，破坏方块时有机会多掉 1–2 个神能晶片。
 */
public final class AetherGodwood {

	public static final String PATH = "aether_godwood";
	public static final String ID = PracticalEnchantments.MOD_ID + ":" + PATH;

	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);

	/** 神能晶片在该模组里出现过的 id，按可能性从高到低。 */
	private static final List<String> SHARD_CANDIDATES = List.of(
			"aether_ii:ambrosium_shard",
			"aether_ii:ambrosium",
			"aether:ambrosium_shard",
			"aether:ambrosium");

	/** 破坏方块时额外掉落晶片的概率。 */
	private static final float SHARD_CHANCE = 0.25F;

	/** 天境战利品箱候选表，命名空间用实际加载的那个模组。 */
	private static final String[] LOOT_PATHS = {
			"chests/dungeon",
			"chests/bronze_dungeon",
			"chests/silver_dungeon",
	};

	/** 解析到的晶片；第一次破坏方块时才解析，模组版本里没有这个物品时为 null。 */
	private static Holder<Item> shard;
	/** 解析是否已经做过，避免每次破坏方块都查一遍注册表。 */
	private static boolean shardResolved;
	/** 晶片要在哪个命名空间里找，登记回调时记下。 */
	private static String shardNamespace = "aether_ii";
	/** 首次命中破坏方块回调时打印一行诊断日志，只打一次。 */
	private static boolean announced;

	private AetherGodwood() {
	}

	public static void registerContent(String namespace) {
		MerlinApi.enchantments().register(Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, PATH))
				.maxLevel(1)
				.weight(1)
				.anvilCost(4)
				.slots("mainhand")
				.supportedItems("#practical_enchantments:godwood_items")
				.cost(20, 0, 50, 0)
				.acquisition(Acquisition.TREASURE_ONLY)
				// 与焚灭、抢夺互斥：同一个掉落类附魔在身上只该有一个
				.exclusiveSet(EnchantmentJson.tag(IncinerateEnchantment.GROUP_TAG))
				.submit();
		for (String path : LOOT_PATHS) {
			MerlinApi.loot().register(LootInjectionBuilder.create()
					.toTables(namespace + ":" + path)
					.asBook()
					.withEnchantments(ID)
					.chance(0.12F)
					.weight(1)
					.quality(2)
					.allowModdedTables());
		}
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries,
			String namespace) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_KILL, AetherGodwood::onKill);
		registrar.register(holder, BuiltInEvents.MODIFY_BLOCK_DROPS, AetherGodwood::onBlockDrops);
		// 晶片解析推迟到第一次破坏方块：回调登记早于任何一次掉落，物品并不在这一步依赖数据包注册表。
		shardNamespace = namespace;
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (event.victim() instanceof ServerPlayer) {
			return;
		}
		KillMarks.markDoubled(event.victim());
	}

	private static void onBlockDrops(BuiltInEvents.ModifyBlockDropsEvent event, EnchantmentContext ctx) {
		if (!announced) {
			announced = true;
			// 一次进服只报一行：这条日志把「事件没派发」和「派发了但工具没附魔」分开。
			LOGGER.info("[PracticalEnchantments] 神木气息：破坏方块回调已触发（等级 {}，工具 {}）",
					ctx.level(), ctx.itemStack().getItem());
		}
		Holder<Item> item = shard();
		if (item == null || event.player().getRandom().nextFloat() >= SHARD_CHANCE) {
			return;
		}
		int count = 1 + event.player().getRandom().nextInt(2);
		event.drops().add(new ItemStack(item, count));
		LOGGER.debug("[PracticalEnchantments] 神木气息：破坏 {} 时额外掉落 {} 个 {}",
				event.blockState().getBlock(), count, item.value());
	}

	/**
	 * 晶片 Holder，第一次需要时解析一次。
	 *
	 * <p>物品属于静态注册表，用 {@link BuiltInRegistries} 查，不依赖数据包注册表访问：那样一来解析结果与
	 * 数据包重载无关，也不会因为注册早于注册表就绪而永久留空。
	 *
	 * @return 晶片 Holder，这个版本的天境里没有该物品时为 {@code null}
	 */
	private static Holder<Item> shard() {
		if (!shardResolved) {
			shardResolved = true;
			shard = resolveShard(shardNamespace);
		}
		return shard;
	}

	private static Holder<Item> resolveShard(String namespace) {
		for (String candidate : SHARD_CANDIDATES) {
			if (!candidate.startsWith(namespace + ":")) {
				continue;
			}
			Holder.Reference<Item> found = BuiltInRegistries.ITEM.get(Identifier.parse(candidate)).orElse(null);
			if (found != null) {
				LOGGER.info("[PracticalEnchantments] 神木气息：额外掉落使用 {}", candidate);
				return found;
			}
		}
		LOGGER.warn("[PracticalEnchantments] 神木气息：{} 里没有找到神能晶片（候选 {}），额外掉落已跳过",
				namespace, SHARD_CANDIDATES);
		return null;
	}
}
