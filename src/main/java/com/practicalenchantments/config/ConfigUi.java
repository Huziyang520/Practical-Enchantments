package com.practicalenchantments.config;

import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可选配置界面的接线：有 Cloth Config 用 Cloth，没有再用 Jupiter，都没有就只留 toml。
 */
public final class ConfigUi {

	private static final Logger LOGGER = LoggerFactory.getLogger("practical_enchantments");

	/** 证明 Cloth Config 在场的类，按名字询问，不做静态链接。 */
	private static final String CLOTH_API = "me.shedaniel.clothconfig2.api.ConfigBuilder";
	/** 证明 Jupiter 在场的类。 */
	private static final String JUPITER_API = "com.iafenvoy.jupiter.ConfigManager";

	/** 注册给 Jupiter 的容器，点「配置」按钮时再显示一次。 */
	private static JupiterContainer container;

	private ConfigUi() {
	}

	/** 装上第一个可用的界面构建器，并挂上 mod 列表里的「配置」按钮。 */
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

	/**
	 * 构建界面。
	 *
	 * @param parent 返回时回到的界面，{@code null} 表示回到游戏
	 * @return 要显示的界面，两种界面都不在时返回 {@code null}
	 */
	public static Screen create(Screen parent) {
		if (clothPresent()) {
			return ClothScreen.create(parent);
		}
		if (jupiterPresent() && container != null) {
			return JupiterScreen.getConfigScreen(parent, container, false);
		}
		return null;
	}

	/** 把界面挂到 mod 列表的「配置」按钮上；没有界面可用时不挂。 */
	private static void registerScreen() {
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory(
						(minecraft, parent) -> create(parent)));
	}

	/** @return Cloth Config 是否在 classpath 上 */
	private static boolean clothPresent() {
		return present(CLOTH_API);
	}

	/** @return Jupiter 是否在 classpath 上 */
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
