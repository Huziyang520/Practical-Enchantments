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
 * 焚灭击杀的生物不掉物品：1.20.1 的战利品表、装备与生物特有掉落最终都走这里，经验球不走。
 */
@Mixin(Entity.class)
public abstract class IncinerateDropMixin {

	/**
	 * 两个重载里只有这个带偏移量的真正建出物品实体，单参数的 {@code spawnAtLocation(ItemStack)} 直接转调它，
	 * 所以拦一次即可覆盖全部掉落路径。
	 */
	@Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)"
			+ "Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"),
			cancellable = true, require = 1)
	private void practicalenchantments$skipIncineratedDrops(ItemStack stack, float offset,
			CallbackInfoReturnable<ItemEntity> info) {
		if (((Entity) (Object) this) instanceof LivingEntity victim && KillMarks.isIncinerated(victim)) {
			info.setReturnValue(null);
		}
	}
}
