// 全部配置的 Forge 侧：从登记表生成 SPEC，写进 config/practical_enchantments-server.toml。

package com.practicalenchantments.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ForgeConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger("practical_enchantments");
	private static final Map<String, ForgeConfigSpec.ConfigValue<?>> VALUES = new LinkedHashMap<>();
	public static final ForgeConfigSpec SPEC;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		for (String section : ConfigSchema.sections()) {
			builder.push(section).translation(ConfigSchema.langKey(section));
			String open = null;
			for (ConfigOption option : ConfigSchema.in(section)) {
				String parent = option.parent();
				if (!parent.equals(open)) {
					if (open != null) {
						builder.pop();
					}
					if (!parent.isEmpty()) {
						builder.push(parent).translation(ConfigSchema.langKey(section + "." + parent));
					}
					open = parent;
				}
				VALUES.put(option.key(), define(builder, option));
			}
			if (open != null && !open.isEmpty()) {
				builder.pop();
			}
			builder.pop();
		}
		SPEC = builder.build();
	}

	private ForgeConfig() {
	}

	public static ModConfig.Type type() {
		return ModConfig.Type.SERVER;
	}

	/** 注册期就要用的值先读一遍文件，否则首次启动会按默认值注册。 */
	public static void preload() {
		Map<String, String> raw = new LinkedHashMap<>();
		readToml(Path.of("config", "practical_enchantments-server.toml"), raw);
		Tunables.applyAll(raw::get);
	}

	public static void apply() {
		if (!SPEC.isLoaded()) {
			Tunables.applyAll(key -> null);
			return;
		}
		Tunables.applyAll(key -> {
			ForgeConfigSpec.ConfigValue<?> value = VALUES.get(key);
			return value == null ? null : String.valueOf(value.get());
		});
	}

	/** 界面保存：逐项写回 SPEC 后落盘并刷新运行时快照。 */
	public static void write(Map<String, String> changed) {
		for (Map.Entry<String, String> entry : changed.entrySet()) {
			ForgeConfigSpec.ConfigValue<?> value = VALUES.get(entry.getKey());
			if (value == null) {
				continue;
			}
			try {
				setTyped(value, ConfigSchema.get(entry.getKey()), entry.getValue());
			} catch (RuntimeException invalid) {
				LOGGER.warn("[PracticalEnchantments] 配置项 {} 的值 {} 不合法，保留原值", entry.getKey(), entry.getValue());
			}
		}
		SPEC.save();
		apply();
	}

	/** 界面回显用的当前值。 */
	public static String current(String key) {
		ForgeConfigSpec.ConfigValue<?> value = VALUES.get(key);
		if (value == null || !SPEC.isLoaded()) {
			return ConfigSchema.get(key).defaultValue();
		}
		return String.valueOf(value.get());
	}

	private static void setTyped(ForgeConfigSpec.ConfigValue<?> value, ConfigOption option, String raw) {
		switch (option.kind()) {
			case BOOLEAN -> {
				if (value instanceof ForgeConfigSpec.BooleanValue bool) {
					bool.set(Boolean.parseBoolean(raw.trim()));
				}
			}
			case INTEGER -> {
				if (value instanceof ForgeConfigSpec.IntValue number) {
					number.set((int) Math.round(Double.parseDouble(raw.trim())));
				}
			}
			case DECIMAL -> {
				if (value instanceof ForgeConfigSpec.DoubleValue number) {
					number.set(Double.parseDouble(raw.trim()));
				}
			}
		}
	}

	private static ForgeConfigSpec.ConfigValue<?> define(ForgeConfigSpec.Builder builder, ConfigOption option) {
		ForgeConfigSpec.Builder entry = builder.translation(ConfigSchema.langKey(option.key()));
		return switch (option.kind()) {
			case BOOLEAN -> entry.define(option.name(), option.defaultBool());
			case INTEGER -> entry.comment("范围 " + option.intMin() + " – " + option.intMax())
					.defineInRange(option.name(), option.defaultInt(), option.intMin(), option.intMax());
			case DECIMAL -> entry.comment("范围 " + (float) option.min() + " – " + (float) option.max())
					.defineInRange(option.name(), option.defaultDouble(), option.min(), option.max());
		};
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
}
