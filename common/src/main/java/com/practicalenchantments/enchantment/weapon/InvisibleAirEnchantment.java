// 风王结界：伤害翻倍在 WeaponAttackMixin 里结算，这里只提供附魔 holder。

package com.practicalenchantments.enchantment.weapon;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;

public final class InvisibleAirEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":invisible_air";

	private InvisibleAirEnchantment() {
	}

	public static boolean hasInMainHand(Player player) {
		for (var entry : player.getMainHandItem().getEnchantments().entrySet()) {
			var key = entry.getKey().unwrapKey();
			if (key.isPresent() && key.get().identifier().toString().equals(ID) && entry.getIntValue() > 0) {
				return true;
			}
		}
		return false;
	}

	public static Holder<Enchantment> holder(RegistryAccess access) {
		return access.lookupOrThrow(Registries.ENCHANTMENT)
				.get(ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse(ID)))
				.orElse(null);
	}
}
