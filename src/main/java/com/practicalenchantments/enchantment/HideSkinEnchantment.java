// 覆皮附魔 - 装备（皮革装备与护身符除外）：皮革套的部分特性，含免疫冰冻。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.PeContent;
import com.practicalenchantments.support.EnchantIds;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.Set;

public final class HideSkinEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":hide_skin";
	private static final Set<Item> LEATHER = Set.of(
			Items.LEATHER_HELMET,
			Items.LEATHER_CHESTPLATE,
			Items.LEATHER_LEGGINGS,
			Items.LEATHER_BOOTS,
			Items.LEATHER_HORSE_ARMOR);
	private static final EquipmentSlot[] ARMOR = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private HideSkinEnchantment() {
	}

	public static int level(ItemStack stack) {
		return EnchantIds.levelOf(stack, ID);
	}

	public static boolean has(ItemStack stack) {
		return level(stack) > 0;
	}

	public static boolean excluded(ItemStack stack) {
		return !stack.isEmpty() && ((Tunables.hideSkinExcludeLeather() && LEATHER.contains(stack.getItem()))
						|| PeContent.isAmulet(stack));
	}

	public static boolean wears(LivingEntity entity) {
		if (entity == null) {
			return false;
		}
		for (EquipmentSlot slot : ARMOR) {
			if (has(entity.getItemBySlot(slot))) {
				return true;
			}
		}
		return false;
	}
}
