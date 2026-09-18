package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.ExclusiveGroupBuilder;
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
 */
public final class DemonicPactEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":demonic_pact";

	/** 互斥组：恶魔交易 ↔ 掉落不死亡 */
	public static final String EXCLUSIVE_SET = ExclusiveGroupBuilder
		.create(PracticalEnchantments.MOD_ID, "death_save")
		.getTagReference();

	/** 冷却（tick） */
	private static final long COOLDOWN_TICKS = 1200L;

	/** 玩家 UUID → 上次触发的 gameTime */
	private static final ConcurrentHashMap<UUID, Long> LAST_TRIGGER = new ConcurrentHashMap<>();

	private DemonicPactEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("恶魔交易")
			.supportedItems("#minecraft:enchantable/chest_armor")
			.weight(1)
			.maxLevel(3)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(6)
			.exclusiveSet(EXCLUSIVE_SET)
			.slots("chest"));
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
		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			if (!stack.isEmpty() && stack.is(Items.EMERALD)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	private static void consumeEmeralds(ServerPlayer player, int amount) {
		int remaining = amount;
		var items = player.getInventory().getNonEquipmentItems();
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
