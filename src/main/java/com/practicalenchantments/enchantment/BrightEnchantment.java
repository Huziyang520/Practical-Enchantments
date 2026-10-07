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
 *
 * <p><b>只回收自己授予的那份夜视</b>：玩家自己用 {@code /effect give}、药水、信标或其它模组
 * 拿到的夜视（尤其是 infinite 永久夜视）一律不覆盖、不删除。判定靠
 * {@link EntityCounter} 标记 + "当前实例是否就是本附魔施加的短时长实例"两道条件。</p>
 *
 * <p>授予方式用 <b>有限时长 + 定期刷新</b>（20 秒 / 每 20 tick 刷新）而不是无限时长：
 * 标记不持久化（玩家离线即清），用有限时长兜底可保证任何情况下夜视都会自然过期，
 * 不会出现"永久残留"。剩余时长始终 ≥380 tick，不会进入原版图标闪烁区间（&lt;200 tick）。</p>
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 * <ol>
 * <li><b>{@code registerCallbacks} 去掉 {@code HolderLookup.Provider} 参数</b>，改用
 * {@code PracticalEnchantments.resolveEnchantment(ID)}（原因见 {@code AerialHasteEnchantment}
 * 类注释第 1 条）。</li>
 *
 * <li><b>{@code Holder<Enchantment>} → {@code Enchantment}</b>：见 {@code VenomEnchantment}
 * 类注释第 2 条。</li>
 *
 * <li><b>{@code Identifier} → {@code ResourceLocation}；{@code Identifier.fromNamespaceAndPath(a, b)}
 * → {@code new ResourceLocation(a, b)}。</b>26.3 把 {@code ResourceLocation} 改名成了
 * {@code Identifier}（{@code fromNamespaceAndPath} 是它的静态工厂）。1.20.1 还是旧名，
 * 构造器 {@code ResourceLocation(String, String)} 已用 javap 确认存在。
 * {@link EntityCounter} 的键类型在本版本也正是 {@code ResourceLocation}，两边对得上。</li>
 *
 * <li><b>⚠️ 读附魔等级的方式换了：{@code helmet.getEnchantments().getLevel(HOLDER)} →
 * {@code EnchantmentHelper.getItemEnchantmentLevel(HOLDER, helmet)}。</b>26.3 的
 * {@code ItemStack#getEnchantments()} 返回的是数据组件 {@code ItemEnchantments}，自带
 * {@code getLevel(Holder)}；1.20.1 <b>根本没有 {@code ItemStack.getEnchantments()} 这个方法</b>
 * （附魔存在 NBT 里），等级要走 {@link EnchantmentHelper#getItemEnchantmentLevel(Enchantment, ItemStack)}。
 * 语义一致：未附魔返回 0。{@code HOLDER != null} 的短路保留原样，虽然本版本的
 * {@code getItemEnchantmentLevel} 对 null 附魔也不会抛异常。</li>
 *
 * <li><b>⚠️ 耐久扣减换了签名：{@code helmet.hurtAndBreak(1, player, EquipmentSlot.HEAD)} →
 * {@code helmet.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.HEAD))}。</b>
 * 1.20.1 的 {@code ItemStack} 只有
 * {@code <T extends LivingEntity> void hurtAndBreak(int, T, Consumer<T>)} 这一个重载
 * （已用 javap 列出全部方法确认，收 {@code EquipmentSlot} 的三参形式不存在）。第三个参数是
 * "物品损坏时"的回调，原版自己的用法就是
 * {@code stack.hurtAndBreak(amount, player, p -> p.broadcastBreakEvent(slot))}
 * （见 {@code Inventory#hurtArmor} 的字节码），所以这里照抄同一写法，
 * 损坏时照旧广播 {@code EquipmentSlot.HEAD} 的装备损坏事件（音效 + 统计）。</li>
 *
 * <li><b>没有其它改动。</b>{@code GlobalEvents.enableLivingEntityTick()}、
 * {@code GlobalEvents.LIVING_ENTITY_TICK.register(...)}、
 * {@code LivingEntityTickEvent.entity()/tickCount()}、{@code player.level().getGameTime()}
 * （1.20.1 的 {@code Level.getGameTime()} 返回 {@code long}，与 26.3 一致，故
 * {@code % 24000} 的结果类型也不用改）、{@code getEffect/addEffect/removeEffect}、
 * {@code MobEffectInstance} 五参构造器、{@code isInfiniteDuration()/getAmplifier()/getDuration()}
 * 全部同形。</li>
 * </ol>
 */
public final class BrightEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":bright";

	/** 本附魔对象（供判断头盔是否佩戴本附魔）。26.3 为 {@code Holder<Enchantment>}。 */
	private static Enchantment HOLDER;

	/** 夜视时长（20 秒）：有限时长 + 刷新，保证任何情况下都会自然过期 */
	private static final int NIGHT_VISION_DURATION = 400;

	/** 刷新间隔（tick）：每 20 tick 一次，剩余时长始终 ≥380 tick，不会闪烁 */
	private static final int REFRESH_INTERVAL = 20;

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

			// 夜间（13000~23000 刻）每秒扣除 1 点耐久
			long timeOfDay = player.level().getGameTime() % 24000;
			if (timeOfDay >= 13000 && timeOfDay <= 23000 && event.tickCount() % 20 == 0) {
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