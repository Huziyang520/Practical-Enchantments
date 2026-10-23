package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 原版附魔调整的界面装配：Cloth Config 与 Jupiter 都可选，缺席时只保留配置文件。
 */
public final class TweaksScreens {

	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);

	/** Cloth Config 的判定类，按名字问，缺席时一个 Cloth 类都不会被解析。 */
	private static final String CLOTH_API = "me.shedaniel.clothconfig2.api.ConfigBuilder";
	/** Jupiter 的判定类。 */
	private static final String JUPITER_API = "com.iafenvoy.jupiter.ConfigManager";

	/** 供 Mod Menu 打开界面的入口；没有可用界面时为 null。 */
	private static volatile Opener opener;

	private TweaksScreens() {
	}

	/** 打开配置界面的形状。 */
	@FunctionalInterface
	public interface Opener {

		/**
		 * @param parent 返回时回到的界面
		 * @return 要显示的界面
		 */
		Screen create(Screen parent);
	}

	/** 装上可用的界面：Cloth 给 Mod Menu，Jupiter 进它自己的配置列表；两个都在时两处都能进。 */
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

	/** @return 是否有能打开的配置界面 */
	public static boolean available() {
		return opener != null;
	}

	/**
	 * @param parent 返回时回到的界面
	 * @return 配置界面，没有可用界面时为 null
	 */
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
