// 26.3 把无敌帧字段改名为 damageCooldownTime，26.1-26.2 仍叫 invulnerableTime。

package com.practicalenchantments.support;

import net.minecraft.world.entity.LivingEntity;
import java.lang.reflect.Field;

public final class ImmunityReset {
	private static final Field FIELD = find();

	private ImmunityReset() {
	}

	private static Field find() {
		for (String name : new String[]{"damageCooldownTime", "invulnerableTime"}) {
			try {
				Field field = LivingEntity.class.getField(name);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException ignored) {
				// 换下一个候选名
			}
		}
		return null;
	}

	public static void zero(LivingEntity entity) {
		if (FIELD == null) {
			return;
		}
		try {
			FIELD.setInt(entity, 0);
		} catch (IllegalAccessException ignored) {
			// 字段不可写就算了，最多少结算几 tick
		}
	}
}
