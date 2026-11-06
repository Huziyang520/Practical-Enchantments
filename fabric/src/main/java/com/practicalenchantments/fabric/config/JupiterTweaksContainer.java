// Jupiter 配置页：每个分类一个页签，整数值用 IntegerEntry、小数用 DoubleEntry。

package com.practicalenchantments.fabric.config;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.container.FileConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.ConfigOption;
import com.practicalenchantments.config.ConfigSchema;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class JupiterTweaksContainer extends FileConfigContainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final String MIRROR = "config/" + PracticalEnchantments.MOD_ID + "/vanilla_tweaks.jcfg";
	private final Map<String, String> pending = new LinkedHashMap<>();

	private JupiterTweaksContainer() {
		super(Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "configuration"),
				Component.translatable(ConfigSchema.langKey("configuration")), MIRROR);
	}

	static void register() {
		ConfigManager.getInstance().registerConfigHandler(new JupiterTweaksContainer());
	}

	static Screen create(Screen parent) {
		JupiterTweaksContainer container = new JupiterTweaksContainer();
		container.init();
		return JupiterScreen.getConfigScreen(parent, container, false);
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

	private net.minecraft.network.chat.MutableComponent label(ConfigOption option) {
		return Component.translatable(ConfigSchema.nameKey(option));
	}

	private net.minecraft.network.chat.MutableComponent tooltip(ConfigOption option) {
		return Component.translatable(ConfigSchema.tooltipKey(option));
	}

	private com.iafenvoy.jupiter.config.interfaces.ConfigEntry<?> entry(ConfigOption option) {
		String key = option.key();
		String current = FabricVanillaTweaks.current(key);
		net.minecraft.network.chat.Component name = label(option);
		net.minecraft.network.chat.Component tip = tooltip(option);
		return switch (option.kind()) {
			case BOOLEAN -> BooleanEntry.builder(name, Boolean.parseBoolean(current))
					.key(key)
					.tooltip(tip)
					.callback((chosen, initial, reset) -> pending.put(key, chosen.toString()))
					.build();
			case INTEGER -> IntegerEntry.builder(name, (int) Math.round(parseDouble(current, option.defaultInt())))
					.key(key)
					.min(option.intMin())
					.max(option.intMax())
					.tooltip(tip)
					.callback((chosen, initial, reset) -> pending.put(key, chosen.toString()))
					.build();
			case DECIMAL -> DoubleEntry.builder(name, parseDouble(current, option.defaultDouble()))
					.key(key)
					.min(option.min())
					.max(option.max())
					.tooltip(tip)
					.callback((chosen, initial, reset) -> pending.put(key, chosen.toString()))
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

	@Override
	public void load() {
		this.configTabs.clear();
		init();
	}

	@Override
	public void save() {
		try {
			Files.createDirectories(Path.of(MIRROR).getParent());
		} catch (IOException exception) {
			LOGGER.warn("[PracticalEnchantments] 无法创建 {} 的目录", MIRROR, exception);
		}
		super.save();
		if (this.pending.isEmpty()) {
			return;
		}
		Map<String, String> values = new LinkedHashMap<>(this.pending);
		this.pending.clear();
		FabricVanillaTweaks.save(values);
	}
}
