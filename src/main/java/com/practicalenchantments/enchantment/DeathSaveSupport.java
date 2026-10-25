// 免死类附魔（恶魔交易 / 掉落不死亡）共用工具。

package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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
		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
			ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey());
			if (id != null && (id.getNamespace() + ":" + id.getPath()).equals(fullId)) {
				return entry.getValue();
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

		for (ItemStack stack : player.getInventory().items) {
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
			player.drop(drop, true, false);
		}
	}
}
