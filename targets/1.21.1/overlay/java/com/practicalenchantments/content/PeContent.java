// PE 自有内容：有瑕疵的黑曜石、进化附魔台、五种附魔书袋、四个护身符与独立创造页签。
// 注册走 DeferredRegister，但对外暴露与主线同形的静态入口（ResourceLocation 常量 + item/block 查询）。

package com.practicalenchantments.content;

import com.practicalenchantments.PracticalEnchantments;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PeContent {
	private static final String NAMESPACE = PracticalEnchantments.MOD_ID;
	private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NAMESPACE);
	private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NAMESPACE);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
			DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NAMESPACE);
	private static final DeferredRegister<CreativeModeTab> TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NAMESPACE);

	public static final ResourceLocation FLAWED_OBSIDIAN = id("flawed_obsidian");
	public static final ResourceLocation EVOLUTION_TABLE = id("evolution_table");
	public static final ResourceLocation BOOK_BAG_LEATHER = id("book_bag_leather");
	public static final ResourceLocation BOOK_BAG_IRON = id("book_bag_iron");
	public static final ResourceLocation BOOK_BAG_GOLD = id("book_bag_gold");
	public static final ResourceLocation BOOK_BAG_DIAMOND = id("book_bag_diamond");
	public static final ResourceLocation BOOK_BAG_NETHERITE = id("book_bag_netherite");
	public static final ResourceLocation IRON_AMULET = id("iron_amulet");
	public static final ResourceLocation GOLDEN_AMULET = id("golden_amulet");
	public static final ResourceLocation DIAMOND_AMULET = id("diamond_amulet");
	public static final ResourceLocation NETHERITE_AMULET = id("netherite_amulet");
	public static final ResourceLocation TAB = id("main");

	private static final ResourceLocation[] BOOK_BAGS = {
			BOOK_BAG_LEATHER, BOOK_BAG_IRON, BOOK_BAG_GOLD, BOOK_BAG_DIAMOND, BOOK_BAG_NETHERITE};
	private static final int[] BOOK_BAG_CAPACITY = {27, 54, 81, 135, 216};
	private static final int[] BOOK_BAG_LOOT_BOOKS = {2, 3, 4, 5, 6};
	private static final Rarity[] BOOK_BAG_RARITY = {
			Rarity.COMMON, Rarity.UNCOMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC};
	private static final Rarity[] AMULET_RARITY = {Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC};
	private static final List<ResourceLocation> AMULETS =
			List.of(IRON_AMULET, GOLDEN_AMULET, DIAMOND_AMULET, NETHERITE_AMULET);

	public static final DeferredBlock<FlawedObsidianBlock> FLAWED_OBSIDIAN_BLOCK = BLOCKS.register("flawed_obsidian",
			() -> new FlawedObsidianBlock(BlockBehaviour.Properties.of()
					.noLootTable()
					.strength(0.5F)));
	public static final DeferredBlock<EvolutionTableBlock> EVOLUTION_TABLE_BLOCK = BLOCKS.register("evolution_table",
			() -> new EvolutionTableBlock(BlockBehaviour.Properties.of()
					.strength(2.5F)
					.requiresCorrectToolForDrops()
					.noOcclusion()
					.lightLevel(state -> state.getValue(EvolutionTableBlock.WORKING) ? 7 : 0)));
	public static final DeferredItem<BlockItem> FLAWED_OBSIDIAN_ITEM =
			ITEMS.registerSimpleBlockItem("flawed_obsidian", FLAWED_OBSIDIAN_BLOCK);
	public static final DeferredItem<BlockItem> EVOLUTION_TABLE_ITEM =
			ITEMS.registerSimpleBlockItem("evolution_table", EVOLUTION_TABLE_BLOCK);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EvolutionTableBlockEntity>> EVOLUTION_TABLE_ENTITY =
			BLOCK_ENTITIES.register("evolution_table", () -> {
				BlockEntityType<EvolutionTableBlockEntity> type = BlockEntityType.Builder
						.of(EvolutionTableBlockEntity::new, EVOLUTION_TABLE_BLOCK.get())
						.build(null);
				EvolutionTableBlockEntity.TYPE = type;
				return type;
			});
	public static final List<DeferredItem<BookBagItem>> BAGS = registerBags();
	public static final DeferredItem<Item> IRON_AMULET_ITEM = amulet("iron_amulet", 0);
	public static final DeferredItem<Item> GOLDEN_AMULET_ITEM = amulet("golden_amulet", 1);
	public static final DeferredItem<Item> DIAMOND_AMULET_ITEM = amulet("diamond_amulet", 2);
	public static final DeferredItem<Item> NETHERITE_AMULET_ITEM = amulet("netherite_amulet", 3);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
			TABS.register("main", PeContent::createTab);

	private PeContent() {
	}

	/** 1.21.1 的注册走 DeferredRegister，公共代码里的这个调用保持空实现。 */
	public static void register() {
	}

	public static void register(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
		TABS.register(modEventBus);
	}

	public static Block block(ResourceLocation id) {
		return BuiltInRegistries.BLOCK.get(id);
	}

	public static Item item(ResourceLocation id) {
		return BuiltInRegistries.ITEM.get(id);
	}

	public static boolean isAmulet(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		for (ResourceLocation amulet : AMULETS) {
			if (stack.is(item(amulet))) {
				return true;
			}
		}
		return false;
	}

	public static List<ResourceLocation> amulets() {
		return AMULETS;
	}

	public static ResourceKey<CreativeModeTab> tabKey() {
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB);
	}

	public static List<ItemStack> tabEntries() {
		List<ItemStack> stacks = new ArrayList<>();
		stacks.add(new ItemStack(item(FLAWED_OBSIDIAN)));
		stacks.add(new ItemStack(item(EVOLUTION_TABLE)));
		for (ResourceLocation bag : BOOK_BAGS) {
			stacks.add(new ItemStack(item(bag)));
		}
		for (ResourceLocation amulet : AMULETS) {
			stacks.add(new ItemStack(item(amulet)));
		}
		return stacks;
	}

	private static List<DeferredItem<BookBagItem>> registerBags() {
		List<DeferredItem<BookBagItem>> registered = new ArrayList<>();
		for (int index = 0; index < BOOK_BAGS.length; index++) {
			String path = BOOK_BAGS[index].getPath();
			int capacity = BOOK_BAG_CAPACITY[index];
			int lootBooks = BOOK_BAG_LOOT_BOOKS[index];
			Rarity rarity = BOOK_BAG_RARITY[index];
			registered.add(ITEMS.register(path,
					() -> new BookBagItem(new Item.Properties().stacksTo(1).rarity(rarity), capacity, lootBooks)));
		}
		return List.copyOf(registered);
	}

	private static DeferredItem<Item> amulet(String name, int tier) {
		Rarity rarity = AMULET_RARITY[tier];
		return ITEMS.register(name, () -> new Item(new Item.Properties().stacksTo(1).rarity(rarity)));
	}

	private static CreativeModeTab createTab() {
		return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
				.title(Component.translatable("itemGroup.practical_enchantments.main"))
				.icon(() -> new ItemStack(IRON_AMULET_ITEM.get()))
				.displayItems((params, output) -> {
					output.accept(FLAWED_OBSIDIAN_ITEM.get());
					output.accept(EVOLUTION_TABLE_ITEM.get());
					for (DeferredItem<BookBagItem> bag : BAGS) {
						output.accept(bag.get());
					}
					output.accept(IRON_AMULET_ITEM.get());
					output.accept(GOLDEN_AMULET_ITEM.get());
					output.accept(DIAMOND_AMULET_ITEM.get());
					output.accept(NETHERITE_AMULET_ITEM.get());
				})
				.build();
	}

	private static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
	}
}
