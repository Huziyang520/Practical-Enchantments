package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentEffectsBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.EntityCounter;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 明朗附魔 - 头盔 I 级
 *
 * <p><b>只回收自己授予的那份夜视</b>：玩家自己用 {@code /effect give}、药水、信标或其它模组
 * 拿到的夜视（尤其是 infinite 永久夜视）一律不覆盖、不删除。判定靠
 * {@link EntityCounter} 标记 + "当前实例是否就是本附魔施加的短时长实例"两道条件。</p>
 *
 * <p>授予方式用 <b>有限时长 + 定期刷新</b>（20 秒 / 每 20 tick 刷新）而不是无限时长：
 * 标记不持久化（玩家离线即清），用有限时长兜底可保证任何情况下夜视都会自然过期，
 * 不会出现"永久残留"。剩余时长始终 ≥380 tick，不会进入原版图标闪烁区间（&lt;200 tick）。</p>
 */
public final class BrightEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":bright";

	/** 本附魔的 Holder（供判断头盔是否佩戴本附魔） */
	private static Holder<Enchantment> HOLDER;

	/** 夜视时长（20 秒）：有限时长 + 刷新，保证任何情况下都会自然过期 */
	private static final int NIGHT_VISION_DURATION = 400;

	/** 刷新间隔（tick）：每 20 tick 一次，剩余时长始终 ≥380 tick，不会闪烁 */
	private static final int REFRESH_INTERVAL = 20;

	/** EntityCounter 标记：当前夜视由本附魔授予（只撤销自己授予的，同坑 19 的思路） */
	private static final Identifier NV_GRANTED =
		Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "bright_nv_granted");

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
		// 使用全局 LIVING_ENTITY_TICK：脱下头盔（即便身上无其他附魔装备）也能移除夜视
		EnchantLibEvents.enableLivingEntityTick();
		EnchantLibEvents.LIVING_ENTITY_TICK.register(BrightEnchantment::onLivingTick);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;

		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		boolean hasBright = !helmet.isEmpty()
			&& HOLDER != null
			&& helmet.getEnchantments().getLevel(HOLDER) > 0;

		MobEffectInstance current = player.getEffect(MobEffects.NIGHT_VISION);

		if (hasBright) {
			// 只在"没有夜视"或"现有夜视就是本附魔给的短时长夜视"时施加 / 刷新。
			// 玩家自己拿到的夜视（药水 / 指令 / 信标 / 其它模组，尤其 infinite）一律不覆盖、不降级。
			if (current == null) {
				player.addEffect(new MobEffectInstance(
					MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION, 0, true, false));
			} else if (isOurs(current) && event.tickCount() % REFRESH_INTERVAL == 0) {
				player.addEffect(new MobEffectInstance(
					MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION, 0, true, false));
			}
			EntityCounter.set(player, NV_GRANTED, 1);

			// 夜间（13000~23000 刻）每秒扣除 1 点耐久
			long timeOfDay = player.level().getGameTime() % 24000;
			if (timeOfDay >= 13000 && timeOfDay <= 23000 && event.tickCount() % 20 == 0) {
				helmet.hurtAndBreak(1, player, EquipmentSlot.HEAD);
			}
		} else {
			// 摘下头盔或耐久归零：只回收本附魔授予的那份。
			// ⚠️ 不能无条件删除"所有无限时长夜视"——那会连玩家自己上的永久夜视一起删掉。
			if (current != null && isOurs(current) && EntityCounter.get(player, NV_GRANTED) > 0) {
				player.removeEffect(MobEffects.NIGHT_VISION);
			}
			EntityCounter.set(player, NV_GRANTED, 0);
		}
	}

	/**
	 * 判断当前状态效果是否就是本附魔施加的那一份（非无限时长、0 级、不超过本附魔给的时长）。
	 *
	 * <p>玩家自己用指令 / 药水拿到的夜视要么是无限时长、要么时长明显长于
	 * {@link #NIGHT_VISION_DURATION}，因此不会被误判、更不会被删除。</p>
	 */
	private static boolean isOurs(MobEffectInstance instance) {
		return !instance.isInfiniteDuration()
			&& instance.getAmplifier() == 0
			&& instance.getDuration() <= NIGHT_VISION_DURATION;
	}
}
