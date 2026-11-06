// 箭矢射出时按附魔放大初速：速矢（无上限）与「快速装填可用于弓」（封顶到满蓄力）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.config.VanillaTweaks;
import com.practicalenchantments.enchantment.SwiftArrowEnchantment;
import com.practicalenchantments.support.EnchantIds;
import com.practicalenchantments.support.ShotContext;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowShootMixin {
	private static final String QUICK_CHARGE = "minecraft:quick_charge";
	
	@Inject(method = "shoot", at = @At("RETURN"), require = 1)
	private void practicalenchantments$shootSpeed(double x, double y, double z, float velocity, float inaccuracy,
												  CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (self.level().isClientSide()) {
			return;
		}
		ItemStack weapon = ShotContext.weapon();
		if (weapon == null || weapon.isEmpty()) {
			return;
		}
		double factor = SwiftArrowEnchantment.speedFactor(weapon);
		boolean bow = weapon.getItem() instanceof BowItem;
		if (bow && VanillaTweaks.quickChargeBow()) {
			int quickCharge = EnchantIds.levelOf(weapon, QUICK_CHARGE);
			if (quickCharge > 0) {
				factor *= 1.0D / Math.max(1.0E-3D, 1.0D - Tunables.quickChargeOnBowSpeedPerLevel() * quickCharge);
			}
		}
		if (factor <= 1.0D) {
			return;
		}
		Vec3 motion = self.getDeltaMovement();
		double speed = motion.length();
		if (speed < 1.0E-4D) {
			return;
		}
		double target = speed * factor;
		if (bow && target > Tunables.swiftArrowBowSpeedCap()) {
					target = Tunables.swiftArrowBowSpeedCap();
		}
		if (target > speed) {
			self.setDeltaMovement(motion.scale(target / speed));
		}
	}
}
