package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 焚灭击杀的生物不掉物品：战利品表、装备与生物特有掉落最终都走 spawnAtLocation，经验球不走这里。
 */
@Mixin(Entity.class)
public abstract class IncinerateDropMixin {

	/**
	 * 1.21.1 的掉落只有一个汇聚点：{@code spawnAtLocation(ItemStack, float)}，
	 * 另两个重载都转发到它；{@code LivingEntity#dropFromLootTable} 的方法引用也落在它上面。
	 */
	@Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)"
			+ "Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"),
			cancellable = true, require = 1)
	private void practicalenchantments$skipIncineratedDrops(ItemStack stack, float offsetY,
			CallbackInfoReturnable<ItemEntity> info) {
		if (((Entity) (Object) this) instanceof LivingEntity victim && KillMarks.isIncinerated(victim)) {
			info.setReturnValue(null);
		}
	}
}
