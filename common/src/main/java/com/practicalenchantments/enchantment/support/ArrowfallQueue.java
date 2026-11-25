// 落矢的延迟队列：按游戏刻排队，由统一的服务器 tick 驱动器放箭。

package com.practicalenchantments.enchantment.support;

import com.huziyang520.merlinlib.api.EntityCounter;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class ArrowfallQueue {
	public static final Identifier GENERATED =
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "arrowfall_generated");
	private static final List<Pending> PENDING = new ArrayList<>();

	private ArrowfallQueue() {
	}

	public static void enqueue(ServerLevel level, UUID owner, Vec3 origin, ItemStack ammo, ItemStack weapon,
							   int fireTicks, long fireAt) {
		if (PENDING.size() > 512) {
			PENDING.clear();
		}
		PENDING.add(new Pending(level, owner, origin, ammo.copy(), weapon.copy(), fireTicks, fireAt));
	}

	public static void tick(ServerLevel level) {
		if (PENDING.isEmpty()) {
			return;
		}
		long now = level.getGameTime();
		Iterator<Pending> iterator = PENDING.iterator();
		while (iterator.hasNext()) {
			Pending pending = iterator.next();
			if (pending.level != level) {
				continue;
			}
			if (pending.fireAt > now) {
				continue;
			}
			iterator.remove();
			spawn(pending);
		}
	}

	public static boolean isGenerated(Entity entity) {
		return EntityCounter.get(entity, GENERATED) > 0;
	}

	private static void spawn(Pending pending) {
		ServerPlayer owner = pending.owner == null ? null
				: pending.level.getServer().getPlayerList().getPlayer(pending.owner);
		Arrow arrow = new Arrow(pending.level, pending.origin.x, pending.origin.y, pending.origin.z,
				pending.ammo, pending.weapon);
		if (owner != null) {
			arrow.setOwner(owner);
		}
		// 落矢是虚拟箭雨：不进入任何玩家的拾取流程，避免把箭刷回背包。
		arrow.pickup = Arrow.Pickup.DISALLOWED;
		arrow.setBaseDamage(2.0D * Tunables.arrowfallDamageFactor());
		arrow.setRemainingFireTicks(pending.fireTicks);
		EntityCounter.set(arrow, GENERATED, 1);
		pending.level.addFreshEntity(arrow);
	}

	private record Pending(ServerLevel level, UUID owner, Vec3 origin, ItemStack ammo, ItemStack weapon,
						   int fireTicks, long fireAt) {
	}
}
