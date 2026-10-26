// 风王结界：伤害翻倍在 WeaponAttackMixin 里结算，这里只提供附魔标识与手持判定。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class InvisibleAirEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":invisible_air";

	private InvisibleAirEnchantment() {
	}

	public static boolean hasInMainHand(Player player) {
		return levelOf(player.getMainHandItem()) > 0;
	}

	public static int levelOf(ItemStack stack) {
		ResourceLocation id = new ResourceLocation(ID);
		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
			if (id.equals(BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey()))) {
				return entry.getValue() == null ? 0 : entry.getValue();
			}
		}
		return 0;
	}

	public static Enchantment enchantment() {
		return PracticalEnchantments.resolveEnchantment(ID);
	}
}
