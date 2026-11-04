// 按附魔 id 读物品上的等级，读的是 NBT 里的 Enchantments 列表。

package com.practicalenchantments.support;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public final class EnchantIds {
	private EnchantIds() {
	}

	public static int levelOf(ItemStack stack, String id) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		ListTag list = stack.getEnchantmentTags();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag = list.getCompound(i);
			if (id.equals(tag.getString("id"))) {
				return tag.getShort("lvl");
			}
		}
		return 0;
	}
}
