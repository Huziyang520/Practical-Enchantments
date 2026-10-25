// 挑血附魔 - 剑、斧（I-Ⅲ 级），未嗔怒的目标只受一成伤害，命中后激怒目标。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.WrathContent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

public final class BloodlettingEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":bloodletting";
	private static final int ANGER_DURATION = 10 * 20;

	private BloodlettingEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, BloodlettingEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		var wrath = BuiltInRegistries.MOB_EFFECT.get(WrathContent.EFFECT_ID);
		if (wrath.isEmpty()) {
			return;
		}
		Holder<MobEffect> effect = wrath.get();
		float current = event.damage().getValue();
		if (target.hasEffect(effect)) {
			float bonus = (float) (1.0D + 0.15D + 0.20D * ctx.level());
			event.damage().setValue(current * bonus);
			return;
		}
		event.damage().setValue(current * 0.10F);
		target.addEffect(new MobEffectInstance(effect, ANGER_DURATION, 0));
	}
}
