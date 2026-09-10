package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentEffectsBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 焚灭附魔 - 剑、斧、矛 I 级
 * 效果：击杀生物无战利品掉落，经验值 ×2
 */
public final class IncinerateEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":incinerate";

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("焚灭")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(1) // 宝藏附魔（weight 最小必须为 1）
			.maxLevel(1)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(0)
			.slots("mainhand")
			.effects(EnchantmentEffectsBuilder.create().build()));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_KILL, IncinerateEnchantment::onPostKill);
	}

	private static void onPostKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) return;

		LivingEntity victim = event.victim();

		// 对玩家无效
		if (victim instanceof ServerPlayer) return;

		// 无战利品掉落：移除死者周围已生成的物品实体（保留经验球）
		// 掉落物会在死亡瞬间以 ItemEntity 形式生成在尸体附近，这里将其全部清空
		for (ItemEntity item : event.level().getEntitiesOfClass(
			ItemEntity.class, victim.getBoundingBox().inflate(0.5))) {
			item.discard();
		}

		// 经验值 ×2（原版掉落 1 倍经验球 + 这里再补 1 倍，合计 2 倍）
		int baseXp = victim.getExperienceReward(event.level(), player);
		if (baseXp > 0) {
			player.giveExperiencePoints(baseXp);
		}
	}
}
