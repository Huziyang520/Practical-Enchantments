// 进化附魔的名称按附魔自身的样式显示（原版 getFullname 强制灰色）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
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

	@Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
	private static void pe$coloredName(Holder<Enchantment> holder, int level,
			CallbackInfoReturnable<Component> callback) {
		if (!holder.is(ZEUS)) {
			return;
		}
		Component name = holder.value().description().copy().withStyle(ChatFormatting.AQUA);
		if (level > 1) {
			name = name.copy().append(" ")
					.append(Component.translatable("enchantment.level." + level).withStyle(ChatFormatting.AQUA));
		}
		callback.setReturnValue(name);
	}
}
