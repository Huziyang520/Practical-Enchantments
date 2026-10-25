// 免死类附魔：恶魔交易 / 掉落不死亡。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.DemonicPactEnchantment;
import com.practicalenchantments.enchantment.UndyingDropEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class DeathSaveMixin {
	@Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
		at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$deathSave(DamageSource source, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;

		if (DemonicPactEnchantment.trySave(player, source)
			|| UndyingDropEnchantment.trySave(player, source)) {
			ci.cancel();
		}
	}
}
