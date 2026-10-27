// Jupiter 可选前置的唯一接触点：只有确认 Jupiter 存在时才会加载本类。

package com.practicalenchantments.config;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import net.minecraft.client.gui.screens.Screen;

public final class JupiterSupport {
	private JupiterSupport() {
	}

	public static Object createContainer() {
		return new JupiterContainer();
	}

	public static void register(Object container) {
		if (container instanceof JupiterContainer jupiterContainer) {
			ConfigManager.getInstance().registerConfigHandler(jupiterContainer);
		}
	}

	public static Screen createScreen(Screen parent, Object container) {
		if (container instanceof JupiterContainer jupiterContainer) {
			return JupiterScreen.getConfigScreen(parent, jupiterContainer, false);
		}
		return null;
	}
}
