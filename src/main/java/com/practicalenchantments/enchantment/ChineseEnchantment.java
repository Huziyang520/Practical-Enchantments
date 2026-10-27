// 中国人附魔 - 胸甲 I 级 效果：装备后解锁创造飞行；空中受击耐久消耗 ×3；拿下胸甲立即恢复正常

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ChineseEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":chinese";

	private static final ResourceLocation FLY_GRANTED =
		ResourceLocation.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "chinese_fly_granted");

	private static Holder<Enchantment> HOLDER;

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(ChineseEnchantment::onLivingTick);
		registrar.register(HOLDER, BuiltInEvents.POST_HURT, ChineseEnchantment::onPostHurt);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;
		boolean hasChinese = hasChinese(player);
		var abilities = player.getAbilities();
		boolean vanillaFlight = player.isCreative() || player.isSpectator();

		if (hasChinese) {
			if (!abilities.mayfly) {
				abilities.mayfly = true;
				player.onUpdateAbilities();
				EntityCounter.set(player, FLY_GRANTED, 1);
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
		int extra = 2 * Math.max(1, (int) (event.amount() / 4));
		chestItem.hurtAndBreak(extra, player, EquipmentSlot.CHEST);
	}

	private static boolean hasChinese(ServerPlayer player) {
		return HOLDER != null && AmuletSupport.wornLevel(player, HOLDER) > 0;
	}
}
