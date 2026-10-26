// 把饰品栏里的护身符接进 ML 的附魔事件派发。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventType;
import com.huziyang520.merlinlib.event.EnchantmentScanner;
import com.huziyang520.merlinlib.event.EnchantmentScanSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class AmuletScanSource implements EnchantmentScanSource {
	private static final AmuletScanSource INSTANCE = new AmuletScanSource();

	private AmuletScanSource() {
	}

	public static void register() {
		EnchantmentScanner.addSource(INSTANCE);
	}

	@Override
	public List<EnchantmentContext> collect(LivingEntity entity, EnchantmentEventType<?> type) {
		List<EnchantmentContext> contexts = new ArrayList<>();
		for (ItemStack stack : AmuletSupport.equippedAmuletStacks(entity)) {
			for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
				int level = entry.getValue() == null ? 0 : entry.getValue();
				if (level > 0) {
					contexts.add(new EnchantmentContext(entry.getKey(), level, stack, null));
				}
			}
		}
		return contexts;
	}
}
