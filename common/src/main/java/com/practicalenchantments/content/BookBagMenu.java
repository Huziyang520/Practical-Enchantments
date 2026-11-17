// 附魔书袋菜单：可见 6 行 × 9 格，滚动由 clickMenuButton 推进，内容整表写回 CONTAINER 组件。

package com.practicalenchantments.content;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class BookBagMenu extends AbstractContainerMenu {
	public static final int VISIBLE_ROWS = 6;
	public static final int VISIBLE_SLOTS = VISIBLE_ROWS * 9;
	public static final int DATA_OFFSET_ROW = 0;
	public static final int DATA_TOTAL_ROWS = 1;
	public static final int DATA_CAPACITY = 2;
	private static final int DATA_COUNT = 3;
	private static final int GRID_X = 8;
	private static final int GRID_Y = 33;
	private static final int INVENTORY_X = 15;
	private static final int INVENTORY_Y = 153;
	private static final int HOTBAR_Y = 211;
	private static final int BUTTON_SCROLL_UP = 0;
	private static final int BUTTON_SCROLL_DOWN = 1;

	private final ItemStack bag;
	private final NonNullList<ItemStack> items;
	private final Container window;
	private final ContainerData data;

	public BookBagMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, inventory.player.getMainHandItem());
	}

	private BookBagMenu(int containerId, Inventory inventory, ItemStack bag) {
		super(PeMenus.bookBag(), containerId);
		this.bag = bag;
		this.items = BookBagStorage.read(bag);
		int capacity = Math.max(VISIBLE_SLOTS, this.items.size());
		int totalRows = Math.max(VISIBLE_ROWS, (capacity + 8) / 9);
		this.data = new SimpleContainerData(DATA_COUNT);
		this.data.set(DATA_OFFSET_ROW, 0);
		this.data.set(DATA_TOTAL_ROWS, totalRows);
		this.data.set(DATA_CAPACITY, capacity);
		this.window = inventory.player.level().isClientSide() ? new SimpleContainer(VISIBLE_SLOTS) : new WindowContainer();
		for (int index = 0; index < VISIBLE_SLOTS; index++) {
			int column = index % 9;
			int row = index / 9;
			this.addSlot(new Slot(this.window, index, GRID_X + column * 18, GRID_Y + row * 18) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return BookBagStorage.mayStore(stack);
				}

				@Override
				public boolean mayPickup(Player player) {
					return BookBagMenu.this.withinCapacity(this.getContainerSlot());
				}
			});
		}
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(inventory, column + row * 9 + 9, INVENTORY_X + column * 18,
						INVENTORY_Y + row * 18));
			}
		}
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, HOTBAR_Y));
		}
		this.addDataSlots(this.data);
	}

	public int offsetRow() {
		return this.data.get(DATA_OFFSET_ROW);
	}

	public int totalRows() {
		return this.data.get(DATA_TOTAL_ROWS);
	}

	public int capacity() {
		return this.data.get(DATA_CAPACITY);
	}

	public boolean canScroll() {
		return this.totalRows() > VISIBLE_ROWS;
	}

	public void requestScroll(int deltaRows) {
		if (!this.canScroll()) {
			return;
		}
		int target = clampOffset(this.offsetRow() + deltaRows);
		this.data.set(DATA_OFFSET_ROW, target);
		if (this.window instanceof WindowContainer container) {
			container.setOffsetRow(target);
		}
		this.broadcastChanges();
	}

	@Override
	public boolean clickMenuButton(Player player, int buttonId) {
		if (buttonId == BUTTON_SCROLL_UP) {
			this.requestScroll(-1);
			return true;
		}
		if (buttonId == BUTTON_SCROLL_DOWN) {
			this.requestScroll(1);
			return true;
		}
		return false;
	}

	private int clampOffset(int row) {
		return Math.max(0, Math.min(this.totalRows() - VISIBLE_ROWS, row));
	}

	private boolean withinCapacity(int slotIndex) {
		return this.offsetRow() * 9 + slotIndex < this.capacity();
	}

	@Override
	public boolean stillValid(Player player) {
		return BookBagStorage.isBag(this.bag) && player.getMainHandItem() == this.bag;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack moved = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			moved = stack.copy();
			if (index < VISIBLE_SLOTS) {
				if (!this.moveItemStackTo(stack, VISIBLE_SLOTS, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (BookBagStorage.mayStore(stack)) {
				if (!this.moveItemStackTo(stack, 0, VISIBLE_SLOTS, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				return ItemStack.EMPTY;
			}
			if (stack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return moved;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!player.level().isClientSide() && BookBagStorage.isBag(this.bag)) {
			BookBagStorage.write(this.bag, this.items);
			player.getInventory().setChanged();
		}
	}

	private final class WindowContainer implements Container {
		private int offsetRow;

		void setOffsetRow(int row) {
			this.offsetRow = row;
		}

		private int absolute(int slot) {
			return this.offsetRow * 9 + slot;
		}

		@Override
		public int getContainerSize() {
			return VISIBLE_SLOTS;
		}

		@Override
		public boolean isEmpty() {
			for (int index = 0; index < VISIBLE_SLOTS; index++) {
				if (!this.getItem(index).isEmpty() && absolute(index) < BookBagMenu.this.capacity()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public ItemStack getItem(int slot) {
			int absolute = absolute(slot);
			if (absolute >= BookBagMenu.this.capacity() || absolute >= BookBagMenu.this.items.size()) {
				return ItemStack.EMPTY;
			}
			return BookBagMenu.this.items.get(absolute);
		}

		@Override
		public ItemStack removeItem(int slot, int amount) {
			int absolute = absolute(slot);
			if (absolute >= BookBagMenu.this.items.size()) {
				return ItemStack.EMPTY;
			}
			ItemStack removed = net.minecraft.world.ContainerHelper.removeItem(BookBagMenu.this.items, absolute, amount);
			this.setChanged();
			return removed;
		}

		@Override
		public ItemStack removeItemNoUpdate(int slot) {
			int absolute = absolute(slot);
			if (absolute >= BookBagMenu.this.items.size()) {
				return ItemStack.EMPTY;
			}
			ItemStack removed = BookBagMenu.this.items.get(absolute);
			BookBagMenu.this.items.set(absolute, ItemStack.EMPTY);
			return removed;
		}

		@Override
		public void setItem(int slot, ItemStack stack) {
			int absolute = absolute(slot);
			if (absolute >= BookBagMenu.this.items.size()) {
				return;
			}
			BookBagMenu.this.items.set(absolute, stack);
			this.setChanged();
		}

		@Override
		public void setChanged() {
			BookBagStorage.write(BookBagMenu.this.bag, BookBagMenu.this.items);
		}

		@Override
		public boolean stillValid(Player player) {
			return BookBagMenu.this.stillValid(player);
		}

		@Override
		public void clearContent() {
			for (int index = 0; index < BookBagMenu.this.items.size(); index++) {
				BookBagMenu.this.items.set(index, ItemStack.EMPTY);
			}
			this.setChanged();
		}
	}

	public List<ItemStack> snapshot() {
		return List.copyOf(this.items);
	}
}
