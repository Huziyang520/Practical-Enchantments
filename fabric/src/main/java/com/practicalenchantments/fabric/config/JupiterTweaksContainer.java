// Jupiter 配置页：让本模组出现在 Jupiter 自己的配置列表里。

package com.practicalenchantments.fabric.config;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.container.FileConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.VanillaTweaks;
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
	private final Map<String, Boolean> pending = new LinkedHashMap<>();

	private JupiterTweaksContainer() {
		super(Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, VanillaTweaks.SECTION_KEY),
				Component.translatable(VanillaTweaks.SECTION_LANG_KEY), MIRROR);
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
		ConfigGroup group = createTab(VanillaTweaks.SECTION_KEY,
				Component.translatable(VanillaTweaks.SECTION_LANG_KEY));
		for (String key : FabricVanillaTweaks.KEYS) {
			group.addEntry(BooleanEntry.builder(Component.translatable(VanillaTweaks.nameKey(key)),
							VanillaTweaks.value(key))
					.key(key)
					.tooltip(Component.translatable(VanillaTweaks.tooltipKey(key)))
					.callback((chosen, initial, reset) -> this.pending.put(key, chosen))
					.build());
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
		Map<String, Boolean> values = new LinkedHashMap<>(this.pending);
		this.pending.clear();
		FabricVanillaTweaks.save(values);
	}
}
