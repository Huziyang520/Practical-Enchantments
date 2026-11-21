// MenuScreens.register 是 private，容器界面改在 create 里接管。

package com.practicalenchantments.fabric.client.mixin;

import com.practicalenchantments.client.BookBagScreen;
import com.practicalenchantments.client.EvolutionTableScreen;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.EvolutionTableMenu;
import com.practicalenchantments.content.PeMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MenuScreens.class)
public abstract class MenuScreensMixin {
	@Inject(method = "create", at = @At("HEAD"), cancellable = true, require = 1)
	private static void practicalenchantments$create(MenuType<?> type, Minecraft minecraft, int windowId,
			Component title, CallbackInfo callback) {
		if (minecraft.player == null) {
			return;
		}
		if (type == PeMenus.evolutionTable()) {
			EvolutionTableMenu menu = new EvolutionTableMenu(windowId, minecraft.player.getInventory());
			minecraft.setScreenAndShow(new EvolutionTableScreen(menu, minecraft.player.getInventory(), title));
			callback.cancel();
		} else if (type == PeMenus.bookBag()) {
			BookBagMenu menu = new BookBagMenu(windowId, minecraft.player.getInventory());
			minecraft.setScreenAndShow(new BookBagScreen(menu, minecraft.player.getInventory(), title));
			callback.cancel();
		}
	}
}
