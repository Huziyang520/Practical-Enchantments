package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * 焚灭附魔 - 剑、斧、矛 I 级
 */
public final class IncinerateEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":incinerate";

	/** 互斥组名（对应数据包标签 {@code practical_enchantments:exclusive_set/looting}） */
	public static final String GROUP_NAME = "looting";

	/** 互斥组标签引用（见 {@code data/practical_enchantments/tags/enchantment/exclusive_set/looting.json}）：与原版抢夺互斥 */


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.POST_KILL, IncinerateEnchantment::onPostKill);
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

		// 分裂发生在 remove 里，晚于这里，故留标记让 Mixin 在那一步跳过
		KillMarks.markIncinerated(victim);

		// 经验值 ×2（原版掉落 1 倍经验球 + 这里再补 1 倍，合计 2 倍）
		int baseXp = victim.getExperienceReward();
		if (baseXp > 0) {
			player.giveExperiencePoints(baseXp);
		}
	}
}