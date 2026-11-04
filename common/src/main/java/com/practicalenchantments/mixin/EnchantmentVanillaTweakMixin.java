// 原版附魔的适用范围开关：力量可上弩、刺棘可上盾；覆皮则排除皮革装备与护身符。

package com.practicalenchantments.mixin;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.VanillaTweaks;
import com.practicalenchantments.enchantment.armor.HideSkinEnchantment;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentVanillaTweakMixin {
	private static final String POWER_KEY = "enchantment.minecraft.power";
	private static final String THORNS_KEY = "enchantment.minecraft.thorns";
	private static final String HIDE_SKIN_KEY =
			"enchantment." + PracticalEnchantments.MOD_ID + ".hide_skin";

	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		Enchantment self = (Enchantment) (Object) this;
		if (!(self.description().getContents() instanceof TranslatableContents contents)) {
			return;
		}
		String key = contents.getKey();
		if (HIDE_SKIN_KEY.equals(key)) {
			if (HideSkinEnchantment.excluded(stack)) {
				cir.setReturnValue(false);
			}
			return;
		}
		if (POWER_KEY.equals(key) && VanillaTweaks.powerCrossbow() && stack.getItem() instanceof CrossbowItem) {
			cir.setReturnValue(true);
			return;
		}
		if (THORNS_KEY.equals(key) && VanillaTweaks.thornsShield() && stack.getItem() instanceof ShieldItem) {
			cir.setReturnValue(true);
		}
	}
}
