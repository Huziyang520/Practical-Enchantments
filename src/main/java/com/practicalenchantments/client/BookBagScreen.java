// 附魔书袋界面：面板高度随档位行数变化，格子取自贴图自身的物品栏格子。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.BookBagMenu;
import com.practicalenchantments.content.BookBagStorage;
import com.practicalenchantments.network.BagSearchSender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BookBagScreen extends AbstractContainerScreen<BookBagMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(PracticalEnchantments.MOD_ID,
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
		super(menu, inventory, title);
		this.imageWidth = PANEL_WIDTH;
		this.imageHeight = menu.panelHeight();
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
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
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

	private void blit(GuiGraphics graphics, int x, int y, int v, int height) {
		graphics.blit(TEXTURE, this.leftPos + x, this.topPos + y, 0, v, PANEL_WIDTH, height);
	}

	private void drawCells(GuiGraphics graphics, int rows) {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < 9; column++) {
				graphics.blit(TEXTURE,
						this.leftPos + BookBagMenu.GRID_X - 1 + column * BookBagMenu.CELL,
						this.topPos + BookBagMenu.GRID_Y - 1 + row * BookBagMenu.CELL,
						CELL_U, CELL_V, CELL_SOURCE, CELL_SOURCE);
			}
		}
	}

	private void drawScrollBar(GuiGraphics graphics, int rows, int top, int bottom) {
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
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		super.renderLabels(graphics, mouseX, mouseY);
		int used = 0;
		if (this.minecraft != null && this.minecraft.player != null) {
			used = BookBagStorage.usedSlots(this.minecraft.player.getMainHandItem());
		}
		Component count = Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".bag.count",
				used, this.menu.capacity());
		// renderLabels 的坐标系已经平移过 leftPos/topPos，这里只能写 GUI 内相对坐标。
		graphics.drawString(this.font, count,
				this.imageWidth - 8 - this.font.width(count), 6, TEXT_COLOR, false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// 搜索框有焦点时，E 是输入字符而不是关界面的快捷键。
		if (this.search != null && this.search.isFocused() && this.minecraft != null
				&& this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
			return false;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (this.menu.canScroll() && this.minecraft != null && this.minecraft.gameMode != null) {
			int button = delta > 0 ? BUTTON_SCROLL_UP : BUTTON_SCROLL_DOWN;
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, button);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}
}
