// PE 自有内容：有瑕疵的黑曜石、四个护身符与独立创造页签。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.platform.Services;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PeContent {
	private static final String NAMESPACE = "practical_enchantments";
	public static final ResourceLocation FLAWED_OBSIDIAN = id("flawed_obsidian");
	public static final ResourceLocation EVOLUTION_TABLE = id("evolution_table");
	public static final ResourceLocation BOOK_BAG_LEATHER = id("book_bag_leather");
	public static final ResourceLocation BOOK_BAG_IRON = id("book_bag_iron");
	public static final ResourceLocation BOOK_BAG_GOLD = id("book_bag_gold");
	public static final ResourceLocation BOOK_BAG_DIAMOND = id("book_bag_diamond");
	public static final ResourceLocation BOOK_BAG_NETHERITE = id("book_bag_netherite");
	private static final ResourceLocation[] BOOK_BAGS = {
			BOOK_BAG_LEATHER, BOOK_BAG_IRON, BOOK_BAG_GOLD, BOOK_BAG_DIAMOND, BOOK_BAG_NETHERITE};
	private static final int[] BOOK_BAG_CAPACITY = {27, 54, 81, 135, 216};
	private static final Rarity[] BOOK_BAG_RARITY = {
			Rarity.COMMON, Rarity.UNCOMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC};
	private static final Rarity[] AMULET_RARITY = {Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC};
	private static final int[] BOOK_BAG_LOOT_BOOKS = {2, 3, 4, 5, 6};
	public static final ResourceLocation IRON_AMULET = id("iron_amulet");
	public static final ResourceLocation GOLDEN_AMULET = id("golden_amulet");
	public static final ResourceLocation DIAMOND_AMULET = id("diamond_amulet");
	public static final ResourceLocation NETHERITE_AMULET = id("netherite_amulet");
	public static final ResourceLocation TAB = id("main");
	private static final List<ResourceLocation> AMULETS =
			List.of(IRON_AMULET, GOLDEN_AMULET, DIAMOND_AMULET, NETHERITE_AMULET);
	private static boolean registered;
	private static Block flawedObsidian;
	private static Block evolutionTable;

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
				() -> new BlockItem(block(EVOLUTION_TABLE), new Item.Properties()));
		Services.REGISTRATIONS.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, EVOLUTION_TABLE,
				PeContent::createEvolutionTableEntity);
		for (int index = 0; index < BOOK_BAGS.length; index++) {
			ResourceLocation bag = BOOK_BAGS[index];
			int capacity = BOOK_BAG_CAPACITY[index];
			int lootBooks = BOOK_BAG_LOOT_BOOKS[index];
			Rarity rarity = BOOK_BAG_RARITY[index];
			Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, bag,
					() -> new BookBagItem(new Item.Properties().stacksTo(1).rarity(rarity), capacity, lootBooks));
		}
		for (int index = 0; index < AMULETS.size(); index++) {
			ResourceLocation amulet = AMULETS.get(index);
			Rarity rarity = AMULET_RARITY[index];
			Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, amulet,
					() -> new Item(new Item.Properties().stacksTo(1).rarity(rarity)));
		}
		Services.REGISTRATIONS.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB, PeContent::createTab);
		PeRecipes.register();
	}

	public static Block block(ResourceLocation id) {
		if (FLAWED_OBSIDIAN.equals(id) && flawedObsidian != null) {
			return flawedObsidian;
		}
		if (EVOLUTION_TABLE.equals(id) && evolutionTable != null) {
			return evolutionTable;
		}
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

	private static Block createBlock() {
		Block created = new FlawedObsidianBlock(BlockBehaviour.Properties.of()
				.noLootTable()
				.strength(0.5F));
		flawedObsidian = created;
		return created;
	}

	private static Item createBlockItem() {
		return new BlockItem(block(FLAWED_OBSIDIAN), new Item.Properties());
	}

	private static Block createEvolutionTable() {
		Block created = new EvolutionTableBlock(BlockBehaviour.Properties.of()
				.strength(2.5F)
				.requiresCorrectToolForDrops()
				.noOcclusion()
				.lightLevel(state -> state.getValue(EvolutionTableBlock.WORKING) ? 7 : 0));
		evolutionTable = created;
		return created;
	}

	private static BlockEntityType<EvolutionTableBlockEntity> createEvolutionTableEntity() {
		EvolutionTableBlockEntity.TYPE = BlockEntityType.Builder
				.of(EvolutionTableBlockEntity::new, block(EVOLUTION_TABLE))
				.build(null);
		return EvolutionTableBlockEntity.TYPE;
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

	private static CreativeModeTab createTab() {
		return CreativeModeTab.builder()
				.title(Component.translatable("itemGroup.practical_enchantments.main"))
				.icon(() -> new ItemStack(item(IRON_AMULET)))
				.displayItems((parameters, output) -> {
					for (ItemStack stack : tabEntries()) {
						output.accept(stack);
					}
				})
				.build();
	}

	private static ResourceLocation id(String path) {
		return new ResourceLocation(NAMESPACE, path);
	}
}
