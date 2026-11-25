// 统一服务器 tick 驱动：落矢队列、收猎、进度扫描、迁移都挂在这里，避免每个功能各开一条全局监听。

package com.practicalenchantments;

import com.practicalenchantments.advancement.PeAdvancementTriggers;
import com.practicalenchantments.enchantment.support.ArrowfallQueue;
import com.practicalenchantments.migration.PeLegacyMigration;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.event.GlobalEvents;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class PeServerRuntime {
	private static final Map<ResourceKey<Level>, Long> LAST_TICK = new HashMap<>();
	private static boolean installed;

	private PeServerRuntime() {
	}

	public static void install() {
		if (installed) {
			return;
		}
		installed = true;
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(PeServerRuntime::onLivingTick);
	}

	private static void onLivingTick(com.huziyang520.merlinlib.event.LivingEntityTickEvent event) {
		if (!(event.level() instanceof ServerLevel level)) {
			return;
		}
		long now = level.getGameTime();
		Long last = LAST_TICK.put(level.dimension(), now);
		if (last != null && last == now) {
			return;
		}
		ArrowfallQueue.tick(level);
		MerlinApi.drops().tickDeathDropMoves(level);
		if (now % 100L == 0L) {
			PeAdvancementTriggers.scan(level);
		}
		if (now % 600L == 0L) {
			PeLegacyMigration.tickPeriodic(level);
		}
	}
}
