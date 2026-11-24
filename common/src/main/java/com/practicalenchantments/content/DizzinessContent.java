// 眩惑：蓝绿效果，每 tick 取消疾跑；移动输入取反在客户端完成。

package com.practicalenchantments.content;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;

public final class DizzinessContent {
	private static boolean registered;

	private DizzinessContent() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		MerlinApi.effects().register(PeEffects.DIZZINESS)
				.category(MobEffectCategory.HARMFUL)
				.color(0x2E8B57)
				.onTick((level, entity, amplifier) -> {
					if (entity instanceof Player player) {
						player.setSprinting(false);
					}
					return true;
				})
				.submit();
		MerlinApi.potions().register(PeEffects.DIZZINESS_POTION)
				.effect(PeEffects.DIZZINESS, Tunables.secondsToTicks(30))
				.submit();
		MerlinApi.potions().register(PeEffects.LONG_DIZZINESS_POTION)
				.effect(PeEffects.DIZZINESS, Tunables.secondsToTicks(60))
				.submit();
	}
}
