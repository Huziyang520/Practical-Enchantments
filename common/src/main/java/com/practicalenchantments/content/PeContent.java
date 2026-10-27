// PE 自有内容：有瑕疵的黑曜石、四个护身符与独立创造页签。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.platform.Services;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PeContent {
	private static final String NAMESPACE = "practical_enchantments";
	public static final Identifier FLAWED_OBSIDIAN = id("flawed_obsidian");
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
		for (Identifier amulet : AMULETS) {
			Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, amulet, () -> new Item(itemProperties(amulet).stacksTo(1)));
		}
		Services.REGISTRATIONS.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB, PeContent::createTab);
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
		BlockItem item = new BlockItem(block, itemProperties(FLAWED_OBSIDIAN));
		item.registerBlocks(Item.BY_BLOCK, item);
		return item;
	}

	private static Item.Properties itemProperties(Identifier id) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
	}

	public static ResourceKey<CreativeModeTab> tabKey() {
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB);
	}

	public static List<ItemStack> tabEntries() {
		List<ItemStack> stacks = new ArrayList<>();
		stacks.add(new ItemStack(item(FLAWED_OBSIDIAN)));
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
