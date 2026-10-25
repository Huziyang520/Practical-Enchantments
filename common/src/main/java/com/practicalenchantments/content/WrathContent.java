// 嗔怒效果与癫狂药水的注册。

package com.practicalenchantments.content;

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
	public static final int NORMAL_DURATION = 45 * 20;
	public static final int LONG_DURATION = 75 * 20;
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
						0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
				.attribute(Attributes.ATTACK_DAMAGE,
						Identifier.fromNamespaceAndPath(NAMESPACE, "wrath_damage"),
						0.20D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
				.submit();
		MerlinApi.potions().register(POTION_ID).effect(EFFECT_ID, NORMAL_DURATION).submit();
		MerlinApi.potions().register(LONG_POTION_ID).effect(EFFECT_ID, LONG_DURATION).submit();
	}
}
