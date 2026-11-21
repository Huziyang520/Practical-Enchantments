// 附魔书袋：只收附魔书，容量随档位。内容存在物品 NBT 里，空袋不留标签。

package com.practicalenchantments.content;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;

public final class BookBagStorage {
	public static final String LOOT_MARKER = "practical_enchantments:loot_bag";
	public static final String TAG_ITEMS = "Inventory";

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
		NonNullList<ItemStack> items = NonNullList.withSize(capacity, ItemStack.EMPTY);
		CompoundTag tag = bag.getTag();
		if (tag == null) {
			return items;
		}
		ListTag list = tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND);
		for (int index = 0; index < list.size() && index < items.size(); index++) {
			items.set(index, ItemStack.of(list.getCompound(index)));
		}
		return items;
	}

	public static void write(ItemStack bag, List<ItemStack> items) {
		ListTag list = new ListTag();
		boolean empty = true;
		for (ItemStack stack : items) {
			if (stack.isEmpty()) {
				list.add(new CompoundTag());
				continue;
			}
			empty = false;
			list.add(stack.save(new CompoundTag()));
		}
		if (empty) {
			bag.removeTagKey(TAG_ITEMS);
			return;
		}
		bag.getOrCreateTag().put(TAG_ITEMS, list);
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
		CompoundTag tag = bag.getTag();
		if (tag == null || !tag.getBoolean(LOOT_MARKER)) {
			return false;
		}
		tag.remove(LOOT_MARKER);
		return true;
	}

	public static void prefill(ItemStack bag, int maxBooks, RandomSource random) {
		if (maxBooks <= 0) {
			return;
		}
		List<Enchantment> pool = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues());
		if (pool.isEmpty()) {
			return;
		}
		int count = random.nextInt(maxBooks + 1);
		if (count <= 0) {
			return;
		}
		NonNullList<ItemStack> items = read(bag);
		for (int index = 0; index < count && index < items.size(); index++) {
			Enchantment enchantment = pool.get(random.nextInt(pool.size()));
			int level = random.nextInt(Math.max(1, enchantment.getMaxLevel())) + 1;
			items.set(index, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level)));
		}
		write(bag, items);
	}
}
