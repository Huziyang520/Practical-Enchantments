package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentEffectsBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 明朗附魔 - 头盔 I 级
 */
public final class BrightEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":bright";

	/** 本附魔的 Holder（供判断头盔是否佩戴本附魔） */
	private static Holder<Enchantment> HOLDER;

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("明朗")
			.supportedItems("#minecraft:enchantable/head_armor")
			.weight(10) // 常见
			.maxLevel(1)
			.minCost(10, 0)
			.maxCost(25, 0)
			.anvilCost(2)
			.slots("head")
			.effects(EnchantmentEffectsBuilder.create().build()));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		// 使用全局 LIVING_ENTITY_TICK：脱下头盔（即便身上无其他附魔装备）也能移除无限夜视
		EnchantLibEvents.enableLivingEntityTick();
		EnchantLibEvents.LIVING_ENTITY_TICK.register(BrightEnchantment::onLivingTick);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;

		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		boolean hasBright = !helmet.isEmpty()
			&& HOLDER != null
			&& helmet.getEnchantments().getLevel(HOLDER) > 0;

		if (hasBright) {
			// 夜视常驻且无限时长：只在缺失时施加一次，避免反复刷新造成闪烁
			if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
				player.addEffect(new MobEffectInstance(
					MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION, 0, true, false));
			}

			// 夜间（13000~23000 刻）每秒扣除 1 点耐久
			long timeOfDay = player.level().getGameTime() % 24000;
			if (timeOfDay >= 13000 && timeOfDay <= 23000 && event.tickCount() % 20 == 0) {
				helmet.hurtAndBreak(1, player, EquipmentSlot.HEAD);
			}
		} else {
			// 摘下头盔或耐久归零：移除本附魔添加的无限夜视（仅移除无限时长的，不影响夜视药水）
			MobEffectInstance current = player.getEffect(MobEffects.NIGHT_VISION);
			if (current != null && current.isInfiniteDuration()) {
				player.removeEffect(MobEffects.NIGHT_VISION);
			}
		}
	}
}
