// 可选配置界面的接线：有 Cloth Config 用 Cloth，没有再用 Jupiter，都没有就只留 toml。

package com.practicalenchantments.config;

import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigUi {
	private static final Logger LOGGER = LoggerFactory.getLogger("practical_enchantments");
	private static final String CLOTH_API = "me.shedaniel.clothconfig2.api.ConfigBuilder";
	private static final String JUPITER_API = "com.iafenvoy.jupiter.ConfigManager";
	private static JupiterContainer container;

	private ConfigUi() {
	}

	public static void install() {
		if (clothPresent()) {
			LOGGER.info("[PracticalEnchantments] 找到 Cloth Config：配置界面走 Cloth，值写入 "
					+ "practical_enchantments-server.toml");
			registerScreen();
			return;
		}
		if (jupiterPresent()) {
			container = new JupiterContainer();
			com.iafenvoy.jupiter.ConfigManager.getInstance().registerConfigHandler(container);
			LOGGER.info("[PracticalEnchantments] 找到 Jupiter：三项开关列在 Jupiter 的配置列表里，值写入 "
					+ "practical_enchantments-server.toml");
			registerScreen();
			return;
		}
		LOGGER.info("[PracticalEnchantments] 既没有 Cloth Config 也没有 Jupiter：只读 "
				+ "practical_enchantments-server.toml，mod 列表里没有配置界面");
	}

	public static Screen create(Screen parent) {
		if (clothPresent()) {
			return ClothScreen.create(parent);
		}
		if (jupiterPresent() && container != null) {
			return JupiterScreen.getConfigScreen(parent, container, false);
		}
		return null;
	}

	private static void registerScreen() {
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory(
						(minecraft, parent) -> create(parent)));
	}

	private static boolean clothPresent() {
		return present(CLOTH_API);
	}

	private static boolean jupiterPresent() {
		return present(JUPITER_API);
	}

	private static boolean present(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException | LinkageError missing) {
			return false;
		}
	}
}
