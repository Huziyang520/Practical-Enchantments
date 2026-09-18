package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 残杀附魔 - 剑/斧/矛单级（宝藏，附魔台/交易均不出）。
 *
 * <p>按目标受伤前血量比例放大最终伤害：</p>
 * <ul>
 *   <li>当前血量 10%~35%：剑/矛 ×1.5，斧 ×2</li>
 *   <li>当前血量低于 10%：剑/矛 ×3，斧 ×4</li>
 *   <li>高于 35%：伤害不变</li>
 * </ul>
 * <p>在最终伤害上乘算，与锋利等原版伤害附魔共存。</p>
 */
public final class ExecuteEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":execute";

	private static final float LOW_HP_RATIO = 0.10F;
	private static final float MID_HP_RATIO = 0.35F;

	private ExecuteEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("残杀")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(1)
			.maxLevel(1)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(5)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, ExecuteEnchantment::onModifyDamage);
	}

	private static void onModifyDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		if (!(event.attacker() instanceof ServerPlayer player)) {
			return;
		}
		LivingEntity target = event.target();
		float maxHp = target.getMaxHealth();
		if (maxHp <= 0.0F) {
			return;
		}
		float ratio = target.getHealth() / maxHp;
		float multiplier;
		boolean axe = player.getMainHandItem().is(ItemTags.AXES);
		if (ratio < LOW_HP_RATIO) {
			multiplier = axe ? 4.0F : 3.0F;
		} else if (ratio <= MID_HP_RATIO) {
			multiplier = axe ? 2.0F : 1.5F;
		} else {
			return;
		}
		event.damage().setValue(event.damage().getValue() * multiplier);
	}
}
