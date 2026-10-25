// 焚灭击杀的立方体生物死后不再分裂。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.support.KillMarks;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractCubeMob.class)
public abstract class CubeMobSplitMixin {
	@Shadow
	protected abstract int getSplitCount();

	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;getSplitCount()I"))
	private int practicalenchantments$skipSplitOnIncinerate(AbstractCubeMob self) {
		return KillMarks.consumeIncinerated(self) ? 0 : getSplitCount();
	}
}
