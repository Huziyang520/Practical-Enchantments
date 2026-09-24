package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.EntityCounter;
import com.enchantlib.api.ExclusiveGroupBuilder;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 羽落附魔 - 靴子单级（娱乐，仅创造/指令获取）。
 *
 * <p>穿着时持续获得缓降，免疫摔落伤害；潜行（Shift）时缓降失效以恢复正常下落速度。
 * 与原版摔落缓冲（羽毛掉落）互斥。</p>
 *
 * <p><b>只回收自己授予的那份缓降</b>：玩家自己用 {@code /effect give}、药水或其它模组拿到的缓降
 * （尤其是 infinite 永久缓降）一律不覆盖、不删除。判定靠 {@link EntityCounter} 标记 +
 * "当前实例是否就是本附魔施加的短时长实例"两道条件（同「明朗」的写法，见坑 19 的思路）。</p>
 *
 * <p>授予方式用 <b>有限时长 + 定期刷新</b>（20 秒 / 每 20 tick 刷新）而不是无限时长：
 * 标记不持久化（玩家离线即清），有限时长兜底保证任何情况下缓降都会自然过期、不会永久残留。
 * 剩余时长始终 ≥380 tick，不会进入原版图标闪烁区间（&lt;200 tick）。</p>
 */
public final class GentleDescentEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":gentle_descent";

	public static final String EXCLUSIVE_SET = ExclusiveGroupBuilder
		.create(PracticalEnchantments.MOD_ID, "slow_fall")
		.getTagReference();

	private static Holder<Enchantment> HOLDER;

	/** 缓降时长（20 秒）：有限时长 + 刷新，保证任何情况下都会自然过期 */
	private static final int SLOW_FALLING_DURATION = 400;

	/** 刷新间隔（tick）：每 20 tick 一次，剩余时长始终 ≥380 tick，不会闪烁 */
	private static final int REFRESH_INTERVAL = 20;

	/** EntityCounter 标记：当前缓降由本附魔授予（只撤销自己授予的） */
	private static final Identifier SF_GRANTED =
		Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "gentle_descent_sf_granted");

	private GentleDescentEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("羽落")
			.supportedItems("#minecraft:enchantable/foot_armor")
			.weight(1)
			.maxLevel(1)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(2)
			.exclusiveSet(EXCLUSIVE_SET)
			.slots("feet"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		EnchantLibEvents.enableLivingEntityTick();
		EnchantLibEvents.LIVING_ENTITY_TICK.register(GentleDescentEnchantment::onLivingTick);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		var entity = event.entity();
		ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
		boolean hasGentle = HOLDER != null && !boots.isEmpty()
			&& boots.getEnchantments().getLevel(HOLDER) > 0;

		MobEffectInstance current = entity.getEffect(MobEffects.SLOW_FALLING);

		if (hasGentle && !entity.isShiftKeyDown()) {
			// 只在"没有缓降"或"现有缓降就是本附魔给的短时长缓降"时施加 / 刷新。
			// 玩家自己拿到的缓降（药水 / 指令 / 其它模组，尤其 infinite）一律不覆盖、不降级。
			if (current == null) {
				entity.addEffect(new MobEffectInstance(
					MobEffects.SLOW_FALLING, SLOW_FALLING_DURATION, 0, true, false));
			} else if (isOurs(current) && event.tickCount() % REFRESH_INTERVAL == 0) {
				entity.addEffect(new MobEffectInstance(
					MobEffects.SLOW_FALLING, SLOW_FALLING_DURATION, 0, true, false));
			}
			EntityCounter.set(entity, SF_GRANTED, 1);
		} else {
			// 潜行 / 脱靴：只回收本附魔授予的那一份。
			// ⚠️ 不能无条件删除"所有无限时长缓降"——那会连玩家自己上的永久缓降一起删掉。
			if (current != null && isOurs(current) && EntityCounter.get(entity, SF_GRANTED) > 0) {
				entity.removeEffect(MobEffects.SLOW_FALLING);
			}
			EntityCounter.set(entity, SF_GRANTED, 0);
		}
	}

	/**
	 * 判断当前状态效果是否就是本附魔施加的那一份（非无限时长、0 级、不超过本附魔给的时长）。
	 *
	 * <p>玩家自己用指令 / 药水拿到的缓降要么是无限时长、要么时长明显长于
	 * {@link #SLOW_FALLING_DURATION}，因此不会被误判、更不会被删除。</p>
	 */
	private static boolean isOurs(MobEffectInstance instance) {
		return !instance.isInfiniteDuration()
			&& instance.getAmplifier() == 0
			&& instance.getDuration() <= SLOW_FALLING_DURATION;
	}
}
