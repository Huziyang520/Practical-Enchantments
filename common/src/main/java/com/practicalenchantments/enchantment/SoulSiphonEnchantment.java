package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 汲灵附魔 - 剑/斧/矛 Ⅴ 级（附魔台可出，老手级图书管理员）。
 *
 * <p>每次命中按攻击的原始伤害（护甲减免前，含矛冲锋加成）产出经验球：</p>
 * <pre>经验 = round(min(原始伤害 × 0.2 × 等级, 2 × 等级))</pre>
 */
public final class SoulSiphonEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":soul_siphon";

	private static final float COEFFICIENT_PER_LEVEL = 0.2F;
	private static final float CAP_PER_LEVEL = 2.0F;

	private SoulSiphonEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("汲灵")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(7)
			.maxLevel(5)
			.minCost(5, 7)
			.maxCost(25, 7)
			.anvilCost(2)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, SoulSiphonEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		float raw = Math.max(0.0F, event.originalDamage());
		int xp = Math.round(Math.min(raw * COEFFICIENT_PER_LEVEL * ctx.level(), CAP_PER_LEVEL * ctx.level()));
		if (xp <= 0) {
			return;
		}
		var target = event.target();
		ExperienceOrb orb = new ExperienceOrb(event.level(),
			target.getX(), target.getY() + 0.5, target.getZ(), xp);
		event.level().addFreshEntity(orb);
	}
}
