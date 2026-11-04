// 原版附魔的适用范围：力量可上弩、刺棘可上盾（含副手槽参与结算），覆皮排除皮革装备与护身符。

package com.practicalenchantments.mixin;

import com.practicalenchantments.config.VanillaTweaks;
import com.practicalenchantments.enchantment.HideSkinEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(Enchantment.class)
public abstract class EnchantmentVanillaTweakMixin {
	private static final String POWER_KEY = "enchantment.minecraft.power";
	private static final String THORNS_KEY = "enchantment.minecraft.thorns";
	private static final String SWIFT_ARROW_KEY = "enchantment.practical_enchantments.swift_arrow";
	private static final String HIDE_SKIN_KEY = "enchantment.practical_enchantments.hide_skin";

	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true, require = 1)
	private void practicalenchantments$canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		String key = ((Enchantment) (Object) this).getDescriptionId();
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
		if (SWIFT_ARROW_KEY.equals(key) && stack.getItem() instanceof CrossbowItem) {
			cir.setReturnValue(true);
			return;
		}
		if (THORNS_KEY.equals(key) && VanillaTweaks.thornsShield() && stack.getItem() instanceof ShieldItem) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getSlotItems", at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$thornsOnShield(LivingEntity entity,
													  CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
		if (!VanillaTweaks.thornsShield()) {
			return;
		}
		if (!THORNS_KEY.equals(((Enchantment) (Object) this).getDescriptionId())) {
			return;
		}
		ItemStack offhand = entity.getItemBySlot(EquipmentSlot.OFFHAND);
		if (!(offhand.getItem() instanceof ShieldItem)) {
			return;
		}
		Map<EquipmentSlot, ItemStack> current = cir.getReturnValue();
		if (current == null) {
			return;
		}
		if (current.containsKey(EquipmentSlot.OFFHAND)) {
			return;
		}
		Map<EquipmentSlot, ItemStack> widened = new LinkedHashMap<>(current);
		widened.put(EquipmentSlot.OFFHAND, offhand);
		cir.setReturnValue(widened);
	}
}
