package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.PracticalEnchantmentsForge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 吸血附魔 - 剑/斧/矛 Ⅴ 级（附魔台可出，老手级图书管理员）。
 */
public final class LifeStealEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":life_steal";

	private static final float HEAL_PER_LEVEL = 0.04F;

	private LifeStealEnchantment() {
	}


	/**
	 * AFTER_DAMAGE 桥接：被打的是 target，攻击者持吸血武器时给攻击者回血。
	 *
	 * <p>由 {@link PracticalEnchantmentsForge#onLivingDamage} 在护甲/魔法减免之后、扣血之前调用，
	 * 因此 {@code amount} 就是"本次实际造成的伤害"。</p>
	 *
	 * @param entity        被击者
	 * @param source        伤害来源
	 * @param amount        实际造成的伤害（护甲减免后）
	 * @param blockedDamage 被盾格挡的部分（本附魔不用）
	 * @param blocked       是否被完全格挡（本附魔不用）
	 */
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