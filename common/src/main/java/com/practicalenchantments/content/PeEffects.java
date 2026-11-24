// 慌乱 / 眩惑的 id 与查询入口；效果与药水分别由两个 Content 类注册。

package com.practicalenchantments.content;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public final class PeEffects {
	public static final Identifier PANIC = id("panic");
	public static final Identifier DIZZINESS = id("dizziness");
	public static final Identifier PANIC_POTION = id("panic");
	public static final Identifier LONG_PANIC_POTION = id("long_panic");
	public static final Identifier DIZZINESS_POTION = id("dizziness");
	public static final Identifier LONG_DIZZINESS_POTION = id("long_dizziness");

	private PeEffects() {
	}

	public static boolean hasPanic(LivingEntity entity) {
		return has(entity, PANIC);
	}

	public static boolean hasDizziness(LivingEntity entity) {
		return has(entity, DIZZINESS);
	}

	public static boolean has(LivingEntity entity, Identifier effectId) {
		if (entity == null) {
			return false;
		}
		return BuiltInRegistries.MOB_EFFECT.get(effectId)
				.map(holder -> entity.hasEffect((Holder<MobEffect>) holder))
				.orElse(false);
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, path);
	}
}
