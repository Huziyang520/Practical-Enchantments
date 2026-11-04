// 追踪箭矢：射出时打标记，首次 tick 时换成暮色森林的 seeker_arrow（按 id 反射，不编译依赖该模组）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.linked.twilightforest.HuntingArrow;
import com.practicalenchantments.support.EnchantIds;
import com.practicalenchantments.support.ShotContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class HuntingArrowMixin {
	private static final String MARK = "practical_enchantments:hunting_arrow";
	private static final ResourceLocation SEEKER = new ResourceLocation("twilightforest", "seeker_arrow");

	@Inject(method = "shoot", at = @At("RETURN"), require = 1)
	private void practicalenchantments$markHunting(double x, double y, double z, float velocity, float inaccuracy,
												   CallbackInfo ci) {
		ItemStack weapon = ShotContext.weapon();
		if (weapon == null || weapon.isEmpty()) {
			return;
		}
		if (EnchantIds.levelOf(weapon, HuntingArrow.ID) <= 0) {
			return;
		}
		AbstractArrow self = (AbstractArrow) (Object) this;
		self.getPersistentData().putBoolean(MARK, true);
	}

	@Inject(method = "tick", at = @At("HEAD"), require = 1)
	private void practicalenchantments$swapToSeeker(CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (self.level().isClientSide() || !self.getPersistentData().getBoolean(MARK)) {
			return;
		}
		self.getPersistentData().remove(MARK);
		if (self.getClass().getName().startsWith("twilightforest.")) {
			return;
		}
		EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(SEEKER);
		Entity created = entityType == null ? null : entityType.create(self.level());
		if (!(created instanceof AbstractArrow seeker)) {
			return;
		}
		seeker.setPos(self.getX(), self.getY(), self.getZ());
		seeker.setDeltaMovement(self.getDeltaMovement());
		seeker.setYRot(self.getYRot());
		seeker.setXRot(self.getXRot());
		seeker.setOwner(self.getOwner());
		seeker.pickup = self.pickup;
		self.level().addFreshEntity(seeker);
		self.discard();
	}
}
