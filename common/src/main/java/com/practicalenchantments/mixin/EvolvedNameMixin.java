// 进化附魔的名称走库里的彩虹文本 API：逐字渐变，色相随时间流动。

package com.practicalenchantments.mixin;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.content.PeEvolution;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EvolvedNameMixin {
	@Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
	private static void pe$coloredName(Holder<Enchantment> holder, int level,
			CallbackInfoReturnable<Component> callback) {
		if (!PeEvolution.isEvolved(holder)) {
			return;
		}
		MutableComponent name = MerlinApi.text().rainbow(holder.value().description()).copy();
		if (level > 1) {
			name.append(Component.literal(" " + level).withStyle(ChatFormatting.AQUA));
		}
		callback.setReturnValue(name);
	}
}
