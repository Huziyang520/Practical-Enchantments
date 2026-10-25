// 毁灭附魔 - 工具（剑除外）I 级

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public final class DestructionEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":destruction";
	private static final TagKey<Block> ORES_TAG = Tags.Blocks.ORES;

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_BLOCK_DROPS, DestructionEnchantment::onModifyBlockDrops);
	}

	private static void onModifyBlockDrops(BuiltInEvents.ModifyBlockDropsEvent event, EnchantmentContext ctx) {
		if (ctx.slot() != EquipmentSlot.MAINHAND) return;
		BlockState brokenState = event.blockState();
		event.drops().clear();
		int xp = 2;

		if (brokenState.is(ORES_TAG)) {
			xp = 5;
		}

		event.addBonusXp(xp);
	}
}
