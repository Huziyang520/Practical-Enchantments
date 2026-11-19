// 进化附魔台界面：原版容器风，只画贴图与标签，不叠加状态文字。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.EvolutionTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class EvolutionTableScreen extends AbstractContainerScreen<EvolutionTableMenu> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID,
			"textures/gui/evolution_table.png");

	public EvolutionTableScreen(EvolutionTableMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 166);
		this.inventoryLabelY = 72;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F,
				this.imageWidth, this.imageHeight, 256, 256);
	}

}
