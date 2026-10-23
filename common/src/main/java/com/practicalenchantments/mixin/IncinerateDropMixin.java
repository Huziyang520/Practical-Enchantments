package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 焚灭击杀的生物不掉物品：战利品表、装备与生物特有掉落最终都走 spawnAtLocation，经验球不走这里。
 */
@Mixin(Entity.class)
public abstract class IncinerateDropMixin {

	@Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;"
			+ "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)"
			+ "Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"),
			cancellable = true, require = 1)
	private void practicalenchantments$skipIncineratedDrops(ServerLevel level, ItemStack stack, Vec3 offset,
			CallbackInfoReturnable<ItemEntity> info) {
		if (((Entity) (Object) this) instanceof LivingEntity victim && KillMarks.isIncinerated(victim)) {
			info.setReturnValue(null);
		}
	}
}
