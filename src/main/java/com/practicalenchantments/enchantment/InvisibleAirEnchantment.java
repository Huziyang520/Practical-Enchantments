// 风王结界：伤害翻倍在 WeaponAttackMixin 里结算，这里只提供附魔 holder。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;

public final class InvisibleAirEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":invisible_air";
	private static Holder<Enchantment> holder;

	private InvisibleAirEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		holder = PracticalEnchantments.resolveEnchantment(registries, ID);
	}

	public static boolean hasInMainHand(Player player) {
		for (var entry : player.getMainHandItem().getEnchantments().entrySet()) {
			var key = entry.getKey().unwrapKey();
			if (key.isPresent() && key.get().location().toString().equals(ID) && entry.getIntValue() > 0) {
				return true;
			}
		}
		return false;
	}

	public static Holder<Enchantment> holder(HolderLookup.Provider registries) {
		Holder<Enchantment> cached = holder;
		if (cached != null) {
			return cached;
		}
		return registries.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
				.get(ResourceKey.create(net.minecraft.core.registries.Registries.ENCHANTMENT,
						ResourceLocation.parse(ID)))
				.orElse(null);
	}
}
