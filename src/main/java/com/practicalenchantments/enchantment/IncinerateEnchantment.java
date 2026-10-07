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
 * 效果：击杀生物无战利品掉落，经验值 ×2
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
 * <li><b>⚠️ {@code victim.getExperienceReward(event.level(), player)} →
 * {@code victim.getExperienceReward()}。</b>26.3 的 {@code LivingEntity#getExperienceReward}
 * 收两个参数（{@code ServerLevel} 与"击杀者"），那是新版本为了按击杀者调整经验而加的；
 * <b>1.20.1 只有无参形式</b>（已用 javap 确认：{@code LivingEntity} 无参版本返回 0，
 * {@code Mob} 覆写它并按 {@code xpReward} + 护甲/手持物随机加成计算）。移植时把两个实参去掉即可，
 * 本附魔本来也不用那两个参数。
 *
 * <p><b>一处需要知情的语义差别</b>：{@code Mob.getExperienceReward()} 每次调用都会重新掷一次
 * "每件护甲/手持物 +0~2 点"的随机数（javap 字节码里能看到 {@code RandomSource.nextInt(3)}）。
 * 原版掉落经验时会调用它一次，本附魔又调用一次，所以两次掷点<b>互相独立</b>，总经验不是严格的
 * "同一数值 ×2"，而是"原版那一份 + 独立掷出的另一份"。26.3 同样如此（它的两参版本也掷随机数），
 * 故这不是移植引入的差异。若将来想严格 ×2，需要缓存原版那一份而不是重算——本移植<b>未</b>做此改动，
 * 以保持与 26.3 一致。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code event.level().getEntitiesOfClass(Class, AABB)} 在 1.20.1 是
 * {@code EntityGetter} 上的 default 方法（{@code ServerLevel} 未声明，但可用）、
 * {@code victim.getBoundingBox().inflate(0.5)}、{@code item.discard()}、
 * {@code player.giveExperiencePoints(int)}、{@code victim instanceof ServerPlayer} 全部同形。</li>
 *
 * <li><b>互斥组字段 {@code GROUP_NAME} 原样保留</b>（{@code "looting"}），它被
 * {@code ExclusiveSets} / 数据包标签引用，1.20.1 侧同样按字符串用。</li>
 * </ol>
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

		// 经验值 ×2（原版掉落 1 倍经验球 + 这里再补 1 倍，合计 2 倍）
		int baseXp = victim.getExperienceReward();
		if (baseXp > 0) {
			player.giveExperiencePoints(baseXp);
		}
	}
}