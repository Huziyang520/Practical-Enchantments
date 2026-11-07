// Cloth Config 界面：按分类各一个页签，数值项用带上下界的输入框。

package com.practicalenchantments.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ClothScreen {
	private ClothScreen() {
	}

	public static Screen create(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable(ConfigSchema.langKey("configuration")));
		ConfigEntryBuilder entries = builder.entryBuilder();
		Map<String, String> pending = new LinkedHashMap<>();
		for (String section : ConfigSchema.sections()) {
			ConfigCategory category = builder.getOrCreateCategory(
					Component.translatable(ConfigSchema.langKey(section)));
			String group = null;
			List<AbstractConfigListEntry> children = new ArrayList<>();
			for (ConfigOption option : ConfigSchema.in(section)) {
				String groupKey = option.parent();
				if (!groupKey.equals(group)) {
					if (group != null) {
						category.addEntry(subCategory(entries, section + "." + group, children));
						children = new ArrayList<>();
					}
					group = groupKey;
				}
				children.add(entry(entries, option, pending));
			}
			if (group != null) {
				if (group.isEmpty()) {
					for (AbstractConfigListEntry child : children) {
						category.addEntry(child);
					}
				} else {
					category.addEntry(subCategory(entries, section + "." + group, children));
				}
			}
		}
		builder.setSavingRunnable(() -> {
			if (pending.isEmpty()) {
				return;
			}
			Map<String, String> values = new LinkedHashMap<>(pending);
			pending.clear();
			ForgeConfig.write(values);
		});
		return builder.build();
	}


	@SuppressWarnings({"rawtypes", "unchecked"})
	private static AbstractConfigListEntry subCategory(ConfigEntryBuilder entries, String path,
			List<AbstractConfigListEntry> children) {
		Component label = Component.translatable(ConfigSchema.langKey(path));
		return entries.startSubCategory(label, children).build();
	}

	private static AbstractConfigListEntry<?> entry(ConfigEntryBuilder entries, ConfigOption option,
			Map<String, String> pending) {
		Component label = Component.translatable(ConfigSchema.nameKey(option));
		Component[] tooltip = {Component.translatable(ConfigSchema.tooltipKey(option))};
		String key = option.key();
		String current = ForgeConfig.current(key);
		return switch (option.kind()) {
			case BOOLEAN -> entries.startBooleanToggle(label, Boolean.parseBoolean(current))
					.setDefaultValue(option.defaultBool())
					.setTooltip(tooltip)
					.setSaveConsumer(chosen -> pending.put(key, chosen.toString()))
					.build();
			case INTEGER -> entries.startIntField(label, parseInt(current, option.defaultInt()))
					.setDefaultValue(option.defaultInt())
					.setMin(option.intMin())
					.setMax(option.intMax())
					.setTooltip(tooltip)
					.setSaveConsumer(chosen -> pending.put(key, chosen.toString()))
					.build();
			case DECIMAL -> entries.startDoubleField(label, parseDouble(current, option.defaultDouble()))
					.setDefaultValue(option.defaultDouble())
					.setMin(option.min())
					.setMax(option.max())
					.setTooltip(tooltip)
					.setSaveConsumer(chosen -> pending.put(key, chosen.toString()))
					.build();
		};
	}

	private static int parseInt(String text, int fallback) {
		try {
			return (int) Math.round(Double.parseDouble(text.trim()));
		} catch (NumberFormatException exception) {
			return fallback;
		}
	}

	private static double parseDouble(String text, double fallback) {
		try {
			return Double.parseDouble(text.trim());
		} catch (NumberFormatException exception) {
			return fallback;
		}
	}
}
