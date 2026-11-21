// 附魔书袋菜单：可见行数随档位，支持按名字过滤（过滤结果由服务端重建并同步）。

package com.practicalenchantments.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.practicalenchantments.support.PinyinSearch;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class BookBagMenu extends AbstractContainerMenu {
	public static final int MAX_ROWS = 6;
	public static final int DATA_OFFSET_ROW = 0;
	public static final int DATA_TOTAL_ROWS = 1;
	public static final int DATA_CAPACITY = 2;
	private static final int DATA_COUNT = 3;
	public static final int GRID_X = 15;
	public static final int GRID_Y = 33;
	public static final int CELL = 18;
	public static final int PANEL_HEADER = 32;
	public static final int PANEL_FOOTER = 96;
	private static final int BUTTON_SCROLL_UP = 0;
	private static final int BUTTON_SCROLL_DOWN = 1;

	private final ItemStack bag;
	private final NonNullList<ItemStack> items;
	private final Container window;
	private final ContainerData data;
	private final int bagSlots;
	private final int bagRows;
	private final int inventoryY;
	private int[] visible = new int[0];
	private int matched;
	private String query = "";

	public BookBagMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, inventory.player.getMainHandItem());
	}

	private BookBagMenu(int containerId, Inventory inventory, ItemStack bag) {
		super(PeMenus.bookBag(), containerId);
		this.bag = bag;
		this.items = BookBagStorage.read(bag);
		int capacity = Math.max(9, this.items.size());
		int totalRows = Math.max(1, (capacity + 8) / 9);
		this.bagRows = Math.min(totalRows, MAX_ROWS);
		this.bagSlots = this.bagRows * 9;
		this.data = new SimpleContainerData(DATA_COUNT);
		this.data.set(DATA_OFFSET_ROW, 0);
		this.data.set(DATA_TOTAL_ROWS, totalRows);
		this.data.set(DATA_CAPACITY, capacity);
		this.inventoryY = GRID_Y + this.bagRows * CELL + 12;
		this.rebuildVisible();
		this.window = inventory.player.level().isClientSide() ? new SimpleContainer(this.bagSlots) : new WindowContainer();
		for (int index = 0; index < this.bagSlots; index++) {
			int column = index % 9;
			int row = index / 9;
			this.addSlot(new Slot(this.window, index, GRID_X + column * CELL, GRID_Y + row * CELL) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return BookBagStorage.mayStore(stack) && BookBagMenu.this.visibleIndex(this.getContainerSlot()) >= 0;
				}

				@Override
				public boolean mayPickup(Player player) {
					return BookBagMenu.this.visibleIndex(this.getContainerSlot()) >= 0;
				}
			});
		}
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(playerSlot(inventory, column + row * 9 + 9, GRID_X + column * CELL,
						this.inventoryY + row * CELL));
			}
		}
		for (int column = 0; column < 9; column++) {
			this.addSlot(playerSlot(inventory, column, GRID_X + column * CELL, this.inventoryY + 58));
		}
		this.addDataSlots(this.data);
	}

	/** 正在打开的书袋本体不可拿起：判据用物品类型，客户端槽位里是同步副本，比引用永远不相等。 */
	private Slot playerSlot(Inventory inventory, int index, int x, int y) {
		return new Slot(inventory, index, x, y) {
			@Override
			public boolean mayPickup(Player player) {
				return !BookBagMenu.this.isOpenBag(this.getItem());
			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return !BookBagMenu.this.isOpenBag(this.getItem());
			}
		};
	}

	private boolean isOpenBag(ItemStack stack) {
		return !stack.isEmpty() && !this.bag.isEmpty() && stack.is(this.bag.getItem());
	}

	@Override
	public void clicked(int slotId, int button, ClickType type, Player player) {
		if (slotId >= 0 && slotId < this.slots.size() && this.isOpenBag(this.slots.get(slotId).getItem())) {
			return;
		}
		super.clicked(slotId, button, type, player);
	}

	public void setQuery(String text) {
		String next = text == null ? "" : text.trim();
		if (next.equals(this.query)) {
			return;
		}
		this.query = next;
		this.data.set(DATA_OFFSET_ROW, 0);
		this.rebuildVisible();
		this.broadcastChanges();
	}

	public String query() {
		return this.query;
	}

	private void rebuildVisible() {
		List<Integer> found = new ArrayList<>();
		String needle = this.query;
		for (int index = 0; index < this.capacity(); index++) {
			if (needle.isEmpty()) {
				found.add(index);
				continue;
			}
			if (index >= this.items.size()) {
				continue;
			}
			ItemStack stack = this.items.get(index);
			if (!stack.isEmpty() && matches(stack, needle)) {
				found.add(index);
			}
		}
		this.visible = new int[found.size()];
		for (int index = 0; index < found.size(); index++) {
			this.visible[index] = found.get(index);
		}
		this.matched = found.size();
		this.data.set(DATA_TOTAL_ROWS, Math.max(1, (this.matched + 8) / 9));
	}

	private static boolean matches(ItemStack stack, String needle) {
		if (PinyinSearch.matches(stack.getHoverName().getString(), needle)) {
			return true;
		}
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		for (Enchantment enchantment : enchantments.keySet()) {
			Component name = Component.translatable(enchantment.getDescriptionId());
			if (PinyinSearch.matches(name.getString(), needle)) {
				return true;
			}
			ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
			if (id != null && id.toString().contains(needle)) {
				return true;
			}
		}
		return false;
	}

	private int visibleIndex(int slot) {
		int index = this.offsetRow() * 9 + slot;
		return index >= 0 && index < this.matched ? this.visible[index] : -1;
	}

	public int visibleRows() {
		return this.bagRows;
	}

	public int inventoryY() {
		return this.inventoryY;
	}

	public int panelHeight() {
		return PANEL_HEADER + this.bagRows * CELL + PANEL_FOOTER;
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

	public int visibleSlotCount() {
		return this.matched;
	}

	public boolean canScroll() {
		return this.totalRows() > this.visibleRows();
	}

	public void requestScroll(int deltaRows) {
		if (!this.canScroll()) {
			return;
		}
		int target = Math.max(0, Math.min(this.totalRows() - this.visibleRows(), this.offsetRow() + deltaRows));
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

	@Override
	public boolean stillValid(Player player) {
		return BookBagStorage.isBag(this.bag) && player.getMainHandItem() == this.bag;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		if (!this.query.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack moved = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			moved = stack.copy();
			if (index < this.bagSlots) {
				if (!this.moveItemStackTo(stack, this.bagSlots, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (BookBagStorage.mayStore(stack)) {
				if (!this.moveItemStackTo(stack, 0, this.bagSlots, false)) {
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

		@Override
		public int getContainerSize() {
			return BookBagMenu.this.bagSlots;
		}

		@Override
		public boolean isEmpty() {
			for (int index = 0; index < BookBagMenu.this.bagSlots; index++) {
				if (!this.getItem(index).isEmpty()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public ItemStack getItem(int slot) {
			int absolute = BookBagMenu.this.visibleIndex(slot);
			if (absolute < 0 || absolute >= BookBagMenu.this.items.size()) {
				return ItemStack.EMPTY;
			}
			return BookBagMenu.this.items.get(absolute);
		}

		@Override
		public ItemStack removeItem(int slot, int amount) {
			int absolute = BookBagMenu.this.visibleIndex(slot);
			if (absolute < 0 || absolute >= BookBagMenu.this.items.size()) {
				return ItemStack.EMPTY;
			}
			ItemStack removed = ContainerHelper.removeItem(BookBagMenu.this.items, absolute, amount);
			this.setChanged();
			return removed;
		}

		@Override
		public ItemStack removeItemNoUpdate(int slot) {
			int absolute = BookBagMenu.this.visibleIndex(slot);
			if (absolute < 0 || absolute >= BookBagMenu.this.items.size()) {
				return ItemStack.EMPTY;
			}
			ItemStack removed = BookBagMenu.this.items.get(absolute);
			BookBagMenu.this.items.set(absolute, ItemStack.EMPTY);
			return removed;
		}

		@Override
		public void setItem(int slot, ItemStack stack) {
			int absolute = BookBagMenu.this.visibleIndex(slot);
			if (absolute < 0 || absolute >= BookBagMenu.this.items.size()) {
				return;
			}
			BookBagMenu.this.items.set(absolute, stack);
			this.setChanged();
		}

		@Override
		public void setChanged() {
			BookBagStorage.write(BookBagMenu.this.bag, BookBagMenu.this.items);
			BookBagMenu.this.rebuildVisible();
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
