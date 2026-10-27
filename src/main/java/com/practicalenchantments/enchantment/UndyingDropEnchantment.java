// 掉落不死亡附魔 - 任意装备 Ⅲ 级（娱乐，仅创造/指令获取）。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public final class UndyingDropEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":undying_drop";
	private static Enchantment ENCHANTMENT;

	private UndyingDropEnchantment() {
	}

	public static boolean trySave(ServerPlayer player, DamageSource source) {
		Enchantment enchantment = enchantment();
		if (enchantment == null) {
			return false;
		}
		int level = AmuletSupport.wornLevel(player, enchantment);
		if (level <= 0) {
			return false;
		}

		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}

		EquipmentSlot destroyedSlot = DeathSaveSupport.findArmorWith(player, "undying_drop");
		if (destroyedSlot == null
				&& DeathSaveSupport.getLevel(player.getItemBySlot(EquipmentSlot.OFFHAND), "undying_drop") > 0) {
			destroyedSlot = EquipmentSlot.OFFHAND;
		}
		int darknessTicks = (20 - 4 * (level - 1)) * 20;
		DeathSaveSupport.dropEverything(player, destroyedSlot);
		DeathSaveSupport.performResurrection(player, darknessTicks);
		return true;
	}

	private static Enchantment enchantment() {
		Enchantment cached = ENCHANTMENT;
		if (cached == null) {
			cached = PracticalEnchantments.resolveEnchantment(ID);
			ENCHANTMENT = cached;
		}
		return cached;
	}
}
