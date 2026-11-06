// Jupiter 配置页：每个分类一个页签，镜像写在 Jupiter 自己的 jcfg 里，权威存储仍是 server.toml。

package com.practicalenchantments.config;

import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.container.FileConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.practicalenchantments.PracticalEnchantments;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JupiterContainer extends FileConfigContainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final String FILE = "config/practical_enchantments/settings.jcfg";
	private final Map<String, String> pending = new LinkedHashMap<>();

	public JupiterContainer() {
		super(new ResourceLocation(PracticalEnchantments.MOD_ID, "configuration"),
				Component.translatable(ConfigSchema.langKey("configuration")), FILE);
	}

	@Override
	public void init() {
		for (String section : ConfigSchema.sections()) {
			ConfigGroup group = createTab(section, Component.translatable(ConfigSchema.langKey(section)));
			for (ConfigOption option : ConfigSchema.in(section)) {
				group.addEntry(entry(option));
			}
		}
	}

	@Override
	public void load() {
		this.configTabs.clear();
		init();
	}

	@Override
	public void save() {
		try {
			Files.createDirectories(Path.of(FILE).getParent());
		} catch (IOException exception) {
			LOGGER.warn("[PracticalEnchantments] 无法创建 {} 所在目录", FILE, exception);
		}
		super.save();
		if (this.pending.isEmpty()) {
			return;
		}
		Map<String, String> values = new LinkedHashMap<>(this.pending);
		this.pending.clear();
		ForgeConfig.write(values);
	}

	private ConfigEntry<?> entry(ConfigOption option) {
		String key = option.key();
		Component label = Component.translatable(ConfigSchema.nameKey(option));
		Component tooltip = Component.translatable(ConfigSchema.tooltipKey(option));
		String current = ForgeConfig.current(key);
		return switch (option.kind()) {
			case BOOLEAN -> BooleanEntry.builder(label, Boolean.parseBoolean(current))
					.key(key)
					.tooltip(tooltip)
					.callback((chosen, initial, reset) -> this.pending.put(key, chosen.toString()))
					.build();
			case INTEGER -> IntegerEntry.builder(label, (int) Math.round(parseDouble(current, option.defaultInt())))
					.key(key)
					.min(option.intMin())
					.max(option.intMax())
					.tooltip(tooltip)
					.callback((chosen, initial, reset) -> this.pending.put(key, chosen.toString()))
					.build();
			case DECIMAL -> DoubleEntry.builder(label, parseDouble(current, option.defaultDouble()))
					.key(key)
					.min(option.min())
					.max(option.max())
					.tooltip(tooltip)
					.callback((chosen, initial, reset) -> this.pending.put(key, chosen.toString()))
					.build();
		};
	}

	private static double parseDouble(String text, double fallback) {
		try {
			return Double.parseDouble(text.trim());
		} catch (NumberFormatException exception) {
			return fallback;
		}
	}
}
