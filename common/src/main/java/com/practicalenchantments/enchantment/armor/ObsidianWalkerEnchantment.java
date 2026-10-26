// 龙晶行者附魔 - 靴子单级，行走时把脚下的熔岩源凝成会自行碎裂的有瑕疵的黑曜石。

package com.practicalenchantments.enchantment.armor;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.PeContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class ObsidianWalkerEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":obsidian_walker";
	private static final int INTERVAL = 4;
	private static final int RADIUS = 1;

	private ObsidianWalkerEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.ENTITY_TICK, ObsidianWalkerEnchantment::onTick);
	}

	public static boolean wornBy(ServerLevel level, LivingEntity entity) {
		ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
		if (boots.isEmpty()) {
			return false;
		}
		Holder<Enchantment> holder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
				.get(ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse(ID)))
				.orElse(null);
		return holder != null && boots.getEnchantments().getLevel(holder) > 0;
	}

	private static void onTick(BuiltInEvents.EntityTickEvent event, EnchantmentContext ctx) {
		if (ctx.slot() != EquipmentSlot.FEET || (event.tickCount() & (INTERVAL - 1)) != 0) {
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
		for (BlockPos column : BlockPos.betweenClosed(origin.offset(-RADIUS, 0, -RADIUS), origin.offset(RADIUS, 0, RADIUS))) {
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
