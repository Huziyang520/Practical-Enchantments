package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

/**
 * 强劲附魔 - 弩 V 级
 * 效果：弩专属远程伤害增幅，每级 +25% 箭矢伤害
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
 * <li><b>没有其它改动。</b>{@code DamageTypes.ARROW}、{@code DamageSource.is(ResourceKey)}、
 * {@code event.attacker().getItemBySlot(EquipmentSlot.MAINHAND)}、
 * {@code net.minecraft.world.item.CrossbowItem}（1.20.1 的包路径与 26.3 相同，已用 javap 确认
 * 类存在）、{@code MutableFloat} 读写全部同形。本类<b>除签名外是逐字直接移植</b>。</li>
 *
 * <li><b>两处语义细节，均与 26.3 一致，移植时特意没有"顺手修好"：</b>
 * <ul>
 *   <li>{@code event.attacker()} 没有做 null 检查。{@code ModifyDamageEvent} 的 javadoc 明说
 *       {@code attacker} 在伤害源没有实体时可以是 {@code null}，这里直接
 *       {@code getItemBySlot} 会 NPE。26.3 如此，移植保持原样；实际触发路径上
 *       {@code DamageTypes.ARROW} 的伤害源必有实体，所以暂时打不到这条路径。</li>
 *   <li>判断的是"主手拿着弩"，而不是"这支箭由弩射出"。弓射出的箭只要主手拿着弩也会被加成。
 *       同样与 26.3 一致。要修需要事件里带上发射武器（1.20.1 的
 *       {@code ProjectileHitEvent} 记录里已有 {@code weapon()} 快照），但那会改变行为，
 *       超出"移植"的范围。</li>
 * </ul></li>
 *
 * <li><b>互斥组字段 {@code GROUP_NAME} 原样保留</b>（{@code "crossbow"}），1.20.1 侧同样按字符串用。</li>
 * </ol>
 */
public final class PowerfulEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":powerful";

	/** 互斥组名（对应数据包标签 {@code practical_enchantments:exclusive_set/crossbow}） */
	public static final String GROUP_NAME = "crossbow";

	/**
	 * 互斥组标签引用（见 {@code data/practical_enchantments/tags/enchantment/exclusive_set/crossbow.json}）。
	 *
	 * <p>为什么不直接用原版的 crossbow 互斥标签：原版
	 * {@code #minecraft:exclusive_set/crossbow} 同时含多重射击与穿透，而本附魔按设计要求
	 * <b>可与穿透叠加</b>，只与多重射击互斥，故自建只含多重射击的互斥组。</p>
	 */


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		// 使用 MODIFY_DAMAGE 事件处理远程伤害加成
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_DAMAGE, PowerfulEnchantment::onModifyDamage);
	}

	private static void onModifyDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		// 只处理弹射物伤害（箭矢）
		DamageSource source = event.source();
		if (!source.is(DamageTypes.ARROW)) return;

		// 检查攻击者主手是否持有弩
		ItemStack mainHand = event.attacker().getItemBySlot(EquipmentSlot.MAINHAND);
		if (!(mainHand.getItem() instanceof CrossbowItem)) return;

		// 每级 +25% 伤害
		int level = ctx.level();
		float multiplier = 1.0f + (level * 0.25f);
		float currentDamage = event.damage().getValue();
		event.damage().setValue(currentDamage * multiplier);
	}
}