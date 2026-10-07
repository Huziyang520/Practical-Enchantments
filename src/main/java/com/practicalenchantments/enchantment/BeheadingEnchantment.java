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

/**
 * 夺首附魔 - 剑/斧/矛 Ⅲ 级（不可附魔台，大师级图书管理员交易）。
 *
 * <p>击杀有头颅的生物时，在原版掉落概率上额外增加 2.5%/级 的掉头率：
 * 僵尸、骷髅、苦力怕、凋灵骷髅、猪灵。凋灵骷髅原版基础概率 2.5%。</p>
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 * <ol>
 * <li><b>{@code registerCallbacks} 去掉 {@code HolderLookup.Provider} 参数</b>，改用
 * {@code PracticalEnchantments.resolveEnchantment(ID)}（原因见 {@code AerialHasteEnchantment}
 * 类注释第 1 条）。</li>
 *
 * <li><b>{@code Holder<Enchantment>} → {@code Enchantment}</b>：见 {@code VenomEnchantment}
 * 类注释第 2 条。</li>
 *
 * <li><b>生物类换了包。</b>26.3 的骷髅/僵尸住在细分包里：
 * {@code net.minecraft.world.entity.monster.skeleton.Skeleton}、
 * {@code ...monster.skeleton.WitherSkeleton}、{@code ...monster.zombie.Zombie}。
 * 1.20.1 这三者都在扁平的 {@code net.minecraft.world.entity.monster} 包里（已用 javap 逐个确认
 * 类存在）。{@code Creeper} 与 {@code ...monster.piglin.Piglin} 两级版本包名相同，未动。
 * <b>这条差异只影响 import，不影响任何判断顺序或行为。</b></li>
 *
 * <li><b>判断顺序原样保留：{@code WitherSkeleton} 必须排在 {@code Skeleton} 之前。</b>
 * 1.20.1 里 {@code WitherSkeleton extends Skeleton}（26.3 同样如此），顺序颠倒会让凋灵骷髅
 * 掉成普通骷髅头，并且丢掉 2.5% 的基础概率。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code event.killer() instanceof ServerPlayer}、
 * {@code victim.blockPosition()}、{@code Block.popResource(Level, BlockPos, ItemStack)}、
 * {@code getRandom().nextFloat()}、{@code Items.WITHER_SKELETON_SKULL} 等全部同形。
 * 特别是 <b>{@code Block.popResource} 而不是 {@code victim.spawnAtLocation}</b>：
 * 前者按方块中心偏移生成掉落物，后者会挂在生物身上；这里照 26.3 走前者。</li>
 * </ol>
 */
public final class BeheadingEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":beheading";

	/** 每级额外掉头概率 */
	private static final float BONUS_PER_LEVEL = 0.025F;
	/** 凋灵骷髅原版掉头基础概率 */
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