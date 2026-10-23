package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2 把这族生物搬进 {@code monster.cubemob} 并抽出 {@code AbstractCubeMob}：{@code remove} 开头先取
 * {@code getSize()}，只有大于 1 才走分裂。目标写成字符串，因为编译基线 26.1.2 还没有这个类。
 * 把大小压成 1，分裂整段跳过，{@code remove} 的其余部分照常执行。
 */
@Mixin(targets = "net.minecraft.world.entity.monster.cubemob.AbstractCubeMob")
public abstract class CubeMobSplitMixin extends AgeableMob {

	@Shadow
	public abstract int getSize();

	@Shadow
	public abstract void setSize(int size, boolean heal);

	protected CubeMobSplitMixin(EntityType<? extends AgeableMob> type, Level level) {
		super(type, level);
	}

	@Inject(method = "remove", at = @At("HEAD"), require = 1)
	private void practicalenchantments$skipSplitOnIncinerate(Entity.RemovalReason reason, CallbackInfo ci) {
		if (KillMarks.consumeIncinerated(this) && getSize() > 1) {
			setSize(1, false);
		}
	}
}
