package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 焚灭击杀的立方体生物死后不再分裂。
 */
@Mixin(AbstractCubeMob.class)
public abstract class CubeMobSplitMixin {

	@Shadow
	protected abstract int getSplitCount();

	// 不能取消 remove 本身（那样实体就永远留在世界里），只把这一次取到的分裂数量改成 0。
	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;getSplitCount()I"))
	private int practicalenchantments$skipSplitOnIncinerate(AbstractCubeMob self) {
		return KillMarks.consumeIncinerated(self) ? 0 : getSplitCount();
	}
}
