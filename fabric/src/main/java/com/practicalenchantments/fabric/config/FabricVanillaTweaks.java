// Fabric 侧原版附魔调整的存取：config/practical_enchantments/vanilla_tweaks.properties。

package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.VanillaTweaks;
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

	public static final String[] KEYS = {
			VanillaTweaks.KEY_INFINITY_CROSSBOW,
			VanillaTweaks.KEY_INFINITY_NO_ARROW,
			VanillaTweaks.KEY_INFINITY_SPECTRAL,
	};

	private FabricVanillaTweaks() {
	}

	public static void load() {
		Map<String, Boolean> values = read();
		write(values);
		apply(values);
	}

	public static void save(Map<String, Boolean> changed) {
		Map<String, Boolean> values = new LinkedHashMap<>();
		for (String key : KEYS) {
			values.put(key, changed.getOrDefault(key, VanillaTweaks.value(key)));
		}
		write(values);
		apply(values);
		for (Map.Entry<String, Boolean> entry : values.entrySet()) {
			MezzConfigBridge.push(entry.getKey(), entry.getValue());
		}
	}

	public static boolean current(String key) {
		return MezzConfigBridge.value(key);
	}

	private static Map<String, Boolean> read() {
		Map<String, Boolean> values = new LinkedHashMap<>();
		Properties properties = new Properties();
		if (Files.isRegularFile(FILE)) {
			try (Reader reader = new InputStreamReader(Files.newInputStream(FILE), StandardCharsets.UTF_8)) {
				properties.load(reader);
			} catch (IOException exception) {
				LOGGER.error("[PracticalEnchantments] 读取 {} 失败，沿用默认值", FILE, exception);
			}
		}
		for (String key : KEYS) {
			String raw = properties.getProperty(key);
			values.put(key, raw == null ? VanillaTweaks.defaultValue(key) : Boolean.parseBoolean(raw.trim()));
		}
		return values;
	}

	private static void write(Map<String, Boolean> values) {
		StringBuilder text = new StringBuilder();
		text.append("# Practical Enchantments —— 原版附魔调整\n");
		text.append("# 改完保存后：重进世界 / 重启服务器生效。\n");
		text.append("#\n");
		text.append("# infinity_crossbow  无限是否可以附在弩上（默认 false）\n");
		text.append("# infinity_no_arrow  背包没有普通箭时是否仍可射击（默认 true）\n");
		text.append("# infinity_spectral  无限是否同样作用于光灵箭（默认 true）\n");
		for (String key : KEYS) {
			text.append('\n').append(key).append('=').append(values.get(key)).append('\n');
		}
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, text.toString(), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			LOGGER.error("[PracticalEnchantments] 写入 {} 失败", FILE, exception);
		}
	}

	private static void apply(Map<String, Boolean> values) {
		VanillaTweaks.apply(
				values.get(VanillaTweaks.KEY_INFINITY_CROSSBOW),
				values.get(VanillaTweaks.KEY_INFINITY_NO_ARROW),
				values.get(VanillaTweaks.KEY_INFINITY_SPECTRAL));
	}
}
