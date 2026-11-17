// Mod Menu 集成：给本模组在模组列表上加一个配置按钮，界面缺失时返回 null 让按钮消失。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.fabric.config.TweaksScreens;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

@Environment(EnvType.CLIENT)
public final class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			try {
				return new ConfigurationScreen(PracticalEnchantments.MOD_ID, parent);
			} catch (Throwable missingPort) {
				return TweaksScreens.available() ? TweaksScreens.create(parent) : null;
			}
		};
	}
}
