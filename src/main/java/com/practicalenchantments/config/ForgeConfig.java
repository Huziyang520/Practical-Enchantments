// 原版附魔调整的配置：SERVER 类型，跟存档走，config/practical_enchantments-server.toml。

package com.practicalenchantments.config;

import java.util.List;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeConfig {
	public static final List<String> KEYS = List.of(VanillaTweaks.KEY_INFINITY_CROSSBOW,
			VanillaTweaks.KEY_INFINITY_NO_ARROW, VanillaTweaks.KEY_INFINITY_SPECTRAL,
			VanillaTweaks.KEY_QUICK_CHARGE_BOW, VanillaTweaks.KEY_POWER_CROSSBOW,
			VanillaTweaks.KEY_THORNS_SHIELD);

	public static final ForgeConfigSpec SPEC;
	private static final ForgeConfigSpec.BooleanValue INFINITY_CROSSBOW;
	private static final ForgeConfigSpec.BooleanValue INFINITY_NO_ARROW;
	private static final ForgeConfigSpec.BooleanValue INFINITY_SPECTRAL;
	private static final ForgeConfigSpec.BooleanValue QUICK_CHARGE_BOW;
	private static final ForgeConfigSpec.BooleanValue POWER_CROSSBOW;
	private static final ForgeConfigSpec.BooleanValue THORNS_SHIELD;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push(VanillaTweaks.SECTION_KEY)
				.translation(VanillaTweaks.SECTION_LANG_KEY)
				.comment("原版附魔调整：无限在弩与光灵箭上的行为。");
		INFINITY_CROSSBOW = define(builder, VanillaTweaks.KEY_INFINITY_CROSSBOW,
				VanillaTweaks.DEFAULT_INFINITY_CROSSBOW, "无限是否可以附在弩上（默认关：只有弓能用）。");
		INFINITY_NO_ARROW = define(builder, VanillaTweaks.KEY_INFINITY_NO_ARROW,
				VanillaTweaks.DEFAULT_INFINITY_NO_ARROW, "带无限的武器在背包没有普通箭时是否仍可射击。");
		INFINITY_SPECTRAL = define(builder, VanillaTweaks.KEY_INFINITY_SPECTRAL,
				VanillaTweaks.DEFAULT_INFINITY_SPECTRAL,
				"无限是否同样作用于光灵箭；开时仍要求背包里至少有一根光灵箭。");
		QUICK_CHARGE_BOW = define(builder, VanillaTweaks.KEY_QUICK_CHARGE_BOW,
				VanillaTweaks.DEFAULT_QUICK_CHARGE_BOW, "快速装填是否可以附在弓上（默认关）。");
		POWER_CROSSBOW = define(builder, VanillaTweaks.KEY_POWER_CROSSBOW,
				VanillaTweaks.DEFAULT_POWER_CROSSBOW, "力量是否可以附在弩上（默认开）。");
		THORNS_SHIELD = define(builder, VanillaTweaks.KEY_THORNS_SHIELD,
				VanillaTweaks.DEFAULT_THORNS_SHIELD, "刺棘是否可以附在盾上（默认开）。");
		builder.pop();
		SPEC = builder.build();
	}

	private ForgeConfig() {
	}

	public static ModConfig.Type type() {
		return ModConfig.Type.SERVER;
	}

	public static void apply() {
		if (!SPEC.isLoaded()) {
			VanillaTweaks.apply(VanillaTweaks.DEFAULT_INFINITY_CROSSBOW, VanillaTweaks.DEFAULT_INFINITY_NO_ARROW,
					VanillaTweaks.DEFAULT_INFINITY_SPECTRAL);
			VanillaTweaks.applyExtra(VanillaTweaks.DEFAULT_QUICK_CHARGE_BOW, VanillaTweaks.DEFAULT_POWER_CROSSBOW,
					VanillaTweaks.DEFAULT_THORNS_SHIELD);
			return;
		}
		VanillaTweaks.apply(
				MezzConfigBridge.value(VanillaTweaks.KEY_INFINITY_CROSSBOW),
				MezzConfigBridge.value(VanillaTweaks.KEY_INFINITY_NO_ARROW),
				MezzConfigBridge.value(VanillaTweaks.KEY_INFINITY_SPECTRAL));
		VanillaTweaks.applyExtra(
				MezzConfigBridge.value(VanillaTweaks.KEY_QUICK_CHARGE_BOW),
				MezzConfigBridge.value(VanillaTweaks.KEY_POWER_CROSSBOW),
				MezzConfigBridge.value(VanillaTweaks.KEY_THORNS_SHIELD));
	}

	public static void write(Map<String, Boolean> values) {
		if (!SPEC.isLoaded()) {
			return;
		}
		for (Map.Entry<String, Boolean> entry : values.entrySet()) {
			value(entry.getKey()).set(entry.getValue());
		}
		SPEC.save();
		apply();
		for (Map.Entry<String, Boolean> entry : values.entrySet()) {
			MezzConfigBridge.push(entry.getKey(), entry.getValue());
		}
	}

	private static ForgeConfigSpec.BooleanValue value(String key) {
		return switch (key) {
			case VanillaTweaks.KEY_INFINITY_CROSSBOW -> INFINITY_CROSSBOW;
			case VanillaTweaks.KEY_INFINITY_NO_ARROW -> INFINITY_NO_ARROW;
			case VanillaTweaks.KEY_INFINITY_SPECTRAL -> INFINITY_SPECTRAL;
			case VanillaTweaks.KEY_QUICK_CHARGE_BOW -> QUICK_CHARGE_BOW;
			case VanillaTweaks.KEY_POWER_CROSSBOW -> POWER_CROSSBOW;
			case VanillaTweaks.KEY_THORNS_SHIELD -> THORNS_SHIELD;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}

	private static ForgeConfigSpec.BooleanValue define(ForgeConfigSpec.Builder builder, String key,
			boolean defaultValue, String comment) {
		return builder.translation(VanillaTweaks.nameKey(key)).comment(comment).define(key, defaultValue);
	}
}
