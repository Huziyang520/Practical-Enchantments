package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * 掉落不死亡附魔 - 任意装备 Ⅲ 级（娱乐，仅创造/指令获取）。
 *
 * <p>受到致命伤害时免死一次：摧毁携带该附魔的那件装备，背包与全身装备全部
 * 掉落脚边，并获得黑暗效果（20/16/12 秒，Ⅰ/Ⅱ/Ⅲ）。与「恶魔交易」互斥。</p>
 *
 * <p>由 {@code DeathSaveMixin} 在 {@code LivingEntity.die} HEAD 处调用。</p>
 */
public final class UndyingDropEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":undying_drop";

	private UndyingDropEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("掉落不死亡")
			.supportedItems("#minecraft:enchantable/armor")
			.weight(1)
			.maxLevel(3)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(3)
			.exclusiveSet(DemonicPactEnchantment.EXCLUSIVE_SET)
			.slots("head", "chest", "legs", "feet"));
	}

	/**
	 * 尝试触发掉落不死亡。
	 *
	 * @return true 表示已免死（Mixin 应取消死亡）
	 */
	public static boolean trySave(ServerPlayer player, DamageSource source) {
		EquipmentSlot enchantedSlot = DeathSaveSupport.findArmorWith(player, "undying_drop");
		if (enchantedSlot == null) {
			return false;
		}
		// 虚空 / /kill 等绕过无敌的伤害不救
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}

		// 先读等级（摧毁前），决定黑暗时长
		int level = DeathSaveSupport.getLevel(player.getItemBySlot(enchantedSlot), "undying_drop");
		int darknessTicks = (20 - 4 * (level - 1)) * 20;

		// 摧毁附魔装备 + 掉落全身物品
		DeathSaveSupport.dropEverything(player, enchantedSlot);

		// 免死 + 黑暗
		DeathSaveSupport.performResurrection(player, darknessTicks);
		return true;
	}
}
