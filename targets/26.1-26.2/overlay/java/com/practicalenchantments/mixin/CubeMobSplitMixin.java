// 焚灭击杀的立方体生物死后不再分裂（26.2+ 的 AbstractCubeMob）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.support.KillMarks;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.cubemob.AbstractCubeMob")
public abstract class CubeMobSplitMixin {
	@Shadow
	protected abstract int getSplitCount();

	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;getSplitCount()I"))
	private int practicalenchantments$skipSplitOnIncinerate(Object self) {
		return KillMarks.consumeIncinerated((LivingEntity) self) ? 0 : getSplitCount();
	}
}
