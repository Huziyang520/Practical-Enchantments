// 明朗附魔 只回收自己授予的那份夜视：玩家自己用 /effect give、药水、信标或其它模组 拿到的夜视（尤其是 infinite 永久夜视）一律不覆盖、不删除。

package com.practicalenchantments.enchantment.armor;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import com.practicalenchantments.content.AmuletSupport;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;

public final class BrightEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":bright";
	private static Holder<Enchantment> HOLDER;
	
	private static final ResourceLocation NV_GRANTED =
		ResourceLocation.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "bright_nv_granted");

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(BrightEnchantment::onLivingTick);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		boolean hasBright = HOLDER != null && AmuletSupport.wornLevel(player, HOLDER) > 0;

		MobEffectInstance current = player.getEffect(MobEffects.NIGHT_VISION);

		if (hasBright) {
			if (current == null) {
				player.addEffect(new MobEffectInstance(
					MobEffects.NIGHT_VISION, Tunables.secondsToTicks(Tunables.brightNightVisionSeconds()), 0, true, false));
			} else if (isOurs(current) && event.tickCount() % Tunables.secondsToTicks(Tunables.brightRefreshSeconds()) == 0) {
				player.addEffect(new MobEffectInstance(
					MobEffects.NIGHT_VISION, Tunables.secondsToTicks(Tunables.brightNightVisionSeconds()), 0, true, false));
			}
			EntityCounter.set(player, NV_GRANTED, 1);
			long timeOfDay = player.level().getGameTime() % 24000;
			boolean night = timeOfDay >= Tunables.brightNightStart() && timeOfDay <= Tunables.brightNightEnd();
			if ((night || !Tunables.brightDurabilityOnlyAtNight())
					&& event.tickCount() % Tunables.brightDurabilityIntervalTicks() == 0 && !helmet.isEmpty()) {
				helmet.hurtAndBreak(1, player, EquipmentSlot.HEAD);
			}
		} else {
			if (current != null && isOurs(current) && EntityCounter.get(player, NV_GRANTED) > 0) {
				player.removeEffect(MobEffects.NIGHT_VISION);
			}
			EntityCounter.set(player, NV_GRANTED, 0);
		}
	}

	private static boolean isOurs(MobEffectInstance instance) {
		return !instance.isInfiniteDuration()
			&& instance.getAmplifier() == 0
			&& instance.getDuration() <= Tunables.secondsToTicks(Tunables.brightNightVisionSeconds());
	}
}
