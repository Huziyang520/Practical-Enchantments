package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.ExclusiveGroupBuilder;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 羽落附魔 - 靴子单级（娱乐，仅创造/指令获取）。
 *
 * <p>穿着时持续获得无限缓降，免疫摔落伤害；潜行（Shift）时缓降失效以恢复正常下落速度。
 * 与原版摔落缓冲（羽毛掉落）互斥。只移除本附魔施加的无限缓降，不影响药水缓降。</p>
 */
public final class GentleDescentEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":gentle_descent";

	public static final String EXCLUSIVE_SET = ExclusiveGroupBuilder
		.create(PracticalEnchantments.MOD_ID, "slow_fall")
		.getTagReference();

	private static Holder<Enchantment> HOLDER;

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

		if (hasGentle && !entity.isShiftKeyDown()) {
			// 无限缓降：只在缺失时施加，避免反复刷新
			if (!entity.hasEffect(MobEffects.SLOW_FALLING)) {
				entity.addEffect(new MobEffectInstance(
					MobEffects.SLOW_FALLING, MobEffectInstance.INFINITE_DURATION, 0, true, false));
			}
		} else {
			// 潜行 / 脱靴：仅移除本附魔的无限缓降，药水缓降（有限时长）保留
			MobEffectInstance current = entity.getEffect(MobEffects.SLOW_FALLING);
			if (current != null && current.isInfiniteDuration()) {
				entity.removeEffect(MobEffects.SLOW_FALLING);
			}
		}
	}
}
