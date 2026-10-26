// PE 自有内容：有瑕疵的黑曜石、四个护身符与独立创造页签。

package com.practicalenchantments.content;

import com.practicalenchantments.PracticalEnchantments;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PeContent {
	private static final DeferredRegister.Blocks BLOCKS =
			DeferredRegister.createBlocks(PracticalEnchantments.MOD_ID);
	private static final DeferredRegister.Items ITEMS =
			DeferredRegister.createItems(PracticalEnchantments.MOD_ID);
	private static final DeferredRegister<CreativeModeTab> TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PracticalEnchantments.MOD_ID);

	public static final DeferredBlock<FlawedObsidianBlock> FLAWED_OBSIDIAN = BLOCKS.register("flawed_obsidian",
			() -> new FlawedObsidianBlock(BlockBehaviour.Properties.of()
					.noLootTable()
					.randomTicks()
					.strength(0.5F)));
	public static final DeferredItem<BlockItem> FLAWED_OBSIDIAN_ITEM =
			ITEMS.registerSimpleBlockItem("flawed_obsidian", FLAWED_OBSIDIAN);
	public static final DeferredItem<Item> IRON_AMULET = amulet("iron_amulet");
	public static final DeferredItem<Item> GOLDEN_AMULET = amulet("golden_amulet");
	public static final DeferredItem<Item> DIAMOND_AMULET = amulet("diamond_amulet");
	public static final DeferredItem<Item> NETHERITE_AMULET = amulet("netherite_amulet");
	public static final List<DeferredItem<Item>> AMULETS =
			List.of(IRON_AMULET, GOLDEN_AMULET, DIAMOND_AMULET, NETHERITE_AMULET);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", PeContent::createTab);

	private PeContent() {
	}

	public static void register(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		TABS.register(modEventBus);
	}

	private static DeferredItem<Item> amulet(String name) {
		return ITEMS.register(name, () -> new Item(new Item.Properties().stacksTo(1)));
	}

	private static CreativeModeTab createTab() {
		return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
				.title(Component.translatable("itemGroup.practical_enchantments.main"))
				.icon(() -> new ItemStack(IRON_AMULET.get()))
				.displayItems((params, output) -> {
					output.accept(FLAWED_OBSIDIAN_ITEM.get());
					for (DeferredItem<Item> amulet : AMULETS) {
						output.accept(amulet.get());
					}
				})
				.build();
	}
}
