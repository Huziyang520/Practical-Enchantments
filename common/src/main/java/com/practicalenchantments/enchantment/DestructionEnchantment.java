package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 毁灭附魔 - 工具（剑除外）I 级
 * 效果：挖掘方块不产出物品掉落，改为掉落经验；基础 2 点，矿石类 5 点
 */
public final class DestructionEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":destruction";
	
	// MC 26.2 中使用 TagKey 直接引用矿石标签
	private static final TagKey<Block> ORES_TAG = TagKey.create(Registries.BLOCK, Identifier.parse("minecraft:ores"));


	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_BLOCK_DROPS, DestructionEnchantment::onModifyBlockDrops);
	}

	private static void onModifyBlockDrops(BuiltInEvents.ModifyBlockDropsEvent event, EnchantmentContext ctx) {
		// 只对主手工具上的毁灭附魔生效。
		// 注意：与伐木工联动递归破坏方块时，Mixin 传入的工具可能为空（ItemStack.EMPTY），
		// 但事件仍会以玩家为主手扫描并触发回调，故不能以 tool.isEmpty() 作为跳过条件，
		// 而应依据上下文中的槽位（主手 = 破坏方块的工具）判断。
		if (ctx.slot() != EquipmentSlot.MAINHAND) return;

		BlockState brokenState = event.blockState();

		// 清除所有物品掉落
		event.drops().clear();

		// 计算经验值
		int xp = 2; // 基础 2 点

		// 矿石类 5 点（使用 minecraft:ores 标签）
		if (brokenState.is(ORES_TAG)) {
			xp = 5;
		}

		// 通过 addBonusXp 补充经验（由 Mixin 自动生成经验球）
		event.addBonusXp(xp);
	}
}
