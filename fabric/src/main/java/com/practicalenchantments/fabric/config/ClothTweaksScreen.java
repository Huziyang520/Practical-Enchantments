package com.practicalenchantments.fabric.config;

import com.practicalenchantments.config.VanillaTweaks;
import java.util.LinkedHashMap;
import java.util.Map;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Cloth Config 界面：一个页签、三个开关，保存时交给 {@link FabricVanillaTweaks}。
 */
final class ClothTweaksScreen {

	private ClothTweaksScreen() {
	}

	static Screen create(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable(VanillaTweaks.SECTION_LANG_KEY));
		ConfigEntryBuilder entries = builder.entryBuilder();
		ConfigCategory category = builder.getOrCreateCategory(
				Component.translatable(VanillaTweaks.SECTION_LANG_KEY));
		Map<String, Boolean> pending = new LinkedHashMap<>();
		for (String key : FabricVanillaTweaks.KEYS) {
			category.addEntry(toggle(entries, key, pending));
		}
		builder.setSavingRunnable(() -> FabricVanillaTweaks.save(pending));
		return builder.build();
	}

	private static AbstractConfigListEntry<?> toggle(ConfigEntryBuilder entries, String key,
													Map<String, Boolean> pending) {
		return entries.startBooleanToggle(Component.translatable(VanillaTweaks.nameKey(key)),
						VanillaTweaks.value(key))
				.setDefaultValue(VanillaTweaks.defaultValue(key))
				.setTooltip(Component.translatable(VanillaTweaks.tooltipKey(key)))
				.setSaveConsumer(chosen -> pending.put(key, chosen))
				.build();
	}
}
