package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.DemonicPactEnchantment;
import com.practicalenchantments.enchantment.UndyingDropEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 免死类附魔：恶魔交易 / 掉落不死亡。
 *
 * <p>在 {@link ServerPlayer#die(DamageSource)} HEAD 处拦截玩家的致命死亡：
 * 优先判定胸甲上的恶魔交易（消耗绿宝石 + 冷却），其次判定任意护甲上的
 * 掉落不死亡（摧毁装备 + 掉落全身物品）。触发后把生命设回 1 点并取消 die，
 * 后续 {@code getHealth() <= 0} 判定不再成立，玩家存活。</p>
 *
 * <p><b>为什么挂 ServerPlayer 而不是 LivingEntity</b>：26.2 中
 * {@code ServerPlayer#die} 完全覆写且不调用 {@code super.die()}，玩家死亡
 * 的虚分派根本不会进入 {@code LivingEntity#die} 方法体——挂在基类上对玩家
 * 永远不触发（先前两附魔失效的根因）。</p>
 *
 * <p>虚空伤害与 {@code /kill}（{@code bypasses_invulnerability}）直接放行，
 * 与原版不死图腾口径一致。仅作用于服务端玩家。</p>
 */
@Mixin(ServerPlayer.class)
public abstract class DeathSaveMixin {

	@Inject(method = "die", at = @At("HEAD"), cancellable = true)
	private void practicalenchantments$deathSave(DamageSource source, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		// 互斥组保证两件不会共存（指令强塞时按恶魔交易优先）
		if (DemonicPactEnchantment.trySave(player, source)
			|| UndyingDropEnchantment.trySave(player, source)) {
			ci.cancel();
		}
	}
}
