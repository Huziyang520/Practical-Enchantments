// 进化附魔的名称逐字彩虹渐变，色相随时间流动（原版 getFullname 强制灰色）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.PeEvolution;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EvolvedNameMixin {
	private static final long PERIOD_MS = 5000L;

	@Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
	private static void pe$coloredName(Holder<Enchantment> holder, int level,
			CallbackInfoReturnable<Component> callback) {
		if (!PeEvolution.isEvolved(holder)) {
			return;
		}
		String text = holder.value().description().getString();
		float phase = (System.currentTimeMillis() % PERIOD_MS) / (float) PERIOD_MS;
		int length = Math.max(1, text.length());
		MutableComponent name = Component.empty();
		for (int index = 0; index < text.length(); index++) {
			float hue = (index / (float) length + phase) % 1.0F;
			int rgb = rainbow(hue);
			name.append(Component.literal(String.valueOf(text.charAt(index)))
					.withStyle(style -> style.withColor(TextColor.fromRgb(rgb))));
		}
		if (level > 1) {
			name.append(Component.literal(" " + level).withStyle(ChatFormatting.AQUA));
		}
		callback.setReturnValue(name);
	}

	private static int rainbow(float hue) {
		float h = (hue % 1.0F) * 6.0F;
		float x = 1.0F - Math.abs(h % 2.0F - 1.0F);
		float r;
		float g;
		float b;
		switch ((int) h) {
			case 0 -> { r = 1.0F; g = x; b = 0.0F; }
			case 1 -> { r = x; g = 1.0F; b = 0.0F; }
			case 2 -> { r = 0.0F; g = 1.0F; b = x; }
			case 3 -> { r = 0.0F; g = x; b = 1.0F; }
			case 4 -> { r = x; g = 0.0F; b = 1.0F; }
			default -> { r = 1.0F; g = 0.0F; b = x; }
		}
		int red = Math.round(r * 255.0F);
		int green = Math.round(g * 255.0F);
		int blue = Math.round(b * 255.0F);
		return (red << 16) | (green << 8) | blue;
	}
}
