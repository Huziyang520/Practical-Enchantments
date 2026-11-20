// PE 自有内容：有瑕疵的黑曜石、四个护身符与独立创造页签。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.platform.Services;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PeContent {
	private static final String NAMESPACE = "practical_enchantments";
	public static final Identifier FLAWED_OBSIDIAN = id("flawed_obsidian");
	public static final Identifier EVOLUTION_TABLE = id("evolution_table");
	public static final Identifier BOOK_BAG_LEATHER = id("book_bag_leather");
	public static final Identifier BOOK_BAG_IRON = id("book_bag_iron");
	public static final Identifier BOOK_BAG_GOLD = id("book_bag_gold");
	public static final Identifier BOOK_BAG_DIAMOND = id("book_bag_diamond");
	public static final Identifier BOOK_BAG_NETHERITE = id("book_bag_netherite");
	private static final Identifier[] BOOK_BAGS = {
			BOOK_BAG_LEATHER, BOOK_BAG_IRON, BOOK_BAG_GOLD, BOOK_BAG_DIAMOND, BOOK_BAG_NETHERITE};
	private static final int[] BOOK_BAG_CAPACITY = {27, 54, 81, 135, 216};
	private static final Rarity[] BOOK_BAG_RARITY = {
			Rarity.COMMON, Rarity.UNCOMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC};
	private static final Rarity[] AMULET_RARITY = {Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC};
	private static final int[] BOOK_BAG_LOOT_BOOKS = {2, 3, 4, 5, 6};
	public static final Identifier IRON_AMULET = id("iron_amulet");
	public static final Identifier GOLDEN_AMULET = id("golden_amulet");
	public static final Identifier DIAMOND_AMULET = id("diamond_amulet");
	public static final Identifier NETHERITE_AMULET = id("netherite_amulet");
	public static final Identifier TAB = id("main");
	private static final List<Identifier> AMULETS =
			List.of(IRON_AMULET, GOLDEN_AMULET, DIAMOND_AMULET, NETHERITE_AMULET);
	private static boolean registered;
	private static Block flawedObsidian;

	private PeContent() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		Services.REGISTRATIONS.register(BuiltInRegistries.BLOCK, FLAWED_OBSIDIAN, PeContent::createBlock);
		Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, FLAWED_OBSIDIAN, PeContent::createBlockItem);
		Services.REGISTRATIONS.register(BuiltInRegistries.BLOCK, EVOLUTION_TABLE, PeContent::createEvolutionTable);
		Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, EVOLUTION_TABLE,
				() -> createBlockItem(EVOLUTION_TABLE));
		Services.REGISTRATIONS.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, EVOLUTION_TABLE,
				PeContent::createEvolutionTableEntity);
		for (int index = 0; index < BOOK_BAGS.length; index++) {
			Identifier bag = BOOK_BAGS[index];
			int capacity = BOOK_BAG_CAPACITY[index];
			int lootBooks = BOOK_BAG_LOOT_BOOKS[index];
			Rarity rarity = BOOK_BAG_RARITY[index];
			Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, bag,
					() -> new BookBagItem(itemProperties(bag).stacksTo(1).rarity(rarity), capacity, lootBooks));
		}
		for (int index = 0; index < AMULETS.size(); index++) {
			Identifier amulet = AMULETS.get(index);
			Rarity rarity = AMULET_RARITY[index];
			Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, amulet,
					() -> new Item(itemProperties(amulet).stacksTo(1).rarity(rarity)));
		}
		Services.REGISTRATIONS.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB, PeContent::createTab);
		PeRecipes.register();
	}

	public static Block block(Identifier id) {
		Block created = flawedObsidian;
		if (created != null && FLAWED_OBSIDIAN.equals(id)) {
			return created;
		}
		return BuiltInRegistries.BLOCK.getValue(id);
	}

	public static Item item(Identifier id) {
		return BuiltInRegistries.ITEM.getValue(id);
	}

	public static boolean isAmulet(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		for (Identifier amulet : AMULETS) {
			if (stack.is(item(amulet))) {
				return true;
			}
		}
		return false;
	}

	public static List<Identifier> amulets() {
		return AMULETS;
	}

	private static Block createBlock() {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, FLAWED_OBSIDIAN);
		Block created = new FlawedObsidianBlock(BlockBehaviour.Properties.of()
				.setId(key)
				.noLootTable()
				.strength(0.5F));
		flawedObsidian = created;
		return created;
	}

	private static Item createBlockItem() {
		Block block = block(FLAWED_OBSIDIAN);
		BlockItem item = new BlockItem(block, blockItemProperties(FLAWED_OBSIDIAN));
		item.registerBlocks(Item.BY_BLOCK, item);
		return item;
	}

	private static Block createEvolutionTable() {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, EVOLUTION_TABLE);
		return new EvolutionTableBlock(BlockBehaviour.Properties.of()
				.setId(key)
				.strength(2.5F)
				.requiresCorrectToolForDrops()
				.noOcclusion()
				.lightLevel(state -> state.getValue(EvolutionTableBlock.WORKING) ? 7 : 0));
	}

	private static Item createBlockItem(Identifier id) {
		Block block = block(id);
		BlockItem item = new BlockItem(block, blockItemProperties(id));
		item.registerBlocks(Item.BY_BLOCK, item);
		return item;
	}

	private static BlockEntityType<EvolutionTableBlockEntity> createEvolutionTableEntity() {
		EvolutionTableBlockEntity.TYPE = createBlockEntityType(Set.of(block(EVOLUTION_TABLE)));
		return EvolutionTableBlockEntity.TYPE;
	}

	// 26.1.2 的 BlockEntityType 构造器仍是 private，且 BlockEntitySupplier 也不可见：
	// 用反射走一次构造，工厂用 Proxy 实现，避免为了一个方块实体去开 access transformer。
	@SuppressWarnings("unchecked")
	private static BlockEntityType<EvolutionTableBlockEntity> createBlockEntityType(Set<Block> blocks) {
		try {
			java.lang.reflect.Constructor<?> target = null;
			for (java.lang.reflect.Constructor<?> candidate : BlockEntityType.class.getDeclaredConstructors()) {
				if (candidate.getParameterCount() == 2) {
					target = candidate;
					break;
				}
			}
			if (target == null) {
				throw new IllegalStateException("no BlockEntityType constructor");
			}
			target.setAccessible(true);
			Class<?> supplierType = target.getParameterTypes()[0];
			Object supplier = java.lang.reflect.Proxy.newProxyInstance(
					BlockEntityType.class.getClassLoader(), new Class<?>[]{supplierType},
					(proxy, method, args) -> "create".equals(method.getName())
							? new EvolutionTableBlockEntity((net.minecraft.core.BlockPos) args[0],
									(net.minecraft.world.level.block.state.BlockState) args[1])
							: null);
			return (BlockEntityType<EvolutionTableBlockEntity>) target.newInstance(supplier, blocks);
		} catch (ReflectiveOperationException failure) {
			throw new IllegalStateException("failed to build the evolution table block entity type", failure);
		}
	}

	private static Item.Properties itemProperties(Identifier id) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
	}

	private static Item.Properties blockItemProperties(Identifier id) {
		return itemProperties(id).useBlockDescriptionPrefix();
	}

	public static ResourceKey<CreativeModeTab> tabKey() {
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB);
	}

	public static List<ItemStack> tabEntries() {
		List<ItemStack> stacks = new ArrayList<>();
		stacks.add(new ItemStack(item(FLAWED_OBSIDIAN)));
		stacks.add(new ItemStack(item(EVOLUTION_TABLE)));
		for (Identifier bag : BOOK_BAGS) {
			stacks.add(new ItemStack(item(bag)));
		}
		for (Identifier amulet : AMULETS) {
			stacks.add(new ItemStack(item(amulet)));
		}
		return stacks;
	}

	private static CreativeModeTab createTab() {
		return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
				.title(Component.translatable("itemGroup.practical_enchantments.main"))
				.icon(() -> new ItemStack(item(IRON_AMULET)))
				.build();
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(NAMESPACE, path);
	}
}
