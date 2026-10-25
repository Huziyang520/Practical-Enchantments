// 羽落附魔 - 靴子单级（娱乐，仅创造/指令获取）。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class GentleDescentEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":gentle_descent";
	private static Holder<Enchantment> HOLDER;
	private static final int SLOW_FALLING_DURATION = 400;
	private static final int REFRESH_INTERVAL = 20;

	private static final Identifier SF_GRANTED =
		Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "gentle_descent_sf_granted");

	private GentleDescentEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(GentleDescentEnchantment::onLivingTick);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		var entity = event.entity();
		ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
		boolean hasGentle = HOLDER != null && !boots.isEmpty()
			&& boots.getEnchantments().getLevel(HOLDER) > 0;

		MobEffectInstance current = entity.getEffect(MobEffects.SLOW_FALLING);

		if (hasGentle && !entity.isShiftKeyDown()) {
			if (current == null) {
				entity.addEffect(new MobEffectInstance(
					MobEffects.SLOW_FALLING, SLOW_FALLING_DURATION, 0, true, false));
			} else if (isOurs(current) && event.tickCount() % REFRESH_INTERVAL == 0) {
				entity.addEffect(new MobEffectInstance(
					MobEffects.SLOW_FALLING, SLOW_FALLING_DURATION, 0, true, false));
			}
			EntityCounter.set(entity, SF_GRANTED, 1);
		} else {
			if (current != null && isOurs(current) && EntityCounter.get(entity, SF_GRANTED) > 0) {
				entity.removeEffect(MobEffects.SLOW_FALLING);
			}
			EntityCounter.set(entity, SF_GRANTED, 0);
		}
	}

	private static boolean isOurs(MobEffectInstance instance) {
		return !instance.isInfiniteDuration()
			&& instance.getAmplifier() == 0
			&& instance.getDuration() <= SLOW_FALLING_DURATION;
	}
}
