// 强劲附魔 - 弩 V 级 效果：弩专属远程伤害增幅，每级 +25% 箭矢伤害

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class PowerfulEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":powerful";
	public static final String GROUP_NAME = "crossbow";

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, PowerfulEnchantment::onModifyDamage);
	}

	private static void onModifyDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		DamageSource source = event.source();
		if (!source.is(DamageTypes.ARROW)) return;
		ItemStack mainHand = event.attacker().getItemBySlot(EquipmentSlot.MAINHAND);
		if (!(mainHand.getItem() instanceof CrossbowItem)) return;
		int level = ctx.level();
		float multiplier = 1.0f + (level * 0.25f);
		float currentDamage = event.damage().getValue();
		event.damage().setValue(currentDamage * multiplier);
	}
}
