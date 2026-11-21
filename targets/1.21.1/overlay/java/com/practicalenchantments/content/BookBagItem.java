// 附魔书袋物品：主手右键打开，副手打开会读到主手，故只接受主手。

package com.practicalenchantments.content;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BookBagItem extends Item {
	private final int capacity;
	private final int maxLootBooks;

	public BookBagItem(Properties properties, int capacity, int maxLootBooks) {
		super(properties);
		this.capacity = Math.min(capacity, 256);
		this.maxLootBooks = Math.max(0, maxLootBooks);
	}

	public int capacity() {
		return this.capacity;
	}

	public int maxLootBooks() {
		return this.maxLootBooks;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}
		if (!(player.getMainHandItem().getItem() instanceof BookBagItem)) {
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}
		if (!level.isClientSide()) {
			ItemStack bag = player.getMainHandItem();
			if (BookBagStorage.consumeLootMarker(bag)) {
				BookBagStorage.prefill(bag, this.maxLootBooks, level.registryAccess(), level.getRandom());
			}
			if (PeMenus.bookBag() == null) {
				return InteractionResultHolder.pass(player.getItemInHand(hand));
			}
			player.openMenu(new SimpleMenuProvider(
					(containerId, inventory, menuPlayer) -> new BookBagMenu(containerId, inventory),
					Component.translatable("container." + PracticalEnchantments.MOD_ID + ".book_bag")));
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return BookBagStorage.usedSlots(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		int capacity = Math.max(1, this.capacity);
		return Math.min(13, Math.round(13.0F * BookBagStorage.usedSlots(stack) / capacity));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0x5A3FA0;
	}
}
