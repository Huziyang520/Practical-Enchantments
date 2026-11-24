// 慌乱：暗红效果，无法拾取掉落物；药水只有普通与延时两种。

package com.practicalenchantments.content;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.world.effect.MobEffectCategory;

public final class PanicContent {
	private static boolean registered;

	private PanicContent() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		MerlinApi.effects().register(PeEffects.PANIC)
				.category(MobEffectCategory.HARMFUL)
				.color(0x8B0000)
				.submit();
		MerlinApi.potions().register(PeEffects.PANIC_POTION)
				.effect(PeEffects.PANIC, Tunables.secondsToTicks(45))
				.submit();
		MerlinApi.potions().register(PeEffects.LONG_PANIC_POTION)
				.effect(PeEffects.PANIC, Tunables.secondsToTicks(75))
				.submit();
	}
}
