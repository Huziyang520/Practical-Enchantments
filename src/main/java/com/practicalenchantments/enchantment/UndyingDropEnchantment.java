// 掉落不死亡附魔 - 任意装备 Ⅲ 级（娱乐，仅创造/指令获取）。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;

public final class UndyingDropEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":undying_drop";

	private UndyingDropEnchantment() {
	}

	public static boolean trySave(ServerPlayer player, DamageSource source) {
		EquipmentSlot enchantedSlot = DeathSaveSupport.findArmorWith(player, "undying_drop");
		if (enchantedSlot == null) {
			return false;
		}

		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}

		int level = DeathSaveSupport.getLevel(player.getItemBySlot(enchantedSlot), "undying_drop");
		int darknessTicks = (20 - 4 * (level - 1)) * 20;
		DeathSaveSupport.dropEverything(player, enchantedSlot);
		DeathSaveSupport.performResurrection(player, darknessTicks);
		return true;
	}
}
