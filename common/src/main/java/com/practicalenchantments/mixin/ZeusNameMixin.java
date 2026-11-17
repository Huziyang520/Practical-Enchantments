// 进化附魔的名称逐字渐变着色（原版 getFullname 强制灰色）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class ZeusNameMixin {
	private static final Identifier ZEUS =
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "zeus_poseidon");
	private static final ChatFormatting[] PALETTE = {
			ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.AQUA,
			ChatFormatting.LIGHT_PURPLE, ChatFormatting.BLUE
	};

	@Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
	private static void pe$coloredName(Holder<Enchantment> holder, int level,
			CallbackInfoReturnable<Component> callback) {
		if (!holder.is(ZEUS)) {
			return;
		}
		String text = holder.value().description().getString();
		MutableComponent name = Component.empty();
		for (int index = 0; index < text.length(); index++) {
			int paletteIndex = text.length() <= 1 ? 0 : index * PALETTE.length / text.length();
			name.append(Component.literal(String.valueOf(text.charAt(index)))
					.withStyle(PALETTE[Math.min(paletteIndex, PALETTE.length - 1)]));
		}
		if (level > 1) {
			name.append(Component.literal(" " + level).withStyle(ChatFormatting.AQUA));
		}
		callback.setReturnValue(name);
	}
}
