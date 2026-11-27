// 飞行附魔 - 胸甲 I 级 效果：装备后解锁创造飞行；拿下后立即恢复正常。

package com.practicalenchantments.enchantment.armor;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.advancement.PeAdvancementTriggers;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.content.AmuletSupport;
import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class FlightEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":flight";

	private static final Identifier FLY_GRANTED =
		Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "flight_fly_granted");

	private static Holder<Enchantment> HOLDER;

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(FlightEnchantment::onLivingTick);
		registrar.register(HOLDER, BuiltInEvents.POST_HURT, FlightEnchantment::onPostHurt);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;
		var abilities = player.getAbilities();
		boolean vanillaFlight = player.isCreative() || player.isSpectator();

		if (hasFlight(player)) {
			if (!abilities.mayfly) {
				abilities.mayfly = true;
				player.onUpdateAbilities();
			}
			// 每次 tick 都补记账：重登与换维度后玩家实体是新的，旧标记已丢失。
			if (!vanillaFlight) {
				EntityCounter.set(player, FLY_GRANTED, 1);
			}
			if (abilities.flying && !vanillaFlight) {
				PeAdvancementTriggers.survivalFlight(player);
			}
		} else if (abilities.mayfly && EntityCounter.get(player, FLY_GRANTED) > 0) {
			EntityCounter.set(player, FLY_GRANTED, 0);
			if (vanillaFlight) {
				return;
			}
			abilities.mayfly = false;
			abilities.flying = false;
			player.onUpdateAbilities();
		}
	}

	private static void onPostHurt(BuiltInEvents.PostHurtEvent event, EnchantmentContext ctx) {
		if (!(event.target() instanceof ServerPlayer player)) return;
		if (player.onGround()) return;
		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty()) return;
		int extra = Tunables.flightAirborneDurability(event.amount());
		chestItem.hurtAndBreak(extra, player, EquipmentSlot.CHEST);
	}

	private static boolean hasFlight(ServerPlayer player) {
		// 同时按捕获的 Holder 与 id 查等级：数据包重载后 Holder 实例可能已经不是物品里存的那一个。
		if (HOLDER != null && AmuletSupport.wornLevel(player, HOLDER) > 0) {
			return true;
		}
		return AmuletSupport.wornLevel(player, ID) > 0;
	}
}
