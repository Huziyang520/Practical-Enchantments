// 武器命中结算：挑衅的伤害与激怒、风王结界的翻倍、重击的命中记录。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.WrathContent;
import com.practicalenchantments.enchantment.HeavyBlowEnchantment;
import com.practicalenchantments.enchantment.InvisibleAirEnchantment;
import com.practicalenchantments.enchantment.ProvocationEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class WeaponAttackMixin {
	private static final float PROVOKED_DAMAGE_FACTOR = 0.10F;
	private static final float ANGER_BONUS_BASE = 1.15F;
	private static final float ANGER_BONUS_PER_LEVEL = 0.20F;
	private static final float INVISIBLE_AIR_FACTOR = 2.0F;
	private static final int ANGER_DURATION = 10 * 20;

	@Redirect(method = "attack", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean practicalenchantments$weaponHit(Entity target, DamageSource source, float amount) {
		Player self = (Player) (Object) this;
		ItemStack stack = self.getMainHandItem();
		float scaled = amount * provocationFactor(self, target, stack) * invisibleAirFactor(stack);
		boolean hit = target.hurt(source, scaled);
		if (HeavyBlowEnchantment.has(self)) {
			HeavyBlowEnchantment.markSwing(self, hit);
		}
		return hit;
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
			return ANGER_BONUS_BASE + ANGER_BONUS_PER_LEVEL * level;
		}
		living.addEffect(new MobEffectInstance(wrath, ANGER_DURATION, 0));
		return PROVOKED_DAMAGE_FACTOR;
	}

	private static float invisibleAirFactor(ItemStack stack) {
		return levelOf(stack, InvisibleAirEnchantment.ID) > 0 ? INVISIBLE_AIR_FACTOR : 1.0F;
	}

	private static int levelOf(ItemStack stack, String id) {
		for (var entry : stack.getEnchantments().entrySet()) {
			var key = entry.getKey().unwrapKey();
			if (key.isPresent() && key.get().location().toString().equals(id)) {
				return entry.getIntValue();
			}
		}
		return 0;
	}

	private static Holder<MobEffect> wrathEffect(Player self) {
		return self.level().registryAccess().lookupOrThrow(Registries.MOB_EFFECT)
				.get(ResourceKey.create(Registries.MOB_EFFECT, WrathContent.EFFECT_ID))
				.orElse(null);
	}
}
