// 自然生成的怪物有几率手持带「风王结界」的剑，1.20.1 没有 1.21 的附魔标签机制。

package com.practicalenchantments.support;

import com.practicalenchantments.enchantment.EnchantmentDefinitions;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public final class InvisibleAirMobGear {
	private static final float CHANCE = 0.02F;

	private InvisibleAirMobGear() {
	}

	public static void tryApply(Entity entity, Level level, boolean loadedFromDisk) {
		if (loadedFromDisk || level.isClientSide() || !(entity instanceof Mob mob)) {
			return;
		}

		ItemStack held = mob.getMainHandItem();

		if (!held.is(ItemTags.SWORDS) || mob.getRandom().nextFloat() >= CHANCE) {
			return;
		}

		Enchantment enchantment = level.registryAccess()
			.registryOrThrow(Registries.ENCHANTMENT)
			.get(EnchantmentDefinitions.INVISIBLE_AIR);

		if (enchantment == null || EnchantmentHelper.getItemEnchantmentLevel(enchantment, held) > 0) {
			return;
		}

		held.enchant(enchantment, 1);
	}
}
