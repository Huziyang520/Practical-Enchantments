package com.practicalenchantments.enchantment;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * 掉落不死亡附魔 - 任意装备 Ⅲ 级（娱乐，仅创造/指令获取）。
 *
 * <p>受到致命伤害时免死一次：摧毁携带该附魔的那件装备，背包与全身装备全部
 * 掉落脚边，并获得黑暗效果（20/16/12 秒，Ⅰ/Ⅱ/Ⅲ）。与「恶魔交易」互斥。</p>
 *
 * <p>由 {@code DeathSaveMixin} 在 {@code LivingEntity.die} HEAD 处调用。</p>
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 *
 * <p>本类<b>没有</b> {@code registerCallbacks}——26.3 也没有，入口是 Mixin，不是事件回调。</p>
 *
 * <ol>
 * <li><b>本类没有被迫改动的地方——是逐字直接移植。</b>import 表（
 * {@code PracticalEnchantments}、{@code ServerPlayer}、{@code DamageTypeTags}、
 * {@code DamageSource}、{@code EquipmentSlot}）与 26.3 逐行一致，方法体也一字未改。</li>
 *
 * <li><b>{@code DamageTypeTags.BYPASSES_INVULNERABILITY} 在 1.20.1 存在</b>（javap 实证），
 * 虚空 / {@code /kill} 不救的逻辑一字未改。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code DeathSaveSupport.findArmorWith(player, "undying_drop")}
 * （返回 {@code EquipmentSlot}，无则 null）、
 * {@code DeathSaveSupport.getLevel(player.getItemBySlot(slot), "undying_drop")}、
 * {@code DeathSaveSupport.dropEverything(player, enchantedSlot)}、
 * {@code DeathSaveSupport.performResurrection(player, darknessTicks)} 全部是已经移植好的
 * 1.20.1 版本，调用点与 26.3 一一对应，本类<b>不重复实现</b>其中任何逻辑。</li>
 *
 * <li><b>顺序原样保留，并且是有意义的</b>：先 {@code findArmorWith} 拿槽位 → 挡无敌伤害 →
 * <b>先读等级</b>（因为 {@code dropEverything} 会毁掉那件装备，读晚了就拿不到等级）→
 * 掉落全身 → 复活。移植未调整任何一步。</li>
 *
 * <li><b>签名必须保持逐字不变</b>：{@code public static boolean trySave(ServerPlayer, DamageSource)}，
 * {@code DeathSaveMixin} 就是按这个签名调的。</li>
 * </ol>
 */
public final class UndyingDropEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":undying_drop";

	private UndyingDropEnchantment() {
	}


	/**
	 * 尝试触发掉落不死亡。
	 *
	 * @return true 表示已免死（Mixin 应取消死亡）
	 */
	public static boolean trySave(ServerPlayer player, DamageSource source) {
		EquipmentSlot enchantedSlot = DeathSaveSupport.findArmorWith(player, "undying_drop");
		if (enchantedSlot == null) {
			return false;
		}
		// 虚空 / /kill 等绕过无敌的伤害不救
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}

		// 先读等级（摧毁前），决定黑暗时长
		int level = DeathSaveSupport.getLevel(player.getItemBySlot(enchantedSlot), "undying_drop");
		int darknessTicks = (20 - 4 * (level - 1)) * 20;

		// 摧毁附魔装备 + 掉落全身物品
		DeathSaveSupport.dropEverything(player, enchantedSlot);

		// 免死 + 黑暗
		DeathSaveSupport.performResurrection(player, darknessTicks);
		return true;
	}
}