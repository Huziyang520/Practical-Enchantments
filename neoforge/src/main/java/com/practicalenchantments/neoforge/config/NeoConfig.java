package com.practicalenchantments.neoforge.config;

import com.practicalenchantments.config.VanillaTweaks;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 原版附魔调整的 NeoForge 配置：配置类型在运行时解析，跟存档走，界面由原生 ConfigurationScreen 提供。
 */
public final class NeoConfig {

	public static final ModConfigSpec SPEC;

	/** 运行时可用的配置类型；loader 之间的类型名差异只在这里吸收一次。 */
	private static final ModConfig.Type TYPE = resolveType();

	private static final ModConfigSpec.ConfigValue<Boolean> INFINITY_CROSSBOW;
	private static final ModConfigSpec.ConfigValue<Boolean> INFINITY_NO_ARROW;
	private static final ModConfigSpec.ConfigValue<Boolean> INFINITY_SPECTRAL;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
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

	private NeoConfig() {
	}

	/**
	 * 依次尝试服务端同步、共享、本地三种类型，取第一个存在的。
	 *
	 * @return 本 loader 上可用的配置类型
	 */
	private static ModConfig.Type resolveType() {
		for (String candidate : new String[] {"SYNCED", "COMMON", "LOCAL"}) {
			try {
				return ModConfig.Type.valueOf(candidate);
			} catch (IllegalArgumentException removedOnThisLoader) {
				// 这个 loader 上没有该类型，继续试下一个
			}
		}
		throw new IllegalStateException("Practical Enchantments: loader 没有可用的服务端配置类型");
	}

	/** @return 配置类型；服务端同步优先，缺则依次退到共享、本地 */
	public static ModConfig.Type type() {
		return TYPE;
	}

	/** 把当前配置值推给 common 的开关持有者。加载与重载各调用一次，模组构建期也先调一次。 */
	public static void apply() {
		if (!SPEC.isLoaded()) {
			VanillaTweaks.apply(VanillaTweaks.DEFAULT_INFINITY_CROSSBOW, VanillaTweaks.DEFAULT_INFINITY_NO_ARROW,
					VanillaTweaks.DEFAULT_INFINITY_SPECTRAL);
			return;
		}
		VanillaTweaks.apply(INFINITY_CROSSBOW.get(), INFINITY_NO_ARROW.get(), INFINITY_SPECTRAL.get());
	}

	private static ModConfigSpec.ConfigValue<Boolean> define(ModConfigSpec.Builder builder, String key,
															boolean defaultValue, String comment) {
		return builder.translation(VanillaTweaks.nameKey(key)).comment(comment).define(key, defaultValue);
	}
}
