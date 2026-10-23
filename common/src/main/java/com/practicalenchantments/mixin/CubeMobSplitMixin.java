package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 26.2 把这族生物搬进 {@code monster.cubemob} 并抽出 {@code AbstractCubeMob}，分裂数量集中在
 * {@code getSplitCount()}。目标写成字符串：本分支的编译基线 26.1.2 还没有这个类，只有 26.2 上
 * {@link PracticalMixinPlugin} 才会放行这条 mixin。
 */
@Mixin(targets = "net.minecraft.world.entity.monster.cubemob.AbstractCubeMob")
public abstract class CubeMobSplitMixin {

	@Shadow
	protected abstract int getSplitCount();

	// 不能取消 remove 本身（那样实体就永远留在世界里），只把这一次取到的分裂数量改成 0。
	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;getSplitCount()I"))
	private int practicalenchantments$skipSplitOnCubeMob(Object self) {
		return KillMarks.consumeIncinerated((LivingEntity) self) ? 0 : ((CubeMobSplitMixin) self).getSplitCount();
	}
}
