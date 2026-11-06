// 强劲附魔 - 弩 Ⅴ 级：命中给予击退、缓慢与流血层数。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.BleedContent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class PowerfulEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":powerful";
	public static final String GROUP_NAME = "crossbow";
	
	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
				BuiltInEvents.MODIFY_DAMAGE, PowerfulEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		if (target == null) {
			return;
		}
		int level = ctx.level();
		push(event, target, level);
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Tunables.secondsToTicks(Tunables.powerfulSlowSecondsBase()
 					+ Tunables.powerfulSlowSecondsPerLevel() * level), level - 1, false, true));
		BleedContent.add(target, (level + 1) / Tunables.powerfulBleedLayersDivisor());
	}

	private static void push(BuiltInEvents.ModifyDamageEvent event, LivingEntity target, int level) {
		Vec3 from = event.attacker() == null ? target.position() : event.attacker().position();
		double dx = target.getX() - from.x;
		double dz = target.getZ() - from.z;
		double length = Math.sqrt(dx * dx + dz * dz);
		if (length < 1.0E-4D) {
			dx = 0.0D;
			dz = 1.0D;
		} else {
			dx /= length;
			dz /= length;
		}
		MerlinApi.combat().knockback(target,
					Tunables.powerfulKnockbackBase() + Tunables.powerfulKnockbackPerLevel() * level, dx, dz);
	}
}
