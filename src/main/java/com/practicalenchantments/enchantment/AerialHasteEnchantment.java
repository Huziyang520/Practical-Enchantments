package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentEffectsBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 浮空速掘附魔 - 头盔 I 级
 * 效果：消除空中挖掘速度惩罚（空中 = 地面速度）
 */
public final class AerialHasteEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":aerial_haste";

	/** 本附魔的 Holder（供 Mixin 判断头盔是否佩戴本附魔） */
	public static volatile Holder<Enchantment> HOLDER;

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("浮空速掘")
			.supportedItems("#minecraft:enchantable/head_armor")
			.weight(4) // 稀有
			.maxLevel(1)
			.minCost(8, 0)
			.maxCost(25, 0)
			.anvilCost(2)
			.slots("head")
			.effects(EnchantmentEffectsBuilder.create().build()));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		// 效果由 Mixin PlayerGetDestroySpeedMixin 实现，无需注册事件回调
	}
}
