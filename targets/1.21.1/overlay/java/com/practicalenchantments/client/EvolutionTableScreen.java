// 进化附魔台界面：原版容器风，只画贴图与标签，不叠加状态文字。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.EvolutionTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EvolutionTableScreen extends AbstractContainerScreen<EvolutionTableMenu> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
			PracticalEnchantments.MOD_ID, "textures/gui/evolution_table.png");

	public EvolutionTableScreen(EvolutionTableMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.inventoryLabelY = 72;
	}

	// 1.21.1 的 AbstractContainerScreen 不画悬停提示，每个容器界面自己收尾。
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F,
				this.imageWidth, this.imageHeight, 256, 256);
	}
}
