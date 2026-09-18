package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 吸血附魔 - 剑/斧/矛 Ⅴ 级（附魔台可出，老手级图书管理员）。
 *
 * <p>命中目标后回复生命，回复量 = 目标本次<b>实际损失生命</b>（护甲/护盾减免后）
 * × 4% × 等级，最高 20%/级。</p>
 *
 * <p>实际伤害只有两加载器的 AFTER_DAMAGE 事件提供，故不走 enchantlib 事件注册，
 * 由 fabric/neoforge 入口把平台事件桥接到 {@link #onAfterDamage}
 * （与 examplemod 的 RetributionEnchantment 同款写法）。</p>
 */
public final class LifeStealEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":life_steal";

	private static final float HEAL_PER_LEVEL = 0.04F;

	private LifeStealEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("吸血")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(8)
			.maxLevel(5)
			.minCost(4, 8)
			.maxCost(24, 8)
			.anvilCost(2)
			.slots("mainhand"));
	}

	/**
	 * AFTER_DAMAGE 桥接：被打的是 target，攻击者持吸血武器时给攻击者回血。
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
