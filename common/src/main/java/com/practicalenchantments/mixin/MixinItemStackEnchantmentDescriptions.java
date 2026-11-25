// 在原版附魔名后补一行模组说明；说明文本来自语言文件的 .desc 键。

package com.practicalenchantments.mixin;

import com.practicalenchantments.PracticalEnchantments;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class MixinItemStackEnchantmentDescriptions {
	private static final Set<String> DESCRIBED = Set.of(
			"eye_of_judgment", "verdict", "point_blank", "sniper", "arrowfall", "mutual_destruction",
			"wildfire", "benevolent_strike", "righteous_cleave", "intimidation", "lightning_rod",
			"scavenger", "disarm");

	@Inject(method = "addDetailsToTooltip", at = @At("TAIL"))
	private void pe$appendDescriptions(Item.TooltipContext context, TooltipDisplay display, Player player,
									   TooltipFlag flag, Consumer<Component> builder, CallbackInfo info) {
		ItemStack self = (ItemStack) (Object) this;
		if (display.shows(DataComponents.ENCHANTMENTS)) {
			append(self.getEnchantments(), builder);
		}
		if (display.shows(DataComponents.STORED_ENCHANTMENTS)) {
			append(self.get(DataComponents.STORED_ENCHANTMENTS), builder);
		}
	}

	private static void append(ItemEnchantments enchantments, Consumer<Component> builder) {
		if (enchantments == null || enchantments.isEmpty()) {
			return;
		}
		for (var entry : enchantments.entrySet()) {
			String id = entry.getKey().unwrapKey().map(key -> key.identifier().toString()).orElse("");
			if (!id.startsWith(PracticalEnchantments.MOD_ID + ":")) {
				continue;
			}
			String path = id.substring(PracticalEnchantments.MOD_ID.length() + 1);
			if (!DESCRIBED.contains(path)) {
				continue;
			}
			builder.accept(Component.translatable("enchantment." + PracticalEnchantments.MOD_ID + "." + path + ".desc")
					.withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}
