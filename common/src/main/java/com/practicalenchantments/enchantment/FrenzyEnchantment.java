package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 狂暴附魔 - 剑/斧/矛 Ⅱ 级（宝藏，大师级图书管理员）。
 *
 * <p>击杀生物后获得速度：Ⅰ 级速度 II 6 秒，Ⅱ 级速度 III 8 秒；
 * 附在斧上时持续时间翻倍（Ⅰ 12 秒 / Ⅱ 16 秒）。每次击杀重新计时。</p>
 */
public final class FrenzyEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":frenzy";

	private FrenzyEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("狂暴")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(1)
			.maxLevel(2)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(3)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_KILL, FrenzyEnchantment::onKill);
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) {
			return;
		}
		// Ⅰ:6s/II，Ⅱ:8s/III；斧翻倍
		int baseSeconds = ctx.level() == 1 ? 6 : 8;
		boolean axe = player.getMainHandItem().is(ItemTags.AXES);
		int duration = baseSeconds * (axe ? 2 : 1) * 20;
		int amplifier = ctx.level(); // Ⅰ→amp1（速度 II），Ⅱ→amp2（速度 III）
		player.addEffect(new MobEffectInstance(MobEffects.SPEED, duration, amplifier, true, true));
	}
}
