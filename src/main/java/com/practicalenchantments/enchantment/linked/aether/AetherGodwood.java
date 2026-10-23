package com.practicalenchantments.enchantment.linked.aether;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.ExclusiveSets;
import com.practicalenchantments.enchantment.KillMarks;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

	/** 解析到的晶片，模组版本里没有这个物品时为 null。 */
	private static Item shard;

	private AetherGodwood() {
	}

	public static void registerContent(String namespace) {
		MerlinApi.enchantments().register(new ResourceLocation(PracticalEnchantments.MOD_ID, PATH))
				.maxLevel(1)
				.weight(1)
				.anvilCost(4)
				.slots("mainhand")
				.supportedItems("#practical_enchantments:godwood_items")
				.cost(20, 0, 50, 0)
				.acquisition(Acquisition.TREASURE_ONLY)
				// 与焚灭、抢夺互斥：同一个掉落类附魔在身上只该有一个
				.exclusiveSet(ExclusiveSets.LOOTING.deepCopy())
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

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, String namespace) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
				BuiltInEvents.POST_KILL, AetherGodwood::onKill);
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
				BuiltInEvents.MODIFY_BLOCK_DROPS, AetherGodwood::onBlockDrops);
		shard = resolveShard(namespace);
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (event.victim() instanceof ServerPlayer) {
			return;
		}
		KillMarks.markDoubled(event.victim());
	}

	private static void onBlockDrops(BuiltInEvents.ModifyBlockDropsEvent event, EnchantmentContext ctx) {
		Item item = shard;
		if (item == null || event.player().getRandom().nextFloat() >= SHARD_CHANCE) {
			return;
		}
		event.drops().add(new ItemStack(item, 1 + event.player().getRandom().nextInt(2)));
	}

	private static Item resolveShard(String namespace) {
		for (String candidate : SHARD_CANDIDATES) {
			if (!candidate.startsWith(namespace + ":")) {
				continue;
			}
			Item found = BuiltInRegistries.ITEM.get(new ResourceLocation(candidate));
			if (found != Items.AIR) {
				return found;
			}
		}
		LOGGER.warn("[PracticalEnchantments] 神木气息：{} 里没有找到神能晶片（候选 {}），额外掉落已跳过",
				namespace, SHARD_CANDIDATES);
		return null;
	}
}
