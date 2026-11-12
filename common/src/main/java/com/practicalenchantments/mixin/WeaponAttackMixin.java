// 武器命中结算：挑衅的伤害与激怒、风王结界的翻倍、重击的命中记录。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.content.WrathContent;
import com.practicalenchantments.support.SweepWindow;
import com.practicalenchantments.enchantment.weapon.HeavyBlowEnchantment;
import com.practicalenchantments.enchantment.weapon.InvisibleAirEnchantment;
import com.practicalenchantments.enchantment.weapon.ProvocationEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class WeaponAttackMixin {
	@Unique
	private Entity practicalenchantments$attackTarget;

	@Inject(method = "attack", at = @At("HEAD"), require = 1)
	private void practicalenchantments$beginSwing(Entity target, CallbackInfo info) {
		practicalenchantments$attackTarget = target;
		Player self = (Player) (Object) this;
		if (HeavyBlowEnchantment.has(self)) {
			HeavyBlowEnchantment.markSwing(self, false);
		}
	}

	// 只改伤害参数、不改写调用点描述符，否则同一调用点上其它模组的 @ModifyArg 会注入失败。
	@ModifyArg(method = "attack", require = 1, index = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private float practicalenchantments$weaponHit(float amount) {
		Player self = (Player) (Object) this;
		Entity target = practicalenchantments$attackTarget;
		// 打尸体不缩放伤害，也不触发挑衅；后半段的结算交还原版。
		if (!target.isAlive() || target instanceof LivingEntity living && living.isDeadOrDying()) {
			return amount;
		}
		ItemStack stack = self.getMainHandItem();
		SweepWindow.begin(self, target);
		return amount * provocationFactor(self, target, stack) * invisibleAirFactor(stack);
	}

	private static float provocationFactor(Player self, Entity target, ItemStack stack) {
		int level = levelOf(stack, ProvocationEnchantment.ID);
		if (level <= 0 || !(target instanceof LivingEntity living)) {
			return 1.0F;
		}
		Holder<MobEffect> wrath = wrathEffect(self);
		if (wrath == null) {
			return 1.0F;
		}
		if (living.hasEffect(wrath)) {
			return (float) (Tunables.provocationAngerBonusBase() + Tunables.provocationAngerBonusPerLevel() * level);
		}
		living.addEffect(new MobEffectInstance(wrath,
					tunablesAngerTicks(), 0));
		return (float) Tunables.provocationProvokedDamageFactor();
	}

	private static float invisibleAirFactor(ItemStack stack) {
		return levelOf(stack, InvisibleAirEnchantment.ID) > 0 ? (float) Tunables.invisibleAirDamageFactor() : 1.0F;
	}

	private static int levelOf(ItemStack stack, String id) {
		for (var entry : stack.getEnchantments().entrySet()) {
			var key = entry.getKey().unwrapKey();
			if (key.isPresent() && key.get().identifier().toString().equals(id)) {
				return entry.getIntValue();
			}
		}
		return 0;
	}

	private static int tunablesAngerTicks() {
		return Tunables.secondsToTicks(Tunables.provocationAngerSeconds());
	}

	private static Holder<MobEffect> wrathEffect(Player self) {
		return self.level().registryAccess().lookupOrThrow(Registries.MOB_EFFECT)
				.get(WrathContent.EFFECT_ID)
				.orElse(null);
	}
}
