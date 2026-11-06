// 嗔怒效果与癫狂药水的注册。

package com.practicalenchantments.content;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class WrathContent {
	public static final String NAMESPACE = "practical_enchantments";
	public static final Identifier EFFECT_ID = Identifier.fromNamespaceAndPath(NAMESPACE, "wrath");
	public static final Identifier POTION_ID = Identifier.fromNamespaceAndPath(NAMESPACE, "wrath");
	public static final Identifier LONG_POTION_ID = Identifier.fromNamespaceAndPath(NAMESPACE, "long_wrath");
	private static boolean registered;

	private WrathContent() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		MerlinApi.effects().register(EFFECT_ID)
				.category(MobEffectCategory.BENEFICIAL)
				.color(0xB4451F)
				.attribute(Attributes.MOVEMENT_SPEED,
						Identifier.fromNamespaceAndPath(NAMESPACE, "wrath_speed"),
						Tunables.wrathSpeedBonus(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
				.attribute(Attributes.ATTACK_DAMAGE,
						Identifier.fromNamespaceAndPath(NAMESPACE, "wrath_damage"),
						Tunables.wrathDamageBonus(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
				.submit();
		MerlinApi.potions().register(POTION_ID).effect(EFFECT_ID, Tunables.secondsToTicks(Tunables.wrathNormalSeconds())).submit();
		MerlinApi.potions().register(LONG_POTION_ID).effect(EFFECT_ID, Tunables.secondsToTicks(Tunables.wrathLongSeconds())).submit();
	}
}
