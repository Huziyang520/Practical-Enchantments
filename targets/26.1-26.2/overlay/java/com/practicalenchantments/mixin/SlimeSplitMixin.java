// 焚灭击杀的史莱姆死后不再分裂（26.1.2，没有 AbstractCubeMob）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.support.KillMarks;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Slime.class)
public abstract class SlimeSplitMixin {
	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;getSize()I", ordinal = 0))
	private int practicalenchantments$skipSplitOnIncinerate(Slime self) {
		return KillMarks.consumeIncinerated(self) ? 0 : self.getSize();
	}
}
