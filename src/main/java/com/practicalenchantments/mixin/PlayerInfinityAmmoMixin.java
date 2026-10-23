package com.practicalenchantments.mixin;

import com.practicalenchantments.config.VanillaTweaks;
import com.practicalenchantments.enchantment.InfinitySupport;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 无限的光灵箭与无箭开关都落在 {@code Player#getProjectile}：它是弓与弩唯一的取弹入口。
 */
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
		// 创造模式原版已经给了一支不消耗的箭，再插手只会打乱它的路径。
		if (self.getAbilities().instabuild) {
			return;
		}

		ItemStack ammo = cir.getReturnValue();
		if (ammo != null && !ammo.isEmpty()) {
			if (VanillaTweaks.infinitySpectral() && ammo.is(Items.SPECTRAL_ARROW)) {
				// 返回副本：背包里那一摞不动，光灵箭仍然要有一根才能射。
				cir.setReturnValue(ammo.copy());
			}
			return;
		}
		if (VanillaTweaks.infinityNoArrow()) {
			cir.setReturnValue(new ItemStack(Items.ARROW));
		}
	}
}
