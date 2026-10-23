package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * 焚灭附魔 - 剑、斧、矛 I 级
 */
public final class IncinerateEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":incinerate";

	/** 互斥组名（对应数据包标签 {@code practical_enchantments:exclusive_set/looting}） */
	public static final String GROUP_NAME = "looting";

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.POST_KILL, IncinerateEnchantment::onPostKill);
	}

	private static void onPostKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer player)) return;

		LivingEntity victim = event.victim();

		// 对玩家无效
		if (victim instanceof ServerPlayer) return;

		// 死亡掉落（战利品表、装备、生物特有的额外掉落）由 KillMarks 的标记在 Entity 侧拦下，
		// 经验球不走那条路径，因此经验照样保留。分裂发生在 remove 里，晚于这里，同样靠标记跳过。
		KillMarks.markIncinerated(victim);

		// 经验值 ×2（原版掉落 1 倍经验球 + 这里再补 1 倍，合计 2 倍）
		int baseXp = victim.getExperienceReward();
		if (baseXp > 0) {
			player.giveExperiencePoints(baseXp);
		}
	}
}