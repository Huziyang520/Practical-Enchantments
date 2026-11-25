// 燎原：穿戴者在地面实际水平移动时，在上一个脚部位置留下短命的原版火焰。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.support.WildfireTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;

public final class WildfireEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":wildfire";
	private static final Map<UUID, BlockPos> LAST_POS = new HashMap<>();

	private WildfireEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.ENTITY_TICK, WildfireEnchantment::onTick);
	}

	private static void onTick(BuiltInEvents.EntityTickEvent event, EnchantmentContext ctx) {
		if (!(event.entity() instanceof ServerPlayer player)) {
			return;
		}
		if (player.tickCount % Tunables.wildfireIntervalTicks() != 0) {
			return;
		}
		if (!player.onGround() || player.isShiftKeyDown() || player.isPassenger()
				|| player.isFallFlying() || player.isInWater()) {
			LAST_POS.remove(player.getUUID());
			return;
		}
		BlockPos current = player.blockPosition();
		BlockPos previous = LAST_POS.put(player.getUUID(), current);
		if (previous == null) {
			return;
		}
		double dx = current.getX() - previous.getX();
		double dz = current.getZ() - previous.getZ();
		if (Math.sqrt(dx * dx + dz * dz) < Tunables.wildfireMinMoveDistance()) {
			return;
		}
		place(event.level(), previous);
	}

	private static void place(ServerLevel level, BlockPos pos) {
		if (!level.isLoaded(pos) || !level.getBlockState(pos).isAir()
				|| level.getBlockState(pos.below()).isAir()) {
			return;
		}
		level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
		if (Tunables.wildfireBurnBlocks()) {
			return;
		}
		WildfireTracker.mark(level, pos, level.getGameTime() + Tunables.wildfireLifetimeTicks());
		level.scheduleTick(pos, Blocks.FIRE, 1);
	}
}
