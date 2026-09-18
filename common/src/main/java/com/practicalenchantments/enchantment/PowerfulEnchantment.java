package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentEffectsBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.ExclusiveGroupBuilder;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 强劲附魔 - 弩 V 级
 * 效果：弩专属远程伤害增幅，每级 +25% 箭矢伤害
 */
public final class PowerfulEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":powerful";

	/** 互斥组名（与 {@code PracticalEnchantments#onRegisterExclusiveGroups} 中注册的组名一致） */
	public static final String GROUP_NAME = "crossbow";

	/**
	 * 互斥组标签引用（由 {@link ExclusiveGroupBuilder} 派生，保证与注册的组名一致）。
	 *
	 * <p>为什么不直接用 {@link com.enchantlib.api.ExclusiveSets#CROSSBOW}：原版
	 * {@code #minecraft:exclusive_set/crossbow} 同时含多重射击与穿透，而本附魔按设计要求
	 * <b>可与穿透叠加</b>，只与多重射击互斥，故自建只含多重射击的互斥组。</p>
	 */
	public static final String EXCLUSIVE_SET = ExclusiveGroupBuilder
		.create(PracticalEnchantments.MOD_ID, GROUP_NAME)
		.getTagReference();

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("强劲")
			.supportedItems("minecraft:crossbow")
			.weight(10) // 常见
			.maxLevel(5)
			.minCost(1, 10)
			.maxCost(15, 10)
			.anvilCost(2)
			// 仅与多重射击互斥
			.exclusiveSet(EXCLUSIVE_SET)
			.slots("mainhand")
			.effects(EnchantmentEffectsBuilder.create().build()));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		// 使用 MODIFY_DAMAGE 事件处理远程伤害加成
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, PowerfulEnchantment::onModifyDamage);
	}

	private static void onModifyDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		// 只处理弹射物伤害（箭矢）
		DamageSource source = event.source();
		if (!source.is(DamageTypes.ARROW)) return;

		// 检查攻击者主手是否持有弩
		ItemStack mainHand = event.attacker().getItemBySlot(EquipmentSlot.MAINHAND);
		if (!(mainHand.getItem() instanceof CrossbowItem)) return;

		// 每级 +25% 伤害
		int level = ctx.level();
		float multiplier = 1.0f + (level * 0.25f);
		float currentDamage = event.damage().getValue();
		event.damage().setValue(currentDamage * multiplier);
	}
}
