// 附魔书袋界面：存储终端风，一屏 6 行，滚轮翻页，右侧自绘滚动条。

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
	private static final int TRACK_X = 173;
	private static final int TRACK_Y = 33;
	private static final int TRACK_WIDTH = 6;
	private static final int TRACK_HEIGHT = 106;
	private static final int TRACK_COLOR = 0xFF373737;
	private static final int THUMB_COLOR = 0xFFB4B4B4;
	private static final int TEXT_COLOR = 0xFF404040;
	private static final int SLOT_BORDER_COLOR = 0xFF8B8B8B;
	private static final int SLOT_FILL_COLOR = 0xFF373737;
	private static final int BUTTON_SCROLL_UP = 0;
	private static final int BUTTON_SCROLL_DOWN = 1;

	public BookBagScreen(BookBagMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 190, 236);
		this.inventoryLabelY = 143;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F,
				this.imageWidth, this.imageHeight, 256, 256);
		this.drawSlots(graphics);
		this.drawScrollBar(graphics);
	}

	private void drawSlots(GuiGraphicsExtractor graphics) {
		for (int row = 0; row < BookBagMenu.VISIBLE_ROWS; row++) {
			for (int column = 0; column < 9; column++) {
				int x = this.leftPos + BookBagMenu.GRID_X + column * 18;
				int y = this.topPos + BookBagMenu.GRID_Y + row * 18;
				graphics.fill(x, y, x + 18, y + 18, SLOT_BORDER_COLOR);
				graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_FILL_COLOR);
			}
		}
	}

	private void drawScrollBar(GuiGraphicsExtractor graphics) {
		if (!this.menu.canScroll()) {
			return;
		}
		int x = this.leftPos + TRACK_X;
		int y = this.topPos + TRACK_Y;
		graphics.fill(x, y, x + TRACK_WIDTH, y + TRACK_HEIGHT, TRACK_COLOR);
		int totalRows = this.menu.totalRows();
		int thumbHeight = Math.max(12, TRACK_HEIGHT * BookBagMenu.VISIBLE_ROWS / totalRows);
		int travel = TRACK_HEIGHT - thumbHeight;
		int maxOffset = Math.max(1, totalRows - BookBagMenu.VISIBLE_ROWS);
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
		graphics.text(this.font, Component.translatable("gui." + PracticalEnchantments.MOD_ID + ".bag.count",
				used, this.menu.capacity()), this.leftPos + 10, this.topPos + 6, TEXT_COLOR);
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
