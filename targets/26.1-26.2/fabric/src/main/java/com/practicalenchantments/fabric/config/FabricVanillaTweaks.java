// 全部配置的 Fabric 侧存取：config/practical_enchantments/vanilla_tweaks.properties。

package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.ConfigOption;
import com.practicalenchantments.config.ConfigSchema;
import com.practicalenchantments.config.Tunables;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FabricVanillaTweaks {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final Path FILE = Path.of("config", PracticalEnchantments.MOD_ID, "vanilla_tweaks.properties");
	private static final Map<String, String> CURRENT = new LinkedHashMap<>();

	private FabricVanillaTweaks() {
	}

	public static void load() {
		Map<String, String> values = read();
		write(values);
		apply(values);
	}

	public static void save(Map<String, String> changed) {
		Map<String, String> values = new LinkedHashMap<>(CURRENT);
		values.putAll(changed);
		write(values);
		apply(values);
		for (Map.Entry<String, String> entry : changed.entrySet()) {
			MezzConfigBridge.push(entry.getKey(), entry.getValue());
		}
	}

	/** 当前生效值（供界面回显）。 */
	public static String current(String key) {
		return CURRENT.getOrDefault(key, ConfigSchema.get(key).defaultValue());
	}

	public static Map<String, String> snapshot() {
		return new LinkedHashMap<>(CURRENT);
	}

	private static Map<String, String> read() {
		Map<String, String> values = new LinkedHashMap<>();
		Properties properties = new Properties();
		if (Files.isRegularFile(FILE)) {
			try (Reader reader = new InputStreamReader(Files.newInputStream(FILE), StandardCharsets.UTF_8)) {
				properties.load(reader);
			} catch (IOException exception) {
				LOGGER.error("[PracticalEnchantments] 读取 {} 失败，沿用默认值", FILE, exception);
			}
		}
		for (ConfigOption option : ConfigSchema.all()) {
			String raw = properties.getProperty(option.key());
			values.put(option.key(), raw == null ? option.defaultValue() : raw.trim());
		}
		return values;
	}

	private static void write(Map<String, String> values) {
		StringBuilder text = new StringBuilder();
		text.append("# Practical Enchantments —— 附魔参数配置\n");
		text.append("# 改完保存后：重进世界 / 重启服务器生效。\n");
		text.append("# 每项后面括号里是默认值与取值范围。\n");
		for (String section : ConfigSchema.sections()) {
			text.append('\n').append("# --- ").append(section).append(" ---\n");
			for (ConfigOption option : ConfigSchema.in(section)) {
				text.append("# ").append(option.key()).append(' ').append(option.defaultValue());
				if (option.kind() == ConfigOption.Kind.INTEGER) {
					text.append(" (").append(option.intMin()).append('~').append(option.intMax()).append(')');
				} else if (option.kind() == ConfigOption.Kind.DECIMAL) {
					text.append(" (").append((float) option.min()).append('~').append((float) option.max()).append(')');
				}
				text.append('\n');
				text.append(option.key()).append('=').append(values.getOrDefault(option.key(), option.defaultValue()))
						.append('\n');
			}
		}
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, text.toString(), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			LOGGER.error("[PracticalEnchantments] 写入 {} 失败", FILE, exception);
		}
	}

	private static void apply(Map<String, String> values) {
		CURRENT.clear();
		CURRENT.putAll(values);
		Tunables.applyAll(CURRENT::get);
	}
}
