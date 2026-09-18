package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.TridentPullSupport;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 三叉戟回归 tick 驱动牵引（贯穿/粉碎 × 忠诚）。
 *
 * <p>本 Mixin 只做一件事：三叉戟每刻把投掷者与自身交给
 * {@link TridentPullSupport#tick}，由状态中心对命中生物与掉落物施加牵引。</p>
 *
 * <p>状态捕获不在本类：实体命中走 enchantlib 的 {@code PROJECTILE_HIT}
 * 事件回调（{@link TridentPullSupport#captureMob}，由
 * {@code PenetrationEnchantment} 注册），方块命中由 {@code TridentSmashMixin}
 * 粉碎成功后调 {@link TridentPullSupport#startItemPull}。牵引不依赖
 * {@code dealtDamage}——方块命中路径上该字段不会置位（坑 30 根因）。</p>
 */
@Mixin(ThrownTrident.class)
public abstract class TridentPullMixin {

	@Inject(method = "tick", at = @At("TAIL"))
	private void practicalenchantments$pullWhileReturning(CallbackInfo ci) {
		ThrownTrident self = (ThrownTrident) (Object) this;
		if (!(self.level() instanceof ServerLevel level)) {
			return;
		}
		if (!(self.getOwner() instanceof LivingEntity owner)) {
			return;
		}
		TridentPullSupport.tick(level, owner, self);
	}
}
