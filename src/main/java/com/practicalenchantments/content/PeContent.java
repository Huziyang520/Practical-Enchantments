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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PeContent {
	private static final String NAMESPACE = "practical_enchantments";
	public static final ResourceLocation FLAWED_OBSIDIAN = id("flawed_obsidian");
	public static final ResourceLocation IRON_AMULET = id("iron_amulet");
	public static final ResourceLocation GOLDEN_AMULET = id("golden_amulet");
	public static final ResourceLocation DIAMOND_AMULET = id("diamond_amulet");
	public static final ResourceLocation NETHERITE_AMULET = id("netherite_amulet");
	public static final ResourceLocation TAB = id("main");
	private static final List<ResourceLocation> AMULETS =
			List.of(IRON_AMULET, GOLDEN_AMULET, DIAMOND_AMULET, NETHERITE_AMULET);
	private static boolean registered;

	private PeContent() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		Services.REGISTRATIONS.register(BuiltInRegistries.BLOCK, FLAWED_OBSIDIAN, PeContent::createBlock);
		Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, FLAWED_OBSIDIAN, PeContent::createBlockItem);
		for (ResourceLocation amulet : AMULETS) {
			Services.REGISTRATIONS.register(BuiltInRegistries.ITEM, amulet, () -> new Item(new Item.Properties().stacksTo(1)));
		}
		Services.REGISTRATIONS.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB, PeContent::createTab);
	}

	public static Block block(ResourceLocation id) {
		return BuiltInRegistries.BLOCK.get(id);
	}

	public static Item item(ResourceLocation id) {
		return BuiltInRegistries.ITEM.get(id);
	}

	public static List<ResourceLocation> amulets() {
		return AMULETS;
	}

	private static Block createBlock() {
		return new FlawedObsidianBlock(BlockBehaviour.Properties.of()
				.noLootTable()
				.randomTicks()
				.strength(0.5F));
	}

	private static Item createBlockItem() {
		return new BlockItem(block(FLAWED_OBSIDIAN), new Item.Properties());
	}

	public static List<ItemStack> tabEntries() {
		List<ItemStack> stacks = new ArrayList<>();
		stacks.add(new ItemStack(item(FLAWED_OBSIDIAN)));
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
