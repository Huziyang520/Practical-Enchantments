// 创伤附魔 - 剑/斧 Ⅱ 级：命中叠流血层数，矛类武器（若存在）多一层。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.BleedContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class TraumaEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":trauma";

	private TraumaEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
				BuiltInEvents.MODIFY_DAMAGE, TraumaEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		int layers = ctx.level();
		if (isSpear(ctx.itemStack())) {
			layers++;
		}
		BleedContent.add(event.target(), layers);
	}

	private static boolean isSpear(ItemStack stack) {
		ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
		return key != null && key.getPath().contains("spear");
	}
}
