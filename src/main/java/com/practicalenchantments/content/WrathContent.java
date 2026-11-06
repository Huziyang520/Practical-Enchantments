// 嗔怒效果与癫狂药水的注册。

package com.practicalenchantments.content;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.effect.MerlinMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class WrathContent {
	public static final String NAMESPACE = "practical_enchantments";
	public static final ResourceLocation EFFECT_ID = new ResourceLocation(NAMESPACE, "wrath");
	public static final ResourceLocation POTION_ID = new ResourceLocation(NAMESPACE, "wrath");
	public static final ResourceLocation LONG_POTION_ID = new ResourceLocation(NAMESPACE, "long_wrath");
	private static boolean registered;

	private WrathContent() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		MerlinMobEffect effect = MerlinApi.effects().register(EFFECT_ID)
				.category(MobEffectCategory.BENEFICIAL)
				.color(0xB4451F)
				.attribute(Attributes.MOVEMENT_SPEED, id("wrath_speed"),
						Tunables.wrathSpeedBonus(), AttributeModifier.Operation.MULTIPLY_TOTAL)
				.attribute(Attributes.ATTACK_DAMAGE, id("wrath_damage"),
						Tunables.wrathDamageBonus(), AttributeModifier.Operation.MULTIPLY_TOTAL)
				.submit();
		MerlinApi.potions().register(POTION_ID).effect(effect.instance(Tunables.secondsToTicks(Tunables.wrathNormalSeconds()), 0)).submit();
		MerlinApi.potions().register(LONG_POTION_ID).effect(effect.instance(Tunables.secondsToTicks(Tunables.wrathLongSeconds()), 0)).submit();
	}

	private static ResourceLocation id(String path) {
		return new ResourceLocation(NAMESPACE, path);
	}
}
