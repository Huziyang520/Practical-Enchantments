// 荆棘盾：把「本次受击者」沿后处理线程传下去，供附魔槽位判定用。

package com.practicalenchantments.enchantment.support;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public final class ThornsShieldSupport {
	private static final ThreadLocal<LivingEntity> VICTIM = new ThreadLocal<>();
	private static final ThreadLocal<LivingEntity> PREVIOUS = new ThreadLocal<>();

	private ThornsShieldSupport() {
	}

	public static void push(Entity entity) {
		PREVIOUS.set(VICTIM.get());
		VICTIM.set(entity instanceof LivingEntity living ? living : null);
	}

	public static boolean active() {
		return VICTIM.get() != null;
	}

	public static void pop() {
		LivingEntity previous = PREVIOUS.get();
		if (previous == null) {
			VICTIM.remove();
		} else {
			VICTIM.set(previous);
		}
		PREVIOUS.remove();
	}

	/** 只有「正在举这面盾」才允许盾上的荆棘参与结算。 */
	public static boolean shouldSkipShieldThorns(EquipmentSlot slot) {
		LivingEntity wearer = VICTIM.get();
		if (wearer == null || (slot != EquipmentSlot.OFFHAND && slot != EquipmentSlot.MAINHAND)) {
			return false;
		}
		ItemStack shield = wearer.getItemBySlot(slot);
		if (!(shield.getItem() instanceof ShieldItem)) {
			return false;
		}
		return !wearer.isBlocking() || !ItemStack.matches(shield, wearer.getUseItem());
	}
}
