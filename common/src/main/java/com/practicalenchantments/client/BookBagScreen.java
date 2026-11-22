// 附魔书袋界面：面板高度随档位行数变化，格子取自贴图自身的物品栏格子。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.BookBagStorage;
import com.practicalenchantments.network.BagSearchSender;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.components.EditBox;
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
	private static final int CELL_SOURCE = 18;
	private static final int PANEL_WIDTH = 190;
	private static final int STORAGE_V = 32;
	private static final int FOOTER_V = 140;
	private static final int TRACK_X = 177;
	private static final int TRACK_WIDTH = 8;
	private static final int ERASE_X = 176;
	private static final int ERASE_WIDTH = 11;
	private static final int LEFT_ERASE_X = 6;
	private static final int LEFT_ERASE_WIDTH = 8;
	private static final int PANEL_COLOR = 0xFFC6C6C6;
	private static final int TRACK_COLOR = 0xFF373737;
	private static final int THUMB_COLOR = 0xFFC6C6C6;
	private static final int TEXT_COLOR = 0xFF404040;
	private static final int BUTTON_SCROLL_UP = 0;
	private static final int BUTTON_SCROLL_DOWN = 1;
	private static final int SEARCH_X = 8;
	// 输入框内嵌区是贴图的 16..29 行；无边框 EditBox 直接把文字顶对齐到自身 y，故 y 取 19、高取 8。
	private static final int SEARCH_Y = 19;
	private static final int SEARCH_WIDTH = 174;
	private static final int SEARCH_HEIGHT = 8;

	private EditBox search;

	public BookBagScreen(BookBagMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, PANEL_WIDTH, menu.panelHeight());
		this.inventoryLabelY = menu.inventoryY() - 11;
	}

	@Override
	protected void init() {
		super.init();
		String previous = this.search == null ? "" : this.search.getValue();
		this.search = new EditBox(this.font, this.leftPos + SEARCH_X, this.topPos + SEARCH_Y,
				SEARCH_WIDTH, SEARCH_HEIGHT, Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".bag.search"));
		this.search.setMaxLength(64);
		this.search.setBordered(false);
		this.search.setValue(previous);
		this.search.setResponder(BagSearchSender::send);
		this.addRenderableWidget(this.search);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		int rows = this.menu.visibleRows();
		blit(graphics, 0, 0, 0, BookBagMenu.PANEL_HEADER);
		for (int row = 0; row < rows; row++) {
			blit(graphics, 0, BookBagMenu.PANEL_HEADER + row * BookBagMenu.CELL, STORAGE_V, BookBagMenu.CELL);
		}
		blit(graphics, 0, BookBagMenu.PANEL_HEADER + rows * BookBagMenu.CELL, FOOTER_V, BookBagMenu.PANEL_FOOTER);
		int gridTop = BookBagMenu.GRID_Y - 1;
		int gridBottom = gridTop + rows * BookBagMenu.CELL;
		graphics.fill(this.leftPos + LEFT_ERASE_X, this.topPos + gridTop,
				this.leftPos + LEFT_ERASE_X + LEFT_ERASE_WIDTH, this.topPos + gridBottom, PANEL_COLOR);
		graphics.fill(this.leftPos + ERASE_X, this.topPos + gridTop, this.leftPos + ERASE_X + ERASE_WIDTH,
				this.topPos + gridBottom, PANEL_COLOR);
		this.drawCells(graphics, rows);
		this.drawScrollBar(graphics, rows, gridTop, gridBottom);
	}

	private void blit(GuiGraphicsExtractor graphics, int x, int y, int v, int height) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + x, this.topPos + y,
				0.0F, (float) v, PANEL_WIDTH, height, 256, 256);
	}

	private void drawCells(GuiGraphicsExtractor graphics, int rows) {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < 9; column++) {
				graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
						this.leftPos + BookBagMenu.GRID_X - 1 + column * BookBagMenu.CELL,
						this.topPos + BookBagMenu.GRID_Y - 1 + row * BookBagMenu.CELL,
						(float) CELL_U, (float) CELL_V, CELL_SOURCE, CELL_SOURCE, 256, 256);
			}
		}
	}

	private void drawScrollBar(GuiGraphicsExtractor graphics, int rows, int top, int bottom) {
		if (!this.menu.canScroll()) {
			return;
		}
		int x = this.leftPos + TRACK_X;
		int height = bottom - top;
		graphics.fill(x, this.topPos + top, x + TRACK_WIDTH, this.topPos + bottom, TRACK_COLOR);
		int totalRows = this.menu.totalRows();
		int thumbHeight = Math.max(12, height * rows / totalRows);
		int travel = height - thumbHeight;
		int maxOffset = Math.max(1, totalRows - rows);
		int thumbY = this.topPos + top + travel * this.menu.offsetRow() / maxOffset;
		int thumbBottom = Math.min(this.topPos + bottom, thumbY + thumbHeight);
		graphics.fill(x + 1, thumbY, x + TRACK_WIDTH - 1, thumbBottom, THUMB_COLOR);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractLabels(graphics, mouseX, mouseY);
		Component count = Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".bag.count",
				this.usedSlots(), this.menu.capacity());
		graphics.text(this.font, count, this.imageWidth - 8 - this.font.width(count), 6, TEXT_COLOR, false);
	}

	private int usedSlots() {
		return this.minecraft == null || this.minecraft.player == null ? 0
				: BookBagStorage.usedSlots(this.minecraft.player.getMainHandItem());
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (this.search != null && this.search.isFocused()) {
			if (event.isEscape()) {
				return super.keyPressed(event);
			}
			if (this.getFocused() != null) {
				this.getFocused().keyPressed(event);
			}
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
		boolean handled = super.mouseClicked(event, doubleClick);
		if (this.search != null && this.search.isFocused()
				&& !this.search.isMouseOver(event.x(), event.y())) {
			this.setFocused(null);
		}
		return handled;
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
