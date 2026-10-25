// 免死类附魔（恶魔交易 / 掉落不死亡）共用工具。

package com.practicalenchantments.enchantment.support;

import com.practicalenchantments.PracticalEnchantments;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class DeathSaveSupport {
	private static final byte TOTEM_ANIMATION_EVENT = 35;

	public static final EquipmentSlot[] ARMOR_SLOTS = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private static final EquipmentSlot[] EQUIPMENT_SLOTS = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
	};

	private DeathSaveSupport() {
	}

	public static int getLevel(ItemStack stack, String path) {
		return getLevelFull(stack, PracticalEnchantments.MOD_ID + ":" + path);
	}

	public static int getLevelFull(ItemStack stack, String fullId) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		for (var entry : stack.getEnchantments().entrySet()) {
			Holder<Enchantment> holder = entry.getKey();
			boolean match = holder.unwrapKey()
				.map(key -> {
					Identifier id = key.identifier();
					return (id.getNamespace() + ":" + id.getPath()).equals(fullId);
				})
				.orElse(false);
			if (match) {
				return entry.getIntValue();
			}
		}
		return 0;
	}

	public static EquipmentSlot findArmorWith(ServerPlayer player, String path) {
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			if (getLevel(player.getItemBySlot(slot), path) > 0) {
				return slot;
			}
		}
		return null;
	}

	public static void performResurrection(ServerPlayer player, int darknessTicks) {
		player.setHealth(1.0F);
		player.removeEffect(MobEffects.REGENERATION);
		player.removeEffect(MobEffects.ABSORPTION);
		player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
		player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
		player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
		if (darknessTicks > 0) {
			player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, darknessTicks, 0));
		}
		if (player.level() instanceof ServerLevel serverLevel) {
			serverLevel.broadcastEntityEvent(player, TOTEM_ANIMATION_EVENT);
		}
	}

	public static void dropEverything(ServerPlayer player, EquipmentSlot destroyedSlot) {
		List<ItemStack> drops = new ArrayList<>();

		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			if (!stack.isEmpty()) {
				drops.add(stack.copy());
				stack.setCount(0);
			}
		}

		for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
			ItemStack stack = player.getItemBySlot(slot);
			if (stack.isEmpty()) {
				continue;
			}
			if (slot == destroyedSlot) {
				player.setItemSlot(slot, ItemStack.EMPTY);
			} else {
				drops.add(stack.copy());
				player.setItemSlot(slot, ItemStack.EMPTY);
			}
		}

		for (ItemStack drop : drops) {
			player.drop(drop, true);
		}
	}
}
