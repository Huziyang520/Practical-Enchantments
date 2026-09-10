package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 处刑人附魔(executioner)。
 *
 * <p>剑/斧 III 级,高血减伤/低血增伤(阈值与幅度随等级变化)。</p>
 */
public final class ExecutionerEnchantment {

	public static final String EXECUTIONER_ID = MOD_ID + ":executioner";

	private ExecutionerEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(EXECUTIONER_ID)
			.description("Executioner")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(5).maxLevel(3)
			.minCost(8, 8).maxCost(30, 8).anvilCost(4)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> executioner = resolveEnchantment(registries, EXECUTIONER_ID);
		registrar.register(executioner, BuiltInEvents.MODIFY_DAMAGE,
			ExecutionerEnchantment::onExecutionerDamage);
	}

	/**
	 * 处刑人回调:根据目标当前 HP% 增减伤害,阈值与幅度随等级变化。
	 *
	 * <p>高血减伤:lvl1 HP&gt;70% -30%, lvl2 HP&gt;80% -20%, lvl3 HP&gt;90% -10%;
	 * 低血增伤:lvl1 HP&lt;50% +20%, lvl2 HP&lt;60% +30%, lvl3 HP&lt;70% +50%;
	 * 中间区间不修改伤害。</p>
	 */
	private static void onExecutionerDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		float maxHp = target.getMaxHealth();
		if (maxHp <= 0) {
			return;
		}
		int level = ctx.level();
		float hpRatio = target.getHealth() / maxHp;
		float currentDamage = event.damage().getValue();
		float highThreshold = 0.6F + 0.1F * level;
		float lowThreshold = 0.4F + 0.1F * level;
		if (hpRatio > highThreshold) {
			float penalty = 0.4F - 0.1F * level;
			event.damage().setValue(currentDamage * (1.0F - penalty));
		} else if (hpRatio < lowThreshold) {
			float bonus = switch (level) {
				case 1 -> 0.20F;
				case 2 -> 0.30F;
				case 3 -> 0.50F;
				default -> 0.20F + 0.15F * (level - 1);
			};
			event.damage().setValue(currentDamage * (1.0F + bonus));
		}
	}
}
