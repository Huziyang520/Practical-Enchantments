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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MenuScreens.class)
public abstract class MenuScreensMixin {
	// 必须照搬原版 fromPacket 的三步：建菜单、接管 containerMenu、再显示界面。
	// 只换屏不接管 containerMenu，客户端每帧都会把界面重开一次，表现为暗色遮罩层闪烁。
	@Inject(method = "create", at = @At("HEAD"), cancellable = true, require = 1)
	private static void practicalenchantments$create(MenuType<?> type, Minecraft minecraft, int windowId,
			Component title, CallbackInfo callback) {
		if (minecraft.player == null) {
			return;
		}
		if (type != PeMenus.evolutionTable() && type != PeMenus.bookBag()) {
			return;
		}
		practicalenchantments$trace(type, windowId);
		AbstractContainerMenu created = type.create(windowId, minecraft.player.getInventory());
		if (created instanceof EvolutionTableMenu menu && type == PeMenus.evolutionTable()) {
			EvolutionTableScreen screen = new EvolutionTableScreen(menu, minecraft.player.getInventory(), title);
			minecraft.player.containerMenu = screen.getMenu();
			minecraft.gui.setScreen(screen);
			callback.cancel();
		} else if (created instanceof BookBagMenu menu) {
			BookBagScreen screen = new BookBagScreen(menu, minecraft.player.getInventory(), title);
			minecraft.player.containerMenu = screen.getMenu();
			minecraft.gui.setScreen(screen);
			callback.cancel();
		}
	}

	// 临时诊断：确认客户端是否在重复创建界面（重复创建会导致遮罩层闪烁）。
	private static int practicalenchantments$creates;

	private static void practicalenchantments$trace(MenuType<?> type, int windowId) {
		practicalenchantments$creates++;
		com.practicalenchantments.PracticalEnchantments.info("[PE-DIAG] MenuScreens.create #{} type={} window={} screen={}",
				practicalenchantments$creates, net.minecraft.core.registries.BuiltInRegistries.MENU.getKey(type), windowId,
				net.minecraft.client.Minecraft.getInstance().gui.screen() == null ? "null"
						: net.minecraft.client.Minecraft.getInstance().gui.screen().getClass().getSimpleName());
	}
}
