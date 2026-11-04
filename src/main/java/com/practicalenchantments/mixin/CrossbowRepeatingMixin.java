// 连弩：原版 tryLoadProjectiles 只装一根，这里用 @Invoker 补装满同种箭矢并扣掉对应消耗。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.RepeatingCrossbowEnchantment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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
		int available = Math.min(extra, ammo.getCount());
		List<ItemStack> loaded = CrossbowChargedAccessor.practicalenchantments$charged(held);
		if (available <= 0 || loaded.isEmpty()) {
			return;
		}
		ItemStack template = loaded.get(0).copy();
		for (int i = 0; i < available; i++) {
			CrossbowChargedAccessor.practicalenchantments$add(held, template.copy());
		}
		boolean creative = shooter instanceof Player player && player.getAbilities().instabuild;
		if (!creative) {
			ammo.shrink(available);
		}
	}
}
