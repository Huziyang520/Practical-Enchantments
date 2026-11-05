// 1.20.1 的无敌帧字段就叫 invulnerableTime，直接清零即可。

package com.practicalenchantments.support;

import net.minecraft.world.entity.LivingEntity;

public final class ImmunityReset {
	private ImmunityReset() {
	}

	public static void zero(LivingEntity entity) {
		entity.invulnerableTime = 0;
	}
}
