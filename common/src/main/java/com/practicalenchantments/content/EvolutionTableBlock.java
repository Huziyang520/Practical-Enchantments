// 进化附魔台：12/16 高的台座，右键手持物品放上台面，空手右键打开界面。
// 没有飘在空中的书，台面只显示玩家放上去的那件物品。

package com.practicalenchantments.content;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Prediction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class EvolutionTableBlock extends Block implements EntityBlock {
	public static final BooleanProperty WORKING = BooleanProperty.create("working");
	private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 12.0);

	public EvolutionTableBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(WORKING, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WORKING);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return new EvolutionTableBlockEntity(worldPosition, blockState);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
			BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return null;
		}
		return (tickLevel, pos, state, blockEntity) -> {
			if (blockEntity instanceof EvolutionTableBlockEntity table) {
				table.serverTick();
			}
		};
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof EvolutionTableBlockEntity table)) {
			return InteractionResult.PASS;
		}
		ItemStack held = player.getMainHandItem();
		boolean openOnly = player.isShiftKeyDown();
		if (!openOnly && !held.isEmpty() && !table.hasDisplayed()) {
			if (!level.isClientSide()) {
				if (!table.placeDisplayed(player, held)) {
					return InteractionResult.PASS;
				}
			}
			return InteractionResult.SUCCESS;
		}
		if (!openOnly && held.isEmpty() && table.hasDisplayed()) {
			if (!level.isClientSide()) {
				ItemStack taken = table.takeDisplayed();
				if (!taken.isEmpty()) {
					player.getInventory().placeItemBackInInventory(taken, Prediction.PREDICTED);
				}
			}
			return InteractionResult.SUCCESS;
		}
		if (!level.isClientSide()) {
			if (PeMenus.evolutionTable() == null) {
				return InteractionResult.PASS;
			}
			player.openMenu(new SimpleMenuProvider(
					(containerId, inventory, menuPlayer) -> new EvolutionTableMenu(containerId, inventory, table),
					Component.translatable("container." + PracticalEnchantments.MOD_ID + ".evolution_table")));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level,
			BlockPos pos, boolean movedByPiston) {
		if (level.getBlockEntity(pos) instanceof EvolutionTableBlockEntity table) {
			table.dropContents(level, pos);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!state.getValue(WORKING)) {
			return;
		}
		level.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8,
				pos.getY() + 1.0, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8, 0.0, 0.05, 0.0);
	}
}
