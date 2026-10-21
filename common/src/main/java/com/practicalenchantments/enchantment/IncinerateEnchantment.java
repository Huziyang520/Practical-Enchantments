package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
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

	/** 互斥组名（对应数据包标签 {@code practical_enchantments:exclusive_set/looting}） */
	public static final String GROUP_NAME = "looting";

	/** 互斥组标签引用（见 {@code data/practical_enchantments/tags/enchantment/exclusive_set/looting.json}）：与原版抢夺互斥 */


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
