// 连弩：原版 tryLoadProjectiles 只装一根，这里按等级补装满同种箭矢并扣掉对应消耗。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.weapon.RepeatingCrossbowEnchantment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;

@Mixin(CrossbowItem.class)
public abstract class CrossbowRepeatingMixin {
	@Unique
	private static ItemStack practicalenchantments$ammo = ItemStack.EMPTY;

	@Inject(method = "tryLoadProjectiles", at = @At("HEAD"), require = 1)
	private static void practicalenchantments$captureAmmo(LivingEntity shooter, ItemStack held,
														  CallbackInfoReturnable<Boolean> cir) {
		practicalenchantments$ammo = shooter.getProjectile(held);
	}

	@Inject(method = "tryLoadProjectiles", at = @At("RETURN"), require = 1)
	private static void practicalenchantments$loadExtra(LivingEntity shooter, ItemStack held,
														CallbackInfoReturnable<Boolean> cir) {
		ItemStack ammo = practicalenchantments$ammo;
		practicalenchantments$ammo = ItemStack.EMPTY;
		if (!cir.getReturnValueZ() || ammo.isEmpty()) {
			return;
		}
		int extra = RepeatingCrossbowEnchantment.level(held);
		if (extra <= 0) {
			return;
		}
		ChargedProjectiles current = held.get(DataComponents.CHARGED_PROJECTILES);
		if (current == null || current.isEmpty()) {
			return;
		}
		List<ItemStack> loaded = current.getItems();
		int available = Math.min(extra, ammo.getCount());
		if (available <= 0) {
			return;
		}
		ItemStack template = loaded.get(0);
		List<ItemStack> merged = new ArrayList<>(loaded.size() + available);
		for (ItemStack item : loaded) {
			merged.add(item.copy());
		}
		for (int i = 0; i < available; i++) {
			merged.add(template.copy());
		}
		if (!shooter.hasInfiniteMaterials()) {
			ammo.shrink(available);
		}
		held.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(merged));
	}
}
