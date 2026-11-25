// 收猎：捕获窗口内新生成的 ItemEntity 才算本次掉落。

package com.practicalenchantments.mixin;

import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class MixinScavengerAddEntity {
	@Inject(method = "addFreshEntity", at = @At("HEAD"))
	private void pe$captureDrops(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if (entity instanceof ItemEntity item) {
			MerlinApi.drops().captureDeathDrop(item);
		}
	}
}
