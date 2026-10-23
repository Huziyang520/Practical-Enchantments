package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 焚灭击杀的史莱姆类（史莱姆 / 岩浆怪 / 模组同族）死后不再分裂。
 */
@Mixin(Slime.class)
public abstract class CubeMobSplitMixin {

	// 1.21.1 的分裂在 Slime#remove(RemovalReason) 里：先取 getSize()，size > 1 才生成子代。
	// 不能取消 remove 本身（那样实体就永远留在世界里），只把这一次取到的尺寸读成 0，
	// 既跳过整段分裂，也照常落到 super.remove。
	@Redirect(method = "remove", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/Slime;getSize()I"))
	private int practicalenchantments$skipSplitOnIncinerate(Slime self) {
		return KillMarks.consumeIncinerated(self) ? 0 : self.getSize();
	}
}
