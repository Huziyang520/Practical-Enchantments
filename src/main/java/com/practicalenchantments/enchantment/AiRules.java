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
 *
 * <p>三者的判定都只回答 MerlinLib 的问题（见 {@link AiHook}），不自己动 AI，也不写 mixin：
 * 库负责在寻敌、躲避、转向三处挂钩，本模组只说"谁不该打穿这顶头的人"。</p>
 *
 * <p>伪装按头颅与生物族的对应关系生效：骷髅头对骷髅/流浪者、凋灵骷髅头对凋灵骷髅（**对凋灵无效**）、
 * 僵尸头对僵尸族、苦力怕头对苦力怕、猪灵头对猪灵**与蛮兵**；末影龙头与玩家头不对应任何生物。
 * 玩家主动攻击过的生物仍然会还手——{@code getLastHurtByMob} 判断。</p>
 *
 * <h2>1.20.1 移植：附魔句柄从 Holder 换成普通对象</h2>
 *
 * <p>与 26.3 的差异只有一处，但它连着改了三行，如实记在这里：</p>
 * <ol>
 *   <li><b>不再有 {@code Holder<Enchantment>}。</b>26.3 的附魔住在数据包驱动的动态注册表里，只能拿
 *       Holder；1.20.1 没有这个注册表，附魔是模组构造期进静态注册表的普通 {@link Enchantment} 对象，
 *       所以字段与参数类型都换成 {@code Enchantment}，{@code register} 也不再需要
 *       {@code HolderLookup.Provider}。</li>
 *   <li><b>附魔等级改用 {@link EnchantmentHelper#getItemEnchantmentLevel(Enchantment, ItemStack)}。</b>
 *       26.3 的 {@code stack.getEnchantments().getLevel(holder)} 是数据组件写法；1.20.1 的附魔存在 NBT 里，
 *       由 {@code EnchantmentHelper} 按对象取等级，语义一致。</li>
 *   <li><b>{@code Cat} 与 {@code Ocelot} 换了包。</b>26.3 在
 *       {@code net.minecraft.world.entity.animal.feline} 下，1.20.1 在
 *       {@code net.minecraft.world.entity.animal} 下（已用 javap 确认）。两者都只做 instanceof 判断，
 *       行为不受影响。</li>
 * </ol>
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
	 * <p>26.3 的签名是 {@code register(HolderLookup.Provider)}：那时附魔要等数据包读完才能按 id 解析成
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
