// 慌乱：服务端取消拾取，客户端不取消以免状态不同步。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.PeEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class MixinItemEntityPickup {
	@Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
	private void pe$panicCannotPickup(Player player, CallbackInfo info) {
		if (!player.level().isClientSide() && PeEffects.hasPanic(player)) {
			info.cancel();
		}
	}
}
