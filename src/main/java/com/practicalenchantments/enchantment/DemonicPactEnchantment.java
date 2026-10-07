package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 恶魔交易附魔 - 胸甲 Ⅲ 级（宝藏）。
 *
 * <p>受到致命伤害且背包绿宝石达到消耗值时免死一次：触发不死图腾效果 + 黑暗，
 * 消耗 64/56/48 个绿宝石（Ⅰ/Ⅱ/Ⅲ），60 秒（1200 tick）冷却；
 * 冷却内或绿宝石不足则正常死亡。与「掉落不死亡」互斥。</p>
 *
 * <p>由 {@code DeathSaveMixin} 在 {@code LivingEntity.die} HEAD 处调用，
 * 冷却时间戳存内存（重启重置，与盾牌冷却同档）。</p>
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 *
 * <p>本类<b>没有</b> {@code registerCallbacks}——26.3 也没有，它的入口是 Mixin，
 * 不是事件回调。这一点是刻意的，不是漏写。</p>
 *
 * <ol>
 * <li><b>⚠️ {@code player.getInventory().getNonEquipmentItems()} →
 * {@code player.getInventory().items}（两处）。</b>1.20.1 的 {@code Inventory} 没有
 * {@code getNonEquipmentItems()}（已用 javap 列出全部成员确认；{@code items} / {@code armor} /
 * {@code offhand} 都是 {@code public final NonNullList<ItemStack>} 公开字段）。
 *
 * <p><b>"要不要连副手一起数"这个问题已经查清，结论是：不用，而且连了反而是错的。</b>
 * 拿 1.21.11 的 {@code Inventory} 反编译核对，{@code getNonEquipmentItems()} 的方法体就是
 * <b>一句</b> {@code return this.items;}（javap -c 实证：{@code aload_0; getfield items; areturn}）。
 * 也就是说这个名字听起来像"全部非装备物品"，实际语义<b>只是主背包 36 格，不含副手</b>
 * （1.21.11 里副手仍是独立的 {@code offhand} 表）。所以 {@code items} 与它<b>逐字等价</b>，
 * 本移植不需要为副手做任何补偿。</p>
 *
 * <p>反过来说，如果照"名字"去猜、把 {@code offhand} 也加进去，就会变成一处真实的
 * <b>行为增强</b>（副手里的绿宝石也能被计入并消耗）。本移植<b>没有</b>这样做。</p></li>
 *
 * <li><b>{@code DamageTypeTags.BYPASSES_INVULNERABILITY} 在 1.20.1 存在</b>
 * （已用 javap 确认，同包还有 {@code BYPASSES_ARMOR} 等），虚空 / {@code /kill} 不救的逻辑
 * 一字未改。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code player.getItemBySlot(EquipmentSlot.CHEST)}、
 * {@code DeathSaveSupport.getLevel(stack, "demonic_pact")}、{@code player.level().getGameTime()}
 * （返回 {@code long}，与 26.3 一致，故 {@code now - last} 的类型不用动）、
 * {@code ConcurrentHashMap<UUID, Long>}、{@code stack.is(Items.EMERALD)}、
 * {@code stack.getCount()/shrink(int)}、{@code DeathSaveSupport.performResurrection} 全部同形。
 * 那个 {@code net.minecraft.world.entity.EquipmentSlot} 的全限定名也照原样保留了。</li>
 *
 * <li><b>签名必须保持逐字不变</b>：{@code public static boolean trySave(ServerPlayer, DamageSource)}，
 * {@code DeathSaveMixin} 就是按这个签名调的。</li>
 * </ol>
 */
public final class DemonicPactEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":demonic_pact";

	/** 互斥组：恶魔交易 ↔ 掉落不死亡 */

	/** 冷却（tick） */
	private static final long COOLDOWN_TICKS = 1200L;

	/** 玩家 UUID → 上次触发的 gameTime */
	private static final ConcurrentHashMap<UUID, Long> LAST_TRIGGER = new ConcurrentHashMap<>();

	private DemonicPactEnchantment() {
	}


	/**
	 * 尝试触发恶魔交易。
	 *
	 * @return true 表示已免死（Mixin 应取消死亡）
	 */
	public static boolean trySave(ServerPlayer player, DamageSource source) {
		ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
		int level = DeathSaveSupport.getLevel(chestplate, "demonic_pact");
		if (level <= 0) {
			return false;
		}
		// 虚空 / /kill 等绕过无敌的伤害不救（与不死图腾一致）
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}

		long now = player.level().getGameTime();
		Long last = LAST_TRIGGER.get(player.getUUID());
		if (last != null && now - last < COOLDOWN_TICKS) {
			return false;
		}

		int cost = 64 - 8 * (level - 1);
		if (countEmeralds(player) < cost) {
			return false;
		}
		consumeEmeralds(player, cost);

		int darknessTicks = (20 - 4 * (level - 1)) * 20;
		DeathSaveSupport.performResurrection(player, darknessTicks);
		LAST_TRIGGER.put(player.getUUID(), now);
		return true;
	}

	private static int countEmeralds(ServerPlayer player) {
		int count = 0;
		// 26.3 用 getInventory().getNonEquipmentItems()；1.20.1 的等价物是公开字段 items。
		// 已核实 1.21.11 的 getNonEquipmentItems() 就是 `return this.items;`，即主背包 36 格、
		// 不含副手，故此处不需要再遍历 offhand（多遍历会变成行为增强，见类注释第 1 条）。
		for (ItemStack stack : player.getInventory().items) {
			if (!stack.isEmpty() && stack.is(Items.EMERALD)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	private static void consumeEmeralds(ServerPlayer player, int amount) {
		int remaining = amount;
		var items = player.getInventory().items;
		for (int i = 0; i < items.size() && remaining > 0; i++) {
			ItemStack stack = items.get(i);
			if (stack.isEmpty() || !stack.is(Items.EMERALD)) {
				continue;
			}
			int take = Math.min(remaining, stack.getCount());
			stack.shrink(take);
			remaining -= take;
		}
	}
}