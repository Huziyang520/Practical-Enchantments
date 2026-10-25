// 原版附魔调整的配置：SERVER 类型，跟存档走，config/practical_enchantments-server.toml。

package com.practicalenchantments.config;

import java.util.List;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeConfig {
	public static final List<String> KEYS = List.of(VanillaTweaks.KEY_INFINITY_CROSSBOW,
			VanillaTweaks.KEY_INFINITY_NO_ARROW, VanillaTweaks.KEY_INFINITY_SPECTRAL);

	public static final ForgeConfigSpec SPEC;
	private static final ForgeConfigSpec.BooleanValue INFINITY_CROSSBOW;
	private static final ForgeConfigSpec.BooleanValue INFINITY_NO_ARROW;
	private static final ForgeConfigSpec.BooleanValue INFINITY_SPECTRAL;

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
			return;
		}
		VanillaTweaks.apply(INFINITY_CROSSBOW.get(), INFINITY_NO_ARROW.get(), INFINITY_SPECTRAL.get());
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
	}

	private static ForgeConfigSpec.BooleanValue value(String key) {
		return switch (key) {
			case VanillaTweaks.KEY_INFINITY_CROSSBOW -> INFINITY_CROSSBOW;
			case VanillaTweaks.KEY_INFINITY_NO_ARROW -> INFINITY_NO_ARROW;
			case VanillaTweaks.KEY_INFINITY_SPECTRAL -> INFINITY_SPECTRAL;
			default -> throw new IllegalArgumentException("未知的原版附魔调整项: " + key);
		};
	}

	private static ForgeConfigSpec.BooleanValue define(ForgeConfigSpec.Builder builder, String key,
			boolean defaultValue, String comment) {
		return builder.translation(VanillaTweaks.nameKey(key)).comment(comment).define(key, defaultValue);
	}
}
