// 附魔书袋界面：格子取自贴图自身的物品栏格子，滚动条按实际行数显示。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.BookBagStorage;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BookBagScreen extends AbstractContainerScreen<BookBagMenu> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID,
			"textures/gui/book_bag.png");
	private static final int CELL_U = 14;
	private static final int CELL_V = 152;
	private static final int CELL_SIZE = 18;
	private static final int PANEL_COLOR = 0xFFC6C6C6;
	private static final int TRACK_X = 177;
	private static final int TRACK_WIDTH = 9;
	private static final int TRACK_COLOR = 0xFF373737;
	private static final int THUMB_COLOR = 0xFFC6C6C6;
	private static final int TEXT_COLOR = 0xFF404040;
	private static final int BUTTON_SCROLL_UP = 0;
	private static final int BUTTON_SCROLL_DOWN = 1;

	public BookBagScreen(BookBagMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 190, 236);
		this.inventoryLabelY = 141;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F,
				this.imageWidth, this.imageHeight, 256, 256);
		graphics.fill(this.leftPos + TRACK_X - 1, this.topPos + BookBagMenu.GRID_Y - 8,
				this.leftPos + TRACK_X + TRACK_WIDTH + 1, this.topPos + BookBagMenu.GRID_Y
						+ BookBagMenu.MAX_ROWS * CELL_SIZE + 9, PANEL_COLOR);
		this.drawCells(graphics);
		this.drawScrollBar(graphics);
	}

	private void drawCells(GuiGraphicsExtractor graphics) {
		int rows = this.menu.visibleRows();
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < 9; column++) {
				graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
						this.leftPos + BookBagMenu.GRID_X + column * CELL_SIZE,
						this.topPos + BookBagMenu.GRID_Y + row * CELL_SIZE,
						(float) CELL_U, (float) CELL_V, CELL_SIZE, CELL_SIZE, 256, 256);
			}
		}
	}

	private void drawScrollBar(GuiGraphicsExtractor graphics) {
		if (!this.menu.canScroll()) {
			return;
		}
		int x = this.leftPos + TRACK_X;
		int y = this.topPos + BookBagMenu.GRID_Y;
		int height = this.menu.visibleRows() * CELL_SIZE;
		graphics.fill(x, y, x + TRACK_WIDTH, y + height, TRACK_COLOR);
		int totalRows = this.menu.totalRows();
		int rows = this.menu.visibleRows();
		int thumbHeight = Math.max(12, height * rows / totalRows);
		int travel = height - thumbHeight;
		int maxOffset = Math.max(1, totalRows - rows);
		int thumbY = y + travel * this.menu.offsetRow() / maxOffset;
		graphics.fill(x + 1, thumbY, x + TRACK_WIDTH - 1, thumbY + thumbHeight, THUMB_COLOR);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractLabels(graphics, mouseX, mouseY);
		int used = 0;
		if (this.minecraft != null && this.minecraft.player != null) {
			used = BookBagStorage.usedSlots(this.minecraft.player.getMainHandItem());
		}
		Component count = Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".bag.count",
				used, this.menu.capacity());
		graphics.text(this.font, count, this.leftPos + this.imageWidth - 8 - this.font.width(count),
				this.topPos + 6, TEXT_COLOR);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (this.menu.canScroll() && this.minecraft != null && this.minecraft.gameMode != null) {
			int button = scrollY > 0 ? BUTTON_SCROLL_UP : BUTTON_SCROLL_DOWN;
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, button);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}
}
