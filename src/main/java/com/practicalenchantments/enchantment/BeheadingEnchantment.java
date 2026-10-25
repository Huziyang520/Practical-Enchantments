// 夺首附魔 - 剑/斧/矛 Ⅲ 级（不可附魔台，大师级图书管理员交易）。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public final class BeheadingEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":beheading";
	private static final float BONUS_PER_LEVEL = 0.025F;
	private static final float WITHER_SKELETON_BASE = 0.025F;

	private BeheadingEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.POST_KILL, BeheadingEnchantment::onKill);
	}

	private static void onKill(BuiltInEvents.PostKillEvent event, EnchantmentContext ctx) {
		if (!(event.killer() instanceof ServerPlayer)) {
			return;
		}
		LivingEntity victim = event.victim();
		Item head = headFor(victim);
		if (head == null) {
			return;
		}
		float base = victim instanceof WitherSkeleton ? WITHER_SKELETON_BASE : 0.0F;
		float chance = base + BONUS_PER_LEVEL * ctx.level();
		if (event.killer().getRandom().nextFloat() < chance) {
			Block.popResource(event.level(), victim.blockPosition(), new ItemStack(head));
		}
	}

	private static Item headFor(LivingEntity victim) {
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
}
