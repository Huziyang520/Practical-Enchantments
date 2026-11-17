// 进化附魔台界面：原版容器风，三行状态 + 一行操作提示。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
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
	private static final int TEXT_COLOR = 0xFF404040;
	private static final int WARN_COLOR = 0xFF8A2B2B;
	private static final int STATE_X = 8;
	private static final int STATE_Y = 46;

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

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractLabels(graphics, mouseX, mouseY);
		int x = this.leftPos + STATE_X;
		int y = this.topPos + STATE_Y;
		graphics.text(this.font, Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".evolution.shelves",
				this.menu.shelves(), EvolutionTableBlockEntity.MAX_SHELVES,
				this.menu.books(), EvolutionTableBlockEntity.MAX_BOOKS), x, y, TEXT_COLOR);
		graphics.text(this.font, Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".evolution.strength",
				this.menu.strength()), x, y + 10, TEXT_COLOR);
		if (this.menu.lapisCost() > 0) {
			graphics.text(this.font, Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".evolution.cost",
					this.menu.lapisCost()), x, y + 20, TEXT_COLOR);
		} else {
			graphics.text(this.font, Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".evolution.idle"),
					x, y + 20, WARN_COLOR);
		}
	}
}
