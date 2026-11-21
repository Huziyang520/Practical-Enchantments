// 配置界面的接线：装了 Neo Configuration Screen Port 就把 mod 列表的配置按钮交给它，
// 没有则退回 Jupiter；两者都没有时只留 toml。

package com.practicalenchantments.config;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigUi {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final String PORT_REGISTRATION =
			"com.huziyang.neoconfigurationscreenport.forge.ConfigScreenRegistration";
	private static final String JUPITER_API = "com.iafenvoy.jupiter.ConfigManager";
	private static Object container;

	private ConfigUi() {
	}

	public static void install() {
		if (present(PORT_REGISTRATION)) {
			registerWithPort();
			LOGGER.info("[PracticalEnchantments] 找到 Neo Configuration Screen Port：配置界面由它渲染，"
					+ "值写入 practical_enchantments-server.toml");
			return;
		}
		if (present(JUPITER_API)) {
			container = JupiterSupport.createContainer();
			JupiterSupport.register(container);
			LOGGER.info("[PracticalEnchantments] 找到 Jupiter：三项开关列在 Jupiter 的配置列表里，值写入 "
					+ "practical_enchantments-server.toml");
			registerScreen();
			return;
		}
		LOGGER.info("[PracticalEnchantments] 既没有配置界面移植也没有 Jupiter：只读 "
				+ "practical_enchantments-server.toml，mod 列表里没有配置界面");
	}

	public static Screen create(Screen parent) {
		if (container != null) {
			return JupiterSupport.createScreen(parent, container);
		}
		return null;
	}

	private static void registerWithPort() {
		try {
			Class<?> registration = Class.forName(PORT_REGISTRATION);
			registration.getMethod("register", String.class).invoke(null, PracticalEnchantments.MOD_ID);
		} catch (ReflectiveOperationException | LinkageError failure) {
			LOGGER.warn("[PracticalEnchantments] 配置界面移植接管失败", failure);
		}
	}

	private static void registerScreen() {
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory(
						(minecraft, parent) -> create(parent)));
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
