// 附魔书袋：只收附魔书，容量随档位。内容存在 CONTAINER 组件里，空袋不写组件。

package com.practicalenchantments.content;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;

public final class BookBagStorage {
	public static final String LOOT_MARKER = "practical_enchantments:loot_bag";

	private BookBagStorage() {
	}

	public static boolean isBag(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof BookBagItem;
	}

	public static int capacity(ItemStack bag) {
		return bag.getItem() instanceof BookBagItem item ? item.capacity() : 0;
	}

	public static NonNullList<ItemStack> read(ItemStack bag) {
		int capacity = Math.max(1, capacity(bag));
		ItemContainerContents contents = bag.get(DataComponents.CONTAINER);
		int size = capacity;
		NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
		if (contents != null) {
			contents.copyInto(items);
		}
		return items;
	}

	public static void write(ItemStack bag, List<ItemStack> items) {
		boolean empty = true;
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				empty = false;
				break;
			}
		}
		if (empty) {
			bag.remove(DataComponents.CONTAINER);
			return;
		}
		bag.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(new ArrayList<>(items)));
	}

	public static int usedSlots(ItemStack bag) {
		int used = 0;
		for (ItemStack stack : read(bag)) {
			if (!stack.isEmpty()) {
				used++;
			}
		}
		return used;
	}

	public static boolean mayStore(ItemStack stack) {
		return stack.is(Items.ENCHANTED_BOOK);
	}

	public static boolean consumeLootMarker(ItemStack bag) {
		CustomData data = bag.get(DataComponents.CUSTOM_DATA);
		if (data == null || !data.copyTag().getBooleanOr(LOOT_MARKER, false)) {
			return false;
		}
		CustomData.update(DataComponents.CUSTOM_DATA, bag, tag -> tag.remove(LOOT_MARKER));
		return true;
	}

	public static void prefill(ItemStack bag, int maxBooks, net.minecraft.core.HolderLookup.Provider registries,
			net.minecraft.util.RandomSource random) {
		if (maxBooks <= 0) {
			return;
		}
		java.util.List<net.minecraft.core.Holder.Reference<net.minecraft.world.item.enchantment.Enchantment>> pool =
				registries.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).listElements().toList();
		if (pool.isEmpty()) {
			return;
		}
		int count = random.nextInt(maxBooks + 1);
		if (count <= 0) {
			return;
		}
		NonNullList<ItemStack> items = read(bag);
		for (int index = 0; index < count && index < items.size(); index++) {
			ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
			net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment =
					pool.get(random.nextInt(pool.size()));
			int level = random.nextInt(Math.max(1, enchantment.value().getMaxLevel())) + 1;
			net.minecraft.world.item.enchantment.EnchantmentHelper.updateEnchantments(book,
					mutable -> mutable.set(enchantment, level));
			items.set(index, book);
		}
		write(bag, items);
	}
}
