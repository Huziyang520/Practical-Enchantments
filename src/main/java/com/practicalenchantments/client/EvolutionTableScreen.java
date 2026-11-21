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
	private static final ResourceLocation TEXTURE = new ResourceLocation(PracticalEnchantments.MOD_ID,
			"textures/gui/evolution_table.png");

	public EvolutionTableScreen(EvolutionTableMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.inventoryLabelY = 72;
	}

	// 与原版 ContainerScreen 相同：容器界面要自己画背景遮罩，并在最后画悬停提示。
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}
}
