package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.AiHook;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 第二批（2.5）三个附魔的生物行为：伪装、猫咪护符、吸猫体质。
 */
public final class AiRules {

	/** 头颅物品 → 它伪装成的生物族（用 instanceof 判定，覆盖同族变种）。 */
	private static boolean hidesFrom(ItemStack head, Mob mob) {
		if (head.is(Items.SKELETON_SKULL)) {
			// 骷髅族：骷髅、流浪者、沼骸等都继承 AbstractSkeleton
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

	private static boolean wears(Player player, EquipmentSlot slot, Enchantment enchantment) {
		if (enchantment == null) {
			return false;
		}
		ItemStack stack = player.getItemBySlot(slot);
		return !stack.isEmpty()
			&& EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0;
	}

	/** 吸猫体质的附魔对象，供驯服判定复用（register 之后才有值）。 */
	private static Enchantment attractionEnchantment;

	/**
	 * 该玩家是否穿着"吸猫体质"。
	 *
	 * @param player 玩家
	 * @return true 表示穿着
	 */
	public static boolean hasCatAttraction(Player player) {
		return attractionEnchantment != null
			&& wears(player, EquipmentSlot.LEGS, attractionEnchantment);
	}

	/** 规则只登记一次（换世界会再次启动服务器）。 */
	private static boolean registered;

	private AiRules() {
	}

	/**
	 * 登记三条规则。
	 *
	 * <p>上游版本的签名是 {@code register(HolderLookup.Provider)}：那时附魔要等数据包读完才能按 id 解析成
	 * Holder。1.20.1 的附魔在模组构造期就进了静态注册表，按 id 直接取对象即可，所以参数整个去掉——
	 * 调用方（{@code PracticalEnchantments#bootstrap}）也相应改成 {@code AiRules.register()}。</p>
	 */
	public static void register() {
		if (registered) {
			return;
		}
		registered = true;

		Enchantment disguise = PracticalEnchantments.resolveEnchantment(
			"practical_enchantments:disguise");
		Enchantment catCharm = PracticalEnchantments.resolveEnchantment(
			"practical_enchantments:cat_charm");
		Enchantment catAttraction = PracticalEnchantments.resolveEnchantment(
			"practical_enchantments:cat_attraction");
		attractionEnchantment = catAttraction;

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
