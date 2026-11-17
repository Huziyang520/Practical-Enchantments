// 接管本模组菜单类型的屏幕创建：26.3 的 MenuScreens.register 是私有的，
// 直接拦截 MenuScreens#create 而不是往注册表里塞东西。

package com.practicalenchantments.fabric.client.mixin;

import com.practicalenchantments.client.EvolutionTableScreen;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.EvolutionTableMenu;
import com.practicalenchantments.fabric.client.PeFabricScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MenuScreens.class)
public class MenuScreensMixin {
	@Inject(method = "create", at = @At("HEAD"), cancellable = true)
	private static <T extends AbstractContainerMenu> void pe$create(MenuType<T> type, Minecraft minecraft, int windowId,
			Component title, CallbackInfo callback) {
		if (!PeFabricScreens.isEvolutionTable(type) || minecraft.player == null) {
			if (!PeFabricScreens.isBookBag(type) || minecraft.player == null) {
				return;
			}
			AbstractContainerMenu bagMenu = type.create(windowId, minecraft.player.getInventory());
			if (bagMenu instanceof BookBagMenu bag) {
				minecraft.setScreenAndShow(new com.practicalenchantments.client.BookBagScreen(bag,
						minecraft.player.getInventory(), title));
				callback.cancel();
			}
			return;
		}
		AbstractContainerMenu menu = type.create(windowId, minecraft.player.getInventory());
		if (menu instanceof EvolutionTableMenu evolution) {
			minecraft.setScreenAndShow(new EvolutionTableScreen(evolution, minecraft.player.getInventory(), title));
			callback.cancel();
		}
	}
}
