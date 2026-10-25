// 吸血附魔 - 剑/斧/矛 Ⅴ 级（附魔台可出，老手级图书管理员）。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LifeStealEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":life_steal";
	private static final float HEAL_PER_LEVEL = 0.04F;

	private LifeStealEnchantment() {
	}

	public static void onAfterDamage(LivingEntity entity, DamageSource source,
									 float amount, float blockedDamage, boolean blocked) {
		if (!(source.getEntity() instanceof ServerPlayer player) || amount <= 0.0F) {
			return;
		}
		int level = DeathSaveSupport.getLevel(player.getMainHandItem(), "life_steal");
		if (level <= 0) {
			return;
		}
		float heal = amount * HEAL_PER_LEVEL * level;
		if (heal > 0.0F) {
			player.heal(heal);
		}
	}
}
