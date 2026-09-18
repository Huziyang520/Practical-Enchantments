package com.practicalenchantments.enchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

/**
 * 夺首附魔 - 剑/斧/矛 Ⅲ 级（宝藏，不可附魔台，大师级图书管理员交易）。
 *
 * <p>击杀有头颅的生物时，在原版掉落概率上额外增加 2.5%/级 的掉头率：
 * 僵尸、骷髅、苦力怕、凋灵骷髅、猪灵。凋灵骷髅原版基础概率 2.5%。</p>
 */
public final class BeheadingEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":beheading";

	/** 每级额外掉头概率 */
	private static final float BONUS_PER_LEVEL = 0.025F;
	/** 凋灵骷髅原版掉头基础概率 */
	private static final float WITHER_SKELETON_BASE = 0.025F;

	private BeheadingEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ID)
			.description("夺首")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(1)
			.maxLevel(3)
			.minCost(0, 0)
			.maxCost(0, 0)
			.anvilCost(4)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_KILL, BeheadingEnchantment::onKill);
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

	/** 按生物类型返回对应头颅物品，无对应头颅返回 null。WitherSkeleton 必须先于 Skeleton 判断。 */
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
