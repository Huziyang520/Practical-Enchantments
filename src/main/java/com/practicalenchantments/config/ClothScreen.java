package com.practicalenchantments.config;

import java.util.LinkedHashMap;
import java.util.Map;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Cloth Config 版「原版附魔调整」界面，键与文案取自 {@link VanillaTweaks}。
 */
public final class ClothScreen {

	private ClothScreen() {
	}

	/**
	 * 构建界面。
	 *
	 * @param parent 返回时回到的界面，{@code null} 表示回到游戏
	 * @return 要显示的界面
	 */
	public static Screen create(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable(VanillaTweaks.SECTION_LANG_KEY));
		ConfigEntryBuilder entries = builder.entryBuilder();
		ConfigCategory category = builder.getOrCreateCategory(
				Component.translatable(VanillaTweaks.SECTION_LANG_KEY));
		Map<String, Boolean> pending = new LinkedHashMap<>();

		for (String key : ForgeConfig.KEYS) {
			category.addEntry(entries.startBooleanToggle(Component.translatable(VanillaTweaks.nameKey(key)),
							VanillaTweaks.value(key))
					.setDefaultValue(VanillaTweaks.defaultValue(key))
					.setTooltip(Component.translatable(VanillaTweaks.tooltipKey(key)))
					.setSaveConsumer(chosen -> pending.put(key, chosen))
					.build());
		}

		builder.setSavingRunnable(() -> {
			if (pending.isEmpty()) {
				return;
			}
			Map<String, Boolean> values = new LinkedHashMap<>(pending);
			pending.clear();
			ForgeConfig.write(values);
		});
		return builder.build();
	}
}
