// 追踪箭矢：命中时把玩家射出的普通箭换成暮色森林的 seeker_arrow（按 id 反射，不编译依赖该模组）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.linked.twilightforest.HuntingArrow;
import com.practicalenchantments.support.EnchantIds;
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
import java.lang.reflect.Constructor;

@Mixin(AbstractArrow.class)
public abstract class HuntingArrowMixin {
	private static final ResourceLocation SEEKER = ResourceLocation.parse("twilightforest:seeker_arrow");

	@Inject(method = "tick", at = @At("HEAD"), require = 1)
	private void practicalenchantments$huntingArrow(CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (self.level().isClientSide() || self.getClass().getName().startsWith("twilightforest.")) {
			return;
		}
		ItemStack weapon = self.getWeaponItem();
		if (weapon == null || weapon.isEmpty() || EnchantIds.levelOf(weapon, HuntingArrow.ID) <= 0) {
			return;
		}
		AbstractArrow seeker = createSeeker(self, weapon);
		if (seeker == null) {
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

	private static AbstractArrow createSeeker(AbstractArrow original, ItemStack weapon) {
		try {
			Class<?> type = Class.forName("twilightforest.entity.projectile.SeekerArrow");
			Constructor<?> constructor = type.getConstructor(AbstractArrow.class, ItemStack.class, ItemStack.class);
			return (AbstractArrow) constructor.newInstance(original, original.getPickupItemStackOrigin(), weapon);
		} catch (ReflectiveOperationException | LinkageError missing) {
		}
		EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(SEEKER);
		Entity created = entityType == null ? null : entityType.create(original.level());
		return created instanceof AbstractArrow arrow ? arrow : null;
	}
}
