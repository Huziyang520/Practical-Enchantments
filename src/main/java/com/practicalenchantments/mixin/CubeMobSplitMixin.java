package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 焚灭击杀的史莱姆类死后不再分裂。岩浆怪与模组史莱姆都继承 {@link Slime}，一并覆盖。
 */
@Mixin(Slime.class)
public abstract class CubeMobSplitMixin {

	/**
	 * {@code Slime#remove} 一进来就读 {@code getSize()}，读到的值大于 1 才进入分裂分支
	 * （{@code iload_2; iconst_1; if_icmple}）。把这一次读取改成 0 即跳过整段分裂，
	 * {@code remove} 本身照常执行，实体不会留在世界里。
	 */
	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;getSize()I"))
	private int practicalenchantments$skipSplitOnIncinerate(Slime self) {
		return KillMarks.consumeIncinerated(self) ? 0 : self.getSize();
	}
}
