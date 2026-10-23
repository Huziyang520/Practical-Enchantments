package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 明朗附魔
 */
public final class BrightEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":bright";

	/** 本附魔对象（供判断头盔是否佩戴本附魔）。 */
	private static Enchantment HOLDER;

	/** 夜视时长（20 秒）：有限时长 + 刷新，保证任何情况下都会自然过期 */
	private static final int NIGHT_VISION_DURATION = 400;

	/** 刷新间隔（tick）：每 20 tick 一次，剩余时长始终 ≥380 tick，不会闪烁 */
	private static final int REFRESH_INTERVAL = 20;

	/**
	 * 夜间扣除 1 点耐久的间隔（tick）。
	 *
	 * <p>275 tick = 13.75 秒。原实现是 20 tick（1 秒）一次，一夜（13000~23000 刻，共 10000 tick）
	 * 要扣 500 点耐久，任何头盔都会在一夜内报废；13.75 秒一次是同一条夜晚区间里约 36 点，
	 * 与「一夜消耗一件钻石头盔的量级」相符，也是用户指定的数值。</p>
	 */
	private static final int DURABILITY_INTERVAL = 275;

	/** EntityCounter 标记：当前夜视由本附魔授予（只撤销自己授予的，同坑 19 的思路） */
	private static final ResourceLocation NV_GRANTED =
		new ResourceLocation(PracticalEnchantments.MOD_ID, "bright_nv_granted");


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		HOLDER = PracticalEnchantments.resolveEnchantment(ID);
		// 使用全局 LIVING_ENTITY_TICK：脱下头盔（即便身上无其他附魔装备）也能移除夜视
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(BrightEnchantment::onLivingTick);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;

		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		boolean hasBright = !helmet.isEmpty()
			&& HOLDER != null
			&& EnchantmentHelper.getItemEnchantmentLevel(HOLDER, helmet) > 0;

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

			// 夜间（13000~23000 刻）每 13.75 秒扣除 1 点耐久
			long timeOfDay = player.level().getGameTime() % 24000;
			if (timeOfDay >= 13000 && timeOfDay <= 23000 && event.tickCount() % DURABILITY_INTERVAL == 0) {
				helmet.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.HEAD));
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