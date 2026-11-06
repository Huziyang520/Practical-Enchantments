// 武器命中结算：挑衅的伤害与激怒、风王结界的翻倍、重击的命中记录。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.content.WrathContent;
import com.practicalenchantments.support.SweepWindow;
import com.practicalenchantments.enchantment.HeavyBlowEnchantment;
import com.practicalenchantments.enchantment.InvisibleAirEnchantment;
import com.practicalenchantments.enchantment.ProvocationEnchantment;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class WeaponAttackMixin {
	
	@Redirect(method = "attack", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean practicalenchantments$weaponHit(Entity target, DamageSource source, float amount) {
		Player self = (Player) (Object) this;
		ItemStack stack = self.getMainHandItem();
		float scaled = amount * provocationFactor(target, stack) * invisibleAirFactor(stack);
		SweepWindow.begin(self, target);
		boolean hit = target.hurt(source, scaled);
		if (HeavyBlowEnchantment.has(self)) {
			HeavyBlowEnchantment.markSwing(self, hit);
		}
		return hit;
	}

	private static float provocationFactor(Entity target, ItemStack stack) {
		int level = levelOf(stack, ProvocationEnchantment.ID);
		if (level <= 0 || !(target instanceof LivingEntity living)) {
			return 1.0F;
		}
		MobEffect wrath = BuiltInRegistries.MOB_EFFECT.get(WrathContent.EFFECT_ID);
		if (wrath == null) {
			return 1.0F;
		}
		if (living.hasEffect(wrath)) {
			return (float) (Tunables.provocationAngerBonusBase() + Tunables.provocationAngerBonusPerLevel() * level);
		}
		living.addEffect(new MobEffectInstance(wrath,
					Tunables.secondsToTicks(Tunables.provocationAngerSeconds()), 0));
		return (float) Tunables.provocationProvokedDamageFactor();
	}

	private static float invisibleAirFactor(ItemStack stack) {
		return levelOf(stack, InvisibleAirEnchantment.ID) > 0 ? (float) Tunables.invisibleAirDamageFactor() : 1.0F;
	}

	private static int levelOf(ItemStack stack, String id) {
		ResourceLocation key = new ResourceLocation(id);
		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
			if (key.equals(BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey()))) {
				return entry.getValue() == null ? 0 : entry.getValue();
			}
		}
		return 0;
	}
}
