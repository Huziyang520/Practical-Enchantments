// 焚灭击杀的史莱姆类死后不再分裂。岩浆怪与模组史莱姆都继承 Slime，一并覆盖。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Slime.class)
public abstract class CubeMobSplitMixin {
	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;getSize()I"))
	private int practicalenchantments$skipSplitOnIncinerate(Slime self) {
		return KillMarks.consumeIncinerated(self) ? 0 : self.getSize();
	}
}
