package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 26.1 的立方体分裂写在 {@link Slime#remove} 里：分裂数量由开头的 {@code getSize()} 决定。
 * 把这一次取到的大小压成 1，{@code size > 1} 的判定即不成立，分裂整段跳过。岩浆怪与继承
 * {@code Slime} 的模组史莱姆共用这一份代码。
 */
@Mixin(Slime.class)
public abstract class SlimeSplitMixin {

	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/Slime;getSize()I", ordinal = 0))
	private int practicalenchantments$skipSplitOnIncinerate(Slime self) {
		return KillMarks.consumeIncinerated(self) ? 1 : self.getSize();
	}
}
