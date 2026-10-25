// 毁灭附魔 - 工具（剑除外）I 级 效果：挖掘方块不产出物品掉落，改为掉落经验；基础 2 点，矿石类 5 点

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class DestructionEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":destruction";
	private static final TagKey<Block> ORES_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.parse("minecraft:ores"));

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_BLOCK_DROPS, DestructionEnchantment::onModifyBlockDrops);
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
