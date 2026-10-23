package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 浮空速掘附魔
 */
public final class AerialHasteEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":aerial_haste";

	/**
	 * 本附魔对象（供 Mixin 判断头盔是否佩戴本附魔）。
	 *
	 * <p>上游版本为 {@code Holder<Enchantment>}；1.20.1 无 Holder 层，见类注释第 2、3 条。</p>
	 */
	public static volatile Enchantment HOLDER;


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		HOLDER = PracticalEnchantments.resolveEnchantment(ID);
		// 效果由 Mixin PlayerGetDestroySpeedMixin 实现，无需注册事件回调
	}
}