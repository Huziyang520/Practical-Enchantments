package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 中国人附魔 - 胸甲 I 级
 * 效果：装备后解锁创造飞行；空中受击耐久消耗 ×3；拿下胸甲立即恢复正常
 */
public final class ChineseEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":chinese";

	/** EntityCounter 标记：飞行权限由本附魔授予（用于只撤销自己授予的飞行） */
	private static final Identifier FLY_GRANTED =
		Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "chinese_fly_granted");

	/** 本附魔的 Holder（注册回调时解析） */
	private static Holder<Enchantment> HOLDER;


	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);

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

		if (hasChinese) {
			// 解锁创造飞行（仅开放飞行权限），并标记"飞行由本附魔授予"
			if (!abilities.mayfly) {
				abilities.mayfly = true;
				player.onUpdateAbilities();
			}
			EntityCounter.set(player, FLY_GRANTED, 1);
		} else if (abilities.mayfly && EntityCounter.get(player, FLY_GRANTED) > 0) {
			// 胸甲被拿下或耐久归零：只撤销本附魔授予的飞行权限。
			// 原版创造/旁观模式以及其他模组授予的 mayfly 一律不碰。
			EntityCounter.set(player, FLY_GRANTED, 0);
			abilities.mayfly = false;
			// ⚠️ 必须把 flying 一并收回，否则 Fabric 端会卡在半空中飞下不来：
			// ① 原版 ServerPlayer 从不写 abilities.flying（javap 实证：整个类只读不写），
			//    收回 mayfly 后 flying 仍是 true；
			// ② 客户端 LocalPlayer#aiStep 里"双击取消飞行 / 收飞行状态"整段逻辑被
			//    `if (abilities.mayfly)` 包住（javap 实证：mayfly 为 false 时 ifeq 直接跳过整块），
			//    于是玩家既保持飞行、又再也没法用双击把飞行关掉。
			// NeoForge 之所以"看起来正常"，是它给 ServerPlayer#tick() 打了兜底补丁
			// （neoforge-<ver>-userdev.jar → patches/net/minecraft/server/level/ServerPlayer.java.patch）：
			//     if (this.getAbilities().flying && !this.mayFly()) {
			//         this.getAbilities().flying = false; this.onUpdateAbilities(); }
			// 原版 / Fabric 没有这道兜底，所以由模组在撤销时自己清干净。
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
		chestItem.hurtAndBreak(extra, player, EquipmentSlot.CHEST);
	}

	/** 判断玩家胸甲上是否有中国人附魔 */
	private static boolean hasChinese(ServerPlayer player) {
		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty() || HOLDER == null) return false;
		return chestItem.getEnchantments().getLevel(HOLDER) > 0;
	}
}
