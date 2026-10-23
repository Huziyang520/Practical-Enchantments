package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * 掉落不死亡附魔 - 任意装备 Ⅲ 级（娱乐，仅创造/指令获取）。
 */
public final class UndyingDropEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":undying_drop";

	private UndyingDropEnchantment() {
	}


	/**
	 * 尝试触发掉落不死亡。
	 *
	 * @return true 表示已免死（Mixin 应取消死亡）
	 */
	public static boolean trySave(ServerPlayer player, DamageSource source) {
		EquipmentSlot enchantedSlot = DeathSaveSupport.findArmorWith(player, "undying_drop");
		if (enchantedSlot == null) {
			return false;
		}
		// 虚空 / /kill 等绕过无敌的伤害不救
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}

		// 先读等级（摧毁前），决定黑暗时长
		int level = DeathSaveSupport.getLevel(player.getItemBySlot(enchantedSlot), "undying_drop");
		int darknessTicks = (20 - 4 * (level - 1)) * 20;

		// 摧毁附魔装备 + 掉落全身物品
		DeathSaveSupport.dropEverything(player, enchantedSlot);

		// 免死 + 黑暗
		DeathSaveSupport.performResurrection(player, darknessTicks);
		return true;
	}
}