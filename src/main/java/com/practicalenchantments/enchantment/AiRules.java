// 第二批（2.5）三个附魔的生物行为：伪装、猫咪护符、吸猫体质。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.AiHook;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class AiRules {
	private static boolean hidesFrom(ItemStack head, Mob mob) {
		if (head.is(Items.SKELETON_SKULL)) {
			return mob instanceof AbstractSkeleton && !(mob instanceof WitherSkeleton);
		}
		if (head.is(Items.WITHER_SKELETON_SKULL)) {
			return mob instanceof WitherSkeleton;
		}
		if (head.is(Items.ZOMBIE_HEAD)) {
			return mob instanceof Zombie;
		}
		if (head.is(Items.CREEPER_HEAD)) {
			return mob instanceof Creeper;
		}
		if (head.is(Items.PIGLIN_HEAD)) {
			return mob instanceof Piglin || mob instanceof PiglinBrute;
		}
		return false;
	}

	private static boolean wears(Player player, EquipmentSlot slot, Holder<Enchantment> enchantment) {
		ItemStack stack = player.getItemBySlot(slot);
		return !stack.isEmpty()
			&& stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(enchantment) > 0;
	}

	private static Holder<Enchantment> attractionHolder;

	public static boolean hasCatAttraction(Player player) {
		return attractionHolder != null && wears(player, EquipmentSlot.LEGS, attractionHolder);
	}

	private static boolean registered;

	private AiRules() {
	}

	public static void register(HolderLookup.Provider registries) {
		if (registered) {
			return;
		}
		registered = true;

		Holder<Enchantment> disguise = PracticalEnchantments.resolveEnchantment(registries,
			"practical_enchantments:disguise");
		Holder<Enchantment> catCharm = PracticalEnchantments.resolveEnchantment(registries,
			"practical_enchantments:cat_charm");
		Holder<Enchantment> catAttraction = PracticalEnchantments.resolveEnchantment(registries,
			"practical_enchantments:cat_attraction");
		attractionHolder = catAttraction;

		MerlinApi.ai().register(new AiHook() {
			@Override
			public boolean allowsTargeting(Mob mob, LivingEntity target) {
				if (!(target instanceof Player player)) {
					return true;
				}
				if ((mob instanceof Creeper || mob instanceof Phantom)
					&& wears(player, EquipmentSlot.HEAD, catCharm)) {
					return false;
				}
				if (mob.getLastHurtByMob() == player) {
					return true;
				}
				return !wears(player, EquipmentSlot.HEAD, disguise)
					|| !hidesFrom(player.getItemBySlot(EquipmentSlot.HEAD), mob);
			}

			@Override
			public boolean wantsToFlee(Mob mob, LivingEntity entity) {
				return entity instanceof Player player
					&& (mob instanceof Creeper || mob instanceof Phantom)
					&& wears(player, EquipmentSlot.HEAD, catCharm);
			}

			@Override
			public boolean allowsAvoiding(Mob mob, LivingEntity entity) {
				return !(mob instanceof Cat || mob instanceof Ocelot) || !(entity instanceof Player player)
					|| !wears(player, EquipmentSlot.LEGS, catAttraction);
			}
		});
	}
}
