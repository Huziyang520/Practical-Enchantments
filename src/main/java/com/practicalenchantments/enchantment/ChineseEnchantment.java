package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 中国人附魔 - 胸甲 I 级
 */
public final class ChineseEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":chinese";

	/** EntityCounter 标记：飞行权限由本附魔授予（用于只撤销自己授予的飞行） */
	private static final ResourceLocation FLY_GRANTED =
		new ResourceLocation(PracticalEnchantments.MOD_ID, "chinese_fly_granted");

	/** 本附魔对象（注册回调时解析）。 */
	private static Enchantment HOLDER;


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		HOLDER = PracticalEnchantments.resolveEnchantment(ID);

		// 飞行解锁/撤销：使用全局 LIVING_ENTITY_TICK，拿下胸甲时（即便身上无其他附魔装备）
		// 也能可靠地撤销飞行，避免"永久飞行"。
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(ChineseEnchantment::onLivingTick);

		// 空中受击耐久 ×3
		registrar.register(HOLDER, BuiltInEvents.POST_HURT, ChineseEnchantment::onPostHurt);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;

		boolean hasChinese = hasChinese(player);
		var abilities = player.getAbilities();
		// 原版自己给的飞行：创造与旁观模式天生 mayfly。它不是本附魔授予的，收回胸甲时不许碰。
		boolean vanillaFlight = player.isCreative() || player.isSpectator();

		if (hasChinese) {
			// 解锁创造飞行（仅开放飞行权限），并且**只有在我们真的把 mayfly 从 false 翻成 true 时**才记账。
			//
			// 这是「创造模式下脱掉胸甲就掉下来、而且再也起飞不了」的根因：旧写法只要身上有本附魔就无条件
			// `EntityCounter.set(FLY_GRANTED, 1)`，而创造模式玩家的 mayfly 本来就是 true（不是我们给的）。
			// 于是脱下胸甲时走 else-if 分支，把创造模式自带的 mayfly 与 flying 一起清成 false ——
			// 玩家的飞行能力被附魔「收回」，只能重新穿上胸甲才恢复。
			// 现在只在「确实由我们打开」时记账，收回时自然无从误收回原版能力。
			if (!abilities.mayfly) {
				abilities.mayfly = true;
				player.onUpdateAbilities();
				EntityCounter.set(player, FLY_GRANTED, 1);
			}
		} else if (abilities.mayfly && EntityCounter.get(player, FLY_GRANTED) > 0) {
			// 胸甲被拿下或耐久归零：只撤销本附魔授予的飞行权限。
			// 原版创造/旁观模式以及其他模组授予的 mayfly 一律不碰。
			EntityCounter.set(player, FLY_GRANTED, 0);
			if (vanillaFlight) {
				// 兜底：上游版本留下的标记可能在创造模式下被写成 1（存档/跨版本），这里同样不清原版飞行。
				return;
			}
			abilities.mayfly = false;
			// ⚠️ 必须把 flying 一并收回，否则会卡在半空中飞下不来：
			// ① 原版 ServerPlayer 从不写 abilities.flying（字节码实证：整个类只读不写），
			//    收回 mayfly 后 flying 仍是 true；
			// ② 客户端 LocalPlayer#aiStep 里"双击取消飞行 / 收飞行状态"整段逻辑被
			//    `if (abilities.mayfly)` 包住（字节码实证：mayfly 为 false 时 ifeq 直接跳过整块），
			//    于是玩家既保持飞行、又再也没法用双击把飞行关掉。
			// NeoForge 之所以"看起来正常"，是它给 ServerPlayer#tick() 打了兜底补丁；
			// 1.20.1 是否有同款补丁未经核实，但主动清干净不会有副作用，故保留。
			abilities.flying = false;
			player.onUpdateAbilities();
		}
	}

	private static void onPostHurt(BuiltInEvents.PostHurtEvent event, EnchantmentContext ctx) {
		if (!(event.target() instanceof ServerPlayer player)) return;

		// 仅对空中受击生效
		if (player.onGround()) return;

		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty()) return;

		// 空中受击：额外造成 2 倍正常单件护甲耐久消耗，合计约 3 倍
		int extra = 2 * Math.max(1, (int) (event.amount() / 4));
		chestItem.hurtAndBreak(extra, player, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
	}

	/** 判断玩家胸甲上是否有中国人附魔 */
	private static boolean hasChinese(ServerPlayer player) {
		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty() || HOLDER == null) return false;
		return EnchantmentHelper.getItemEnchantmentLevel(HOLDER, chestItem) > 0;
	}
}