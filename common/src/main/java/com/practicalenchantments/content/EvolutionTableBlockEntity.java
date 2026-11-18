// 进化附魔台方块实体：青金石槽、台面物品、书架/附魔书计数与进化倒计时。
// 台面物品与青金石槽一起存进 NBT，退出重进不会丢。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.EvolutionApi;
import com.huziyang520.merlinlib.api.EvolutionRule;
import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EvolutionTableBlockEntity extends BlockEntity implements ItemOwner {
	public static final int MAX_SHELVES = 15;
	public static final int MAX_BOOKS = 30;
	public static final int MAX_STRENGTH = 30;
	public static final int DATA_SHELVES = 0;
	public static final int DATA_BOOKS = 1;
	public static final int DATA_STRENGTH = 2;
	public static final int DATA_COST = 3;
	public static final int DATA_COUNT = 4;
	private static final int SCAN_INTERVAL = 20;
	public static BlockEntityType<EvolutionTableBlockEntity> TYPE;

	private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
	private final SimpleContainer lapisView = new SimpleContainer(1);
	private final ContainerData data = new SimpleContainerData(DATA_COUNT);
	private int scanCooldown;
	private int workTicks;
	private int workTotal;

	public EvolutionTableBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(TYPE, worldPosition, blockState);
	}

	public Container lapisContainer() {
		return this.lapisView;
	}

	@Override
	public net.minecraft.world.level.Level level() {
		return this.level;
	}

	@Override
	public net.minecraft.world.phys.Vec3 position() {
		return net.minecraft.world.phys.Vec3.atCenterOf(this.getBlockPos());
	}

	@Override
	public float getVisualRotationYInDegrees() {
		return 0.0F;
	}

	public ContainerData data() {
		return this.data;
	}

	public ItemStack displayed() {
		return this.items.get(1);
	}

	public boolean hasDisplayed() {
		return !this.items.get(1).isEmpty();
	}

	public boolean placeDisplayed(Player player, ItemStack held) {
		if (held.isEmpty() || this.hasDisplayed() || this.workTotal > 0) {
			return false;
		}
		if (!held.is(Items.ENCHANTED_BOOK) && !held.has(net.minecraft.core.component.DataComponents.ENCHANTABLE)) {
			return false;
		}
		this.items.set(1, held.copyWithCount(1));
		held.shrink(1);
		this.setChanged();
		this.sync();
		return true;
	}

	public ItemStack takeDisplayed() {
		if (!this.hasDisplayed() || this.workTotal > 0) {
			return ItemStack.EMPTY;
		}
		ItemStack taken = this.items.get(1);
		this.items.set(1, ItemStack.EMPTY);
		this.setChanged();
		this.sync();
		return taken;
	}

	public void dropContents(Level level, BlockPos pos) {
		for (ItemStack stack : this.items) {
			if (!stack.isEmpty()) {
				ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack);
				level.addFreshEntity(entity);
			}
		}
		this.items.clear();
		this.lapisView.clearContent();
	}

	public int strength() {
		return this.data.get(DATA_STRENGTH);
	}

	public int shelves() {
		return this.data.get(DATA_SHELVES);
	}

	public int books() {
		return this.data.get(DATA_BOOKS);
	}

	public boolean isWorking() {
		return this.workTotal > 0;
	}

	private void sync() {
		if (this.level != null) {
			this.setChanged();
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
		}
	}

	public void serverTick() {
		if (!(this.level instanceof ServerLevel serverLevel)) {
			return;
		}
		if (this.workTotal > 0) {
			this.workTicks++;
			if (this.workTicks % 4 == 0) {
				serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getBlockPos().getX() + 0.5,
						this.getBlockPos().getY() + 1.1, this.getBlockPos().getZ() + 0.5,
						3, 0.2, 0.2, 0.2, 0.02);
			}
			if (this.workTicks >= this.workTotal) {
				this.finish(serverLevel);
			}
			return;
		}
		if (--this.scanCooldown <= 0) {
			this.scanCooldown = SCAN_INTERVAL;
			this.rescan(serverLevel);
		}
		if (this.canStart(serverLevel)) {
			this.start(serverLevel);
		}
	}

	private void rescan(Level level) {
		int shelves = 0;
		int books = 0;
		BlockPos origin = this.getBlockPos();
		for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
			BlockPos target = origin.offset(offset);
			BlockState targetState = level.getBlockState(target);
			boolean chiseled = targetState.is(Blocks.CHISELED_BOOKSHELF);
			if (!chiseled && !targetState.is(Blocks.BOOKSHELF)) {
				continue;
			}
			BlockPos middle = origin.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
			if (!level.isEmptyBlock(middle)) {
				continue;
			}
			if (chiseled && level.getBlockEntity(target) instanceof ChiseledBookShelfBlockEntity shelf) {
				int enchanted = 0;
				int stored = 0;
				for (int slot = 0; slot < ChiseledBookShelfBlockEntity.MAX_BOOKS_IN_STORAGE; slot++) {
					ItemStack book = shelf.getItem(slot);
					if (book.isEmpty()) {
						continue;
					}
					stored++;
					if (book.is(Items.ENCHANTED_BOOK)) {
						enchanted++;
					}
				}
				if (stored == 0) {
					continue;
				}
				shelves++;
				books += enchanted;
			} else {
				shelves++;
			}
		}
		shelves = Math.min(shelves, MAX_SHELVES);
		books = Math.min(books, MAX_BOOKS);
		boolean changed = shelves != this.data.get(DATA_SHELVES) || books != this.data.get(DATA_BOOKS);
		this.data.set(DATA_SHELVES, shelves);
		this.data.set(DATA_BOOKS, books);
		this.data.set(DATA_STRENGTH, Math.min(2 * shelves, books));
		EvolutionRule rule = this.hasDisplayed()
				? MerlinApi.evolution().match(this.items.get(1), level.registryAccess())
				: null;
		this.data.set(DATA_COST, rule == null ? 0 : rule.lapisCost());
		if (changed) {
			this.setChanged();
			this.sync();
		}
	}

	private boolean canStart(ServerLevel level) {
		if (!this.hasDisplayed() || this.strength() < 1) {
			return false;
		}
		EvolutionRule rule = MerlinApi.evolution().match(this.items.get(1), level.registryAccess());
		return rule != null && this.lapisView.getItem(0).getCount() >= rule.lapisCost();
	}

	private void start(ServerLevel level) {
		EvolutionRule rule = MerlinApi.evolution().match(this.items.get(1), level.registryAccess());
		if (rule == null) {
			return;
		}
		this.lapisView.removeItem(0, rule.lapisCost());
		this.workTicks = 0;
		this.workTotal = rule.ticks();
		this.setWorking(true);
		level.playSound(null, this.getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
	}

	private void finish(ServerLevel level) {
		EvolutionRule rule = MerlinApi.evolution().match(this.items.get(1), level.registryAccess());
		EvolutionApi evolution = MerlinApi.evolution();
		RandomSource random = level.getRandom();
		EvolutionApi.Outcome outcome = rule == null ? EvolutionApi.Outcome.FAILED
				: evolution.roll(rule, this.strength() / (float) MAX_STRENGTH, random);
		boolean applied = switch (outcome) {
			case EVOLVED -> evolution.applyEvolution(this.items.get(1), rule, level.registryAccess());
			case TRANSMUTED -> evolution.applyTransmutation(this.items.get(1), this.strength(),
					level.registryAccess(), random);
			case FAILED -> false;
		};
		if (outcome != EvolutionApi.Outcome.FAILED && applied) {
			level.playSound(null, this.getBlockPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.2F);
		} else {
			level.playSound(null, this.getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.6F, 0.6F);
		}
		this.workTotal = 0;
		this.workTicks = 0;
		this.setWorking(false);
		this.setChanged();
	}

	private void setWorking(boolean working) {
		if (this.level == null) {
			return;
		}
		BlockState state = this.getBlockState();
		if (state.getValue(EvolutionTableBlock.WORKING) != working) {
			this.level.setBlock(this.getBlockPos(), state.setValue(EvolutionTableBlock.WORKING, working), 3);
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.items, true);
		output.putInt("work_ticks", this.workTicks);
		output.putInt("work_total", this.workTotal);
		output.putInt("shelves", this.data.get(DATA_SHELVES));
		output.putInt("books", this.data.get(DATA_BOOKS));
		output.putInt("strength", this.data.get(DATA_STRENGTH));
		output.putInt("cost", this.data.get(DATA_COST));
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.items.clear();
		ContainerHelper.loadAllItems(input, this.items);
		this.lapisView.setItem(0, this.items.get(0));
		this.workTicks = input.getIntOr("work_ticks", 0);
		this.workTotal = input.getIntOr("work_total", 0);
		this.data.set(DATA_SHELVES, input.getIntOr("shelves", 0));
		this.data.set(DATA_BOOKS, input.getIntOr("books", 0));
		this.data.set(DATA_STRENGTH, input.getIntOr("strength", 0));
		this.data.set(DATA_COST, input.getIntOr("cost", 0));
		this.scanCooldown = 0;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		this.items.set(0, this.lapisView.getItem(0));
	}
}
