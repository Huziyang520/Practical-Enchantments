// 全部配置的 NeoForge 侧：从登记表生成 SPEC，界面由原生 ConfigurationScreen 提供。

package com.practicalenchantments.neoforge.config;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.ConfigOption;
import com.practicalenchantments.config.ConfigSchema;
import com.practicalenchantments.config.Tunables;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class NeoConfig {
	private static final org.slf4j.Logger LOGGER =
			org.slf4j.LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	public static final ModConfigSpec SPEC;
	private static final ModConfig.Type TYPE = resolveType();
	private static final Map<String, ModConfigSpec.ConfigValue<?>> VALUES = new LinkedHashMap<>();

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		for (String section : ConfigSchema.sections()) {
			builder.push(section);
			String open = null;
			for (ConfigOption option : ConfigSchema.in(section)) {
				String parent = option.parent();
				if (!parent.equals(open)) {
					if (open != null) {
						builder.pop();
					}
					if (!parent.isEmpty()) {
						builder.push(parent);
					}
					open = parent;
				}
				VALUES.put(option.key(), define(builder, section, option));
			}
			if (open != null && !open.isEmpty()) {
				builder.pop();
			}
			builder.pop();
		}
		SPEC = builder.build();
	}

	private NeoConfig() {
	}

	public static String langKey(String path) {
		return PracticalEnchantments.MOD_ID + ".configuration." + path;
	}

	public static ModConfig.Type type() {
		return TYPE;
	}

	private static ModConfig.Type resolveType() {
		for (String candidate : new String[] {"SYNCED", "SERVER", "COMMON", "LOCAL", "CLIENT"}) {
			try {
				return ModConfig.Type.valueOf(candidate);
			} catch (IllegalArgumentException removedOnThisLoader) {
				// 换一个仍然存在的类型
			}
		}
		throw new IllegalStateException("Practical Enchantments: loader 没有可用的服务端配置类型");
	}


	/**
	 * 注册期就要用到的值（流血/腐毒层时长、眨眼药水时长）在 SPEC 加载前先读一遍文件，
	 * 否则首次启动会按默认值注册，改过的配置要等第二次启动才生效。
	 */
	public static void preload() {
		Map<String, String> raw = new LinkedHashMap<>();
		for (String name : new String[] {"practical_enchantments-server.toml",
				"practical_enchantments-common.toml", "practical_enchantments-synced.toml"}) {
			readToml(Path.of("config", name), raw);
		}
		Tunables.applyAll(raw::get);
	}

	private static void readToml(Path file, Map<String, String> out) {
		if (!Files.isRegularFile(file)) {
			return;
		}
		try {
			String section = "";
			for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
				String trimmed = line.trim();
				if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
					section = trimmed.substring(1, trimmed.length() - 1).trim();
					continue;
				}
				if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
					continue;
				}
				int cut = trimmed.indexOf('=');
				String key = trimmed.substring(0, cut).trim();
				String value = trimmed.substring(cut + 1).trim();
				int comment = value.indexOf('#');
				if (comment >= 0) {
					value = value.substring(0, comment).trim();
				}
				if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
					value = value.substring(1, value.length() - 1);
				}
				out.put(section.isEmpty() ? key : section + "." + key, value);
			}
		} catch (IOException unreadable) {
			LOGGER.warn("[PracticalEnchantments] 读取 {} 失败，注册期沿用默认值", file);
		}
	}

	public static void apply() {
		if (!SPEC.isLoaded()) {
			Tunables.applyAll(key -> null);
			return;
		}
		Tunables.applyAll(key -> {
			ModConfigSpec.ConfigValue<?> value = VALUES.get(key);
			return value == null ? null : String.valueOf(value.get());
		});
	}

	private static ModConfigSpec.ConfigValue<?> define(ModConfigSpec.Builder builder, String section,
			ConfigOption option) {
		var entry = builder.translation(langKey(option.key()));
		return switch (option.kind()) {
			case BOOLEAN -> entry.define(option.name(), option.defaultBool());
			case INTEGER -> entry.comment("范围 " + option.intMin() + " – " + option.intMax())
					.defineInRange(option.name(), option.defaultInt(), option.intMin(), option.intMax());
			case DECIMAL -> entry.comment("范围 " + (float) option.min() + " – " + (float) option.max())
					.defineInRange(option.name(), option.defaultDouble(), option.min(), option.max());
		};
	}
}
