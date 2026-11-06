// 龙晶行者附魔 - 靴子单级，行走时把脚下的熔岩源凝成会自行碎裂的有瑕疵的黑曜石。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.PeContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class ObsidianWalkerEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":obsidian_walker";
	
	private ObsidianWalkerEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
				BuiltInEvents.ENTITY_TICK, ObsidianWalkerEnchantment::onTick);
	}

	public static boolean wornBy(LivingEntity entity) {
		ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
		if (boots.isEmpty()) {
			return false;
		}
		Enchantment enchantment = PracticalEnchantments.resolveEnchantment(ID);
		return EnchantmentHelper.getEnchantments(boots).getOrDefault(enchantment, 0) > 0;
	}

	private static void onTick(BuiltInEvents.EntityTickEvent event, EnchantmentContext ctx) {
		if (ctx.slot() != EquipmentSlot.FEET || event.tickCount() % Tunables.obsidianWalkerIntervalTicks() != 0) {
			return;
		}
		LivingEntity entity = event.entity();
		if (!entity.onGround() || entity.isInLava()) {
			return;
		}
		ServerLevel level = event.level();
		BlockPos feet = entity.blockPosition();
		BlockState solidified = PeContent.block(PeContent.FLAWED_OBSIDIAN).defaultBlockState();
		if (!solidifyLayer(level, feet.getY(), feet, solidified)) {
			solidifyLayer(level, feet.getY() - 1, feet, solidified);
		}
	}

	private static boolean solidifyLayer(ServerLevel level, int y, BlockPos origin, BlockState solidified) {
		boolean changed = false;
		int radius = Tunables.obsidianWalkerRadius();
		int radiusSqr = radius * radius;
		for (BlockPos column : BlockPos.betweenClosed(origin.offset(-radius, 0, -radius), origin.offset(radius, 0, radius))) {
			int dx = column.getX() - origin.getX();
			int dz = column.getZ() - origin.getZ();
			if (dx * dx + dz * dz > radiusSqr) {
				continue;
			}
			BlockPos target = new BlockPos(column.getX(), y, column.getZ());
			if (!level.isEmptyBlock(target.above())) {
				continue;
			}
			FluidState fluid = level.getFluidState(target);
			if (!fluid.is(FluidTags.LAVA) || !fluid.isSource()) {
				continue;
			}
			level.setBlock(target, solidified, Block.UPDATE_ALL);
			changed = true;
		}
		return changed;
	}
}
