// 龙晶行者附魔 - 靴子单级，站在熔岩源上时把脚下的熔岩凝固成有瑕疵的黑曜石。

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
		if (ctx.slot() != EquipmentSlot.FEET) {
			return;
		}
		ServerLevel level = event.level();
		LivingEntity entity = event.entity();
		BlockState solidified = PeContent.block(PeContent.FLAWED_OBSIDIAN).defaultBlockState();
		BlockPos feet = entity.blockPosition();
		solidify(level, feet, solidified);
		solidify(level, feet.below(), solidified);
	}

	private static void solidify(ServerLevel level, BlockPos pos, BlockState solidified) {
		FluidState fluid = level.getFluidState(pos);
		if (!fluid.is(FluidTags.LAVA) || !fluid.isSource()) {
			return;
		}
		level.setBlock(pos, solidified, Block.UPDATE_ALL);
	}
}
