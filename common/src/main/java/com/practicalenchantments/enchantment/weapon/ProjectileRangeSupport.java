// 箭类附魔共用的距离与来源判定。

package com.practicalenchantments.enchantment.weapon;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

public final class ProjectileRangeSupport {
	private ProjectileRangeSupport() {
	}

	public static boolean isArrowShot(DamageSource source) {
		return source.getDirectEntity() instanceof AbstractArrow && source.getEntity() instanceof Player;
	}

	public static double horizontalDistance(LivingEntity attacker, LivingEntity target) {
		double dx = attacker.getX() - target.getX();
		double dz = attacker.getZ() - target.getZ();
		return Math.sqrt(dx * dx + dz * dz);
	}
}
