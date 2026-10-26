// 把饰品栏里的护身符接进 ML 的附魔事件派发。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventType;
import com.huziyang520.merlinlib.event.EnchantmentScanSource;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class AmuletScanSource implements EnchantmentScanSource {
	private static final AmuletScanSource INSTANCE = new AmuletScanSource();

	private AmuletScanSource() {
	}

	public static void register() {
		com.huziyang520.merlinlib.event.EnchantmentScanner.addSource(INSTANCE);
	}

	@Override
	public List<EnchantmentContext> collect(LivingEntity entity, EnchantmentEventType<?> type) {
		List<EnchantmentContext> contexts = new ArrayList<>();
		for (ItemStack stack : AmuletSupport.equippedAmuletStacks(entity)) {
			for (var entry : stack.getEnchantments().entrySet()) {
				Holder<Enchantment> holder = entry.getKey();
				int level = entry.getIntValue();
				if (level > 0) {
					contexts.add(new EnchantmentContext(holder, level, stack, null));
				}
			}
		}
		return contexts;
	}
}
