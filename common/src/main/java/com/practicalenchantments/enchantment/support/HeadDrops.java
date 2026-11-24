// 夺首与裁决共用头颅映射，保证同一次死亡只掉一个头。

package com.practicalenchantments.enchantment.support;

import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class HeadDrops {
	private HeadDrops() {
	}

	public static Item headFor(LivingEntity victim) {
		if (victim instanceof WitherSkeleton) {
			return Items.WITHER_SKELETON_SKULL;
		}
		if (victim instanceof Skeleton) {
			return Items.SKELETON_SKULL;
		}
		if (victim instanceof Zombie) {
			return Items.ZOMBIE_HEAD;
		}
		if (victim instanceof Creeper) {
			return Items.CREEPER_HEAD;
		}
		if (victim instanceof Piglin) {
			return Items.PIGLIN_HEAD;
		}
		return null;
	}

	public static boolean drop(ServerLevel level, LivingEntity victim, ServerPlayer killer) {
		Item head = headFor(victim);
		if (head == null) {
			return false;
		}
		MerlinApi.drops().dropAtDeath(level, victim.position(), new ItemStack(head), victim);
		return true;
	}
}
