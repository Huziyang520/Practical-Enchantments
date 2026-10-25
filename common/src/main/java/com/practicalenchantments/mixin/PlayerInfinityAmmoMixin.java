// 无限的光灵箭与无箭开关都落在 Player#getProjectile(ItemStack)：它是弓与弩唯一的取弹入口。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.VanillaTweaks;
import com.practicalenchantments.enchantment.support.InfinitySupport;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerInfinityAmmoMixin {
	@Inject(method = "getProjectile", at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$infinityAmmo(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir) {
		if (!(weapon.getItem() instanceof ProjectileWeaponItem projectileWeapon)) {
			return;
		}
		if (InfinitySupport.level(weapon) <= 0) {
			return;
		}
		if (projectileWeapon instanceof CrossbowItem && !VanillaTweaks.infinityCrossbow()) {
			return;
		}
		Player self = (Player) (Object) this;

		if (self.hasInfiniteMaterials()) {
			return;
		}

		ItemStack ammo = cir.getReturnValue();
		if (!ammo.isEmpty()) {
			if (VanillaTweaks.infinitySpectral() && ammo.is(Items.SPECTRAL_ARROW)) {
				cir.setReturnValue(ammo.copy());
			}
			return;
		}
		if (VanillaTweaks.infinityNoArrow()) {
			cir.setReturnValue(new ItemStack(Items.ARROW));
		}
	}
}
