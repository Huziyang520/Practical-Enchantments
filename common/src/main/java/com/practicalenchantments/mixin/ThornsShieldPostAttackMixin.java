// 受击后处理开始时记下受击者，供荆棘盾判定「是否举着这面盾」。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.VanillaTweaks;
import com.practicalenchantments.enchantment.support.ThornsShieldSupport;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentHelper.class)
public abstract class ThornsShieldPostAttackMixin {
	private static final String TARGET =
			"doPostAttackEffectsWithItemSourceOnBreak(Lnet/minecraft/server/level/ServerLevel;"
					+ "Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;"
					+ "Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)V";

	@Inject(method = TARGET, at = @At("HEAD"), require = 1)
	private static void practicalenchantments$pushVictim(ServerLevel level, Entity entity, DamageSource source,
			ItemStack itemSource, Consumer<ItemStack> onBreak, CallbackInfo ci) {
		if (VanillaTweaks.thornsShield()) {
			ThornsShieldSupport.push(entity);
		}
	}

	@Inject(method = TARGET, at = @At("RETURN"), require = 1)
	private static void practicalenchantments$popVictim(ServerLevel level, Entity entity, DamageSource source,
			ItemStack itemSource, Consumer<ItemStack> onBreak, CallbackInfo ci) {
		if (VanillaTweaks.thornsShield()) {
			ThornsShieldSupport.pop();
		}
	}
}
