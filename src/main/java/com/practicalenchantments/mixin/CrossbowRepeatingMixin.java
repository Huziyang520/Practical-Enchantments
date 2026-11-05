// 连弩：装填时按等级多装箭，发射时一次只放出一根、其余保持上弦。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.RepeatingCrossbowEnchantment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;

@Mixin(CrossbowItem.class)
public abstract class CrossbowRepeatingMixin {
	@Unique
	private static ItemStack practicalenchantments$ammo = ItemStack.EMPTY;
	@Unique
	private static List<ItemStack> practicalenchantments$rest;

	@Inject(method = "tryLoadProjectiles", at = @At("HEAD"), require = 1)
	private static void practicalenchantments$captureAmmo(LivingEntity shooter, ItemStack held,
														  CallbackInfoReturnable<Boolean> cir) {
		practicalenchantments$ammo = shooter.getProjectile(held);
	}

	@Inject(method = "tryLoadProjectiles", at = @At("RETURN"), require = 1)
	private static void practicalenchantments$loadExtra(LivingEntity shooter, ItemStack held,
														CallbackInfoReturnable<Boolean> cir) {
		ItemStack ammo = practicalenchantments$ammo;
		practicalenchantments$ammo = ItemStack.EMPTY;
		if (!cir.getReturnValueZ() || ammo.isEmpty()) {
			return;
		}
		int extra = RepeatingCrossbowEnchantment.level(held);
		if (extra <= 0) {
			return;
		}
		int available = Math.min(extra, ammo.getCount());
		List<ItemStack> loaded = CrossbowChargedAccessor.practicalenchantments$charged(held);
		if (available <= 0 || loaded.isEmpty()) {
			return;
		}
		ItemStack template = loaded.get(0).copy();
		for (int i = 0; i < available; i++) {
			CrossbowChargedAccessor.practicalenchantments$add(held, template.copy());
		}
		boolean creative = shooter instanceof Player player && player.getAbilities().instabuild;
		if (!creative) {
			ammo.shrink(available);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), require = 1)
	private void practicalenchantments$peelOne(Level level, Player player, InteractionHand hand,
											   CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		practicalenchantments$rest = null;
		ItemStack held = player.getItemInHand(hand);
		if (RepeatingCrossbowEnchantment.level(held) <= 0) {
			return;
		}
		List<ItemStack> loaded = CrossbowChargedAccessor.practicalenchantments$charged(held);
		if (loaded.size() <= 1) {
			return;
		}
		practicalenchantments$rest = new ArrayList<>(loaded.subList(1, loaded.size()));
		CrossbowChargedAccessor.practicalenchantments$clear(held);
		CrossbowChargedAccessor.practicalenchantments$add(held, loaded.get(0).copy());
	}

	@Inject(method = "use", at = @At("RETURN"), require = 1)
	private void practicalenchantments$keepLoaded(Level level, Player player, InteractionHand hand,
												  CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (practicalenchantments$rest == null) {
			return;
		}
		List<ItemStack> remaining = new ArrayList<>(practicalenchantments$rest.size());
		for (ItemStack template : practicalenchantments$rest) {
			remaining.add(template.copy());
		}
		practicalenchantments$rest = null;
		ItemStack held = player.getItemInHand(hand);
		CrossbowChargedAccessor.practicalenchantments$clear(held);
		for (ItemStack item : remaining) {
			CrossbowChargedAccessor.practicalenchantments$add(held, item);
		}
	}
}
