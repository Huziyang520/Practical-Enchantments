// 连弩：装填时按等级多装箭，发射时一次只放出一根、其余保持上弦。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.weapon.RepeatingCrossbowEnchantment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.ChargedProjectiles;
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
	private static List<ItemStackTemplate> practicalenchantments$rest;

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
		ChargedProjectiles current = held.get(DataComponents.CHARGED_PROJECTILES);
		if (current == null || current.isEmpty()) {
			return;
		}
		List<ItemStackTemplate> loaded = current.items();
		// getProjectile() 只回一份，多出来的箭要自己从背包里扣。
		int available = practicalenchantments$takeAmmo(shooter, ammo, extra);
		if (available <= 0) {
			return;
		}
		ItemStackTemplate template = loaded.get(0);
		List<ItemStack> merged = new ArrayList<>(loaded.size() + available);
		for (ItemStackTemplate item : loaded) {
			merged.add(item.create());
		}
		for (int i = 0; i < available; i++) {
			merged.add(template.create());
		}
		held.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(merged));
	}

	@Unique
	private static int practicalenchantments$takeAmmo(LivingEntity shooter, ItemStack template, int want) {
		if (want <= 0 || template.isEmpty()) {
			return 0;
		}
		if (shooter.hasInfiniteMaterials()) {
			return want;
		}
		if (!(shooter instanceof Player player)) {
			int take = Math.min(want, Math.max(0, template.getCount() - 1));
			template.shrink(take);
			return take;
		}
		Inventory inventory = player.getInventory();
		int taken = 0;
		for (int i = 0; i < inventory.getContainerSize() && taken < want; i++) {
			ItemStack slot = inventory.getItem(i);
			if (slot.isEmpty() || slot == template || !slot.is(template.getItem())) {
				continue;
			}
			int take = Math.min(want - taken, slot.getCount());
			slot.shrink(take);
			taken += take;
		}
		return taken;
	}

	@Inject(method = "use", at = @At("HEAD"), require = 1)
	private void practicalenchantments$peelOne(Level level, Player player, InteractionHand hand,
											   CallbackInfoReturnable<InteractionResult> cir) {
		practicalenchantments$rest = null;
		ItemStack held = player.getItemInHand(hand);
		if (RepeatingCrossbowEnchantment.level(held) <= 0) {
			return;
		}
		ChargedProjectiles current = held.get(DataComponents.CHARGED_PROJECTILES);
		if (current == null) {
			return;
		}
		List<ItemStackTemplate> loaded = current.items();
		if (loaded.size() <= 1) {
			return;
		}
		practicalenchantments$rest = new ArrayList<>(loaded.subList(1, loaded.size()));
		held.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(List.of(loaded.get(0).create())));
	}

	@Inject(method = "use", at = @At("RETURN"), require = 1)
	private void practicalenchantments$keepLoaded(Level level, Player player, InteractionHand hand,
												  CallbackInfoReturnable<InteractionResult> cir) {
		if (practicalenchantments$rest == null) {
			return;
		}
		List<ItemStack> remaining = new ArrayList<>(practicalenchantments$rest.size());
		for (ItemStackTemplate template : practicalenchantments$rest) {
			remaining.add(template.create());
		}
		practicalenchantments$rest = null;
		player.getItemInHand(hand).set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(remaining));
	}
}
