// 龙晶行者的免伤：踩在岩浆/火上的灼烧伤害直接免掉（数据包写法两端不一致，这里用代码统一）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.armor.ObsidianWalkerEnchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ObsidianWalkerImmunityMixin {
	@Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$skipSteppingBurn(ServerLevel level, DamageSource source, float amount,
			CallbackInfo info) {
		if (!source.is(DamageTypeTags.BURN_FROM_STEPPING) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return;
		}
		if (ObsidianWalkerEnchantment.wornBy(level, (LivingEntity) (Object) this)) {
			info.cancel();
		}
	}
}
