// Mod Menu 集成：给本模组在模组列表上加一个配置按钮，界面缺失时返回 null 让按钮消失。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.fabric.config.TweaksScreens;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> TweaksScreens.available() ? TweaksScreens.create(parent) : null;
	}
}
