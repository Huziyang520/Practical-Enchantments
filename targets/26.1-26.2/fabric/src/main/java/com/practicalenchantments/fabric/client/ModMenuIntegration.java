// Mod Menu 集成：配置按钮走 Forge Config API Port 的界面。

package com.practicalenchantments.fabric.client;

import com.practicalenchantments.PracticalEnchantments;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

@Environment(EnvType.CLIENT)
public final class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ConfigurationScreen(PracticalEnchantments.MOD_ID, parent);
	}
}
