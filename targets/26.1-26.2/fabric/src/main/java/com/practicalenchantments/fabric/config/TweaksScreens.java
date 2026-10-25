// 原版附魔调整的界面装配：Cloth Config 与 Jupiter 都可选，缺席时只保留配置文件。

package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TweaksScreens {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final String CLOTH_API = "me.shedaniel.clothconfig2.api.ConfigBuilder";
	private static final String JUPITER_API = "com.iafenvoy.jupiter.ConfigManager";
	private static volatile Opener opener;

	private TweaksScreens() {
	}

	@FunctionalInterface
	public interface Opener {
		Screen create(Screen parent);
	}

	public static void install() {
		if (present(CLOTH_API)) {
			opener = ClothTweaksScreen::create;
			LOGGER.info("[PracticalEnchantments] 发现 Cloth Config：原版附魔调整可在 Cloth 界面编辑");
		}
		if (present(JUPITER_API)) {
			JupiterTweaksContainer.register();
			if (opener == null) {
				opener = JupiterTweaksContainer::create;
			}
			LOGGER.info("[PracticalEnchantments] 发现 Jupiter：原版附魔调整已列入它的配置列表");
		}
		if (opener == null) {
			LOGGER.info("[PracticalEnchantments] 没有 Cloth Config 也没有 Jupiter，原版附魔调整只读配置文件");
		}
	}

	public static boolean available() {
		return opener != null;
	}

	public static Screen create(Screen parent) {
		Opener current = opener;
		return current == null ? null : current.create(parent);
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
