// 进化附魔台界面：一个只收青金石的槽，状态由 ContainerData 同步给客户端。

package com.practicalenchantments.content;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public class EvolutionTableMenu extends AbstractContainerMenu {
	private static final int LAPIS_SLOT = 0;
	private static final int LAPIS_X = 80;
	private static final int LAPIS_Y = 24;
	private static final int INVENTORY_X = 8;
	private static final int INVENTORY_Y = 84;
	private static final int HOTBAR_Y = 142;

	private final Container lapis;
	private final ContainerData data;
	@Nullable
	private final BlockEntity blockEntity;

	public EvolutionTableMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(1), new SimpleContainerData(EvolutionTableBlockEntity.DATA_COUNT),
				null);
	}

	public EvolutionTableMenu(int containerId, Inventory inventory, EvolutionTableBlockEntity table) {
		this(containerId, inventory, table.lapisContainer(), table.data(), table);
	}

	private EvolutionTableMenu(int containerId, Inventory inventory, Container lapis, ContainerData data,
			@Nullable BlockEntity blockEntity) {
		super(PeMenus.evolutionTable(), containerId);
		this.lapis = lapis;
		this.data = data;
		this.blockEntity = blockEntity;
		checkContainerSize(lapis, 1);
		this.addSlot(new Slot(lapis, LAPIS_SLOT, LAPIS_X, LAPIS_Y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(Items.LAPIS_LAZULI);
			}
		});
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(inventory, column + row * 9 + 9, INVENTORY_X + column * 18,
						INVENTORY_Y + row * 18));
			}
		}
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, HOTBAR_Y));
		}
		this.addDataSlots(data);
	}

	public int shelves() {
		return this.data.get(EvolutionTableBlockEntity.DATA_SHELVES);
	}

	public int books() {
		return this.data.get(EvolutionTableBlockEntity.DATA_BOOKS);
	}

	public int strength() {
		return this.data.get(EvolutionTableBlockEntity.DATA_STRENGTH);
	}

	public int lapisCost() {
		return this.data.get(EvolutionTableBlockEntity.DATA_COST);
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.blockEntity == null) {
			return true;
		}
		return player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(this.blockEntity.getBlockPos())) <= 64.0;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack moved = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			moved = stack.copy();
			if (index == LAPIS_SLOT) {
				if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (stack.is(Items.LAPIS_LAZULI)) {
				if (!this.moveItemStackTo(stack, LAPIS_SLOT, LAPIS_SLOT + 1, false)) {
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
		this.lapis.setChanged();
	}
}
