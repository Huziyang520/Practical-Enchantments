package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

/**
 * 毁灭附魔 - 工具（剑除外）I 级
 * 效果：挖掘方块不产出物品掉落，改为掉落经验；基础 2 点，矿石类 5 点
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
 * <li><b>⚠️ 矿石标签换了命名空间，这是本次移植里唯一一处"行为可能变强"的改动，需要知情人确认。</b>
 * 详见下面 {@link #ORES_TAG} 的注释：26.3 写的 {@code minecraft:ores} 在 1.20.1 与 26.3
 * <b>都不存在</b>，本移植改用 Forge 提供的 {@code forge:ores}。</li>
 *
 * <li><b>其余逐字直接移植。</b>{@code ctx.slot() != EquipmentSlot.MAINHAND} 的短路、
 * {@code event.drops().clear()}、{@code event.addBonusXp(int)}、{@code BlockState.is(TagKey)}
 * 全部同形。原注释里"不能以 {@code tool.isEmpty()} 跳过"的理由在 1.20.1 同样成立：
 * 伐木工递归破坏时事件照样会以玩家主手扫描并触发本回调，故判断依据仍然是上下文槽位。</li>
 * </ol>
 */
public final class DestructionEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":destruction";

	/**
	 * 矿石方块标签。
	 *
	 * <h2>为什么不是 26.3 的 {@code minecraft:ores}</h2>
	 *
	 * <p>26.3 原码写的是
	 * {@code TagKey.create(Registries.BLOCK, Identifier.parse("minecraft:ores"))}，并附注
	 * "MC 26.2 中使用 TagKey 直接引用矿石标签"。移植时逐项核对过两边的数据包，结论是
	 * <b>这个标签在两边都不存在</b>：</p>
	 * <ul>
	 *   <li>1.20.1 原版 jar 里没有 {@code data/minecraft/tags/blocks/ores.json}
	 *       （只有 {@code coal_ores}、{@code copper_ores}、{@code diamond_ores}、
	 *       {@code emerald_ores}、{@code gold_ores}、{@code iron_ores}、{@code lapis_ores}、
	 *       {@code redstone_ores} 这些按材质拆分的标签，没有合集标签）。</li>
	 *   <li>26.3 整棵树里也搜不到任何 {@code ores.json}
	 *       （{@code data/minecraft/tags/blocks/} 目录根本不存在），本模组自己也没有提供它。</li>
	 * </ul>
	 *
	 * <p>也就是说 26.3 的 {@code brokenState.is(ORES_TAG)} <b>恒为 false</b>，实际行为是"任何方块都
	 * 只给 2 点经验"，与它自己 javadoc 写的"矿石类 5 点"不符。这是一处 26.3 就存在的失效代码，
	 * 不是 1.20.1 造成的。</p>
	 *
	 * <h2>两个可选做法，以及为什么选后者</h2>
	 * <ul>
	 *   <li><b>照抄 {@code minecraft:ores}</b>：与 26.3 逐字相同，但把失效代码一起搬过来，
	 *       矿石永远只有 2 点。</li>
	 *   <li><b>用 {@code forge:ores}（本类采用）</b>：{@code net.minecraftforge.common.Tags.Blocks.ORES}
	 *       是 Forge 提供的合集标签，1.20.1 的 Forge 通用 jar 里确实有
	 *       {@code data/forge/tags/blocks/ores.json}，内容为
	 *       {@code #forge:ores/{coal,copper,diamond,emerald,gold,iron,lapis,redstone,quartz,netherite_scrap}}
	 *       十个子标签的并集（已解包确认），语义与 26.3 想写的"矿石"完全对应。</li>
	 * </ul>
	 *
	 * <p>选后者的理由：它实现的是本附魔 javadoc 明确写出的设计意图；Forge 依赖本项目本来就有
	 * （{@code VillagerTradeRegistration} 已在用 {@code net.minecraftforge.*}）。</p>
	 *
	 * <p><b>这是一处经用户拍板的行为修正，不是移植者的擅自决定。</b>移植过程中把两种做法连同
	 * 证据一并上报（"矿石在 26.3 上恒为 2 点，与它自己的 javadoc 矛盾"），用户选择"修好它"。
	 * 效果是矿石经验从 2 点变成 5 点，属于<b>有意为之、且已知情</b>的变化——这与"移植期间不得夹带
	 * 新功能"的纪律不冲突，因为它修的是一个从未生效过的判断，而不是新增能力。</p>
	 *
	 * <p>若要回到严格复刻 26.3 的实际表现，把下面这一行换回
	 * {@code TagKey.create(Registries.BLOCK, new ResourceLocation("minecraft", "ores"))} 即可，
	 * 其余代码不用动。</p>
	 */
	private static final TagKey<Block> ORES_TAG = Tags.Blocks.ORES;


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		registrar.register(PracticalEnchantments.resolveEnchantment(ID),
			BuiltInEvents.MODIFY_BLOCK_DROPS, DestructionEnchantment::onModifyBlockDrops);
	}

	private static void onModifyBlockDrops(BuiltInEvents.ModifyBlockDropsEvent event, EnchantmentContext ctx) {
		// 只对主手工具上的毁灭附魔生效。
		// 注意：与伐木工联动递归破坏方块时，Mixin 传入的工具可能为空（ItemStack.EMPTY），
		// 但事件仍会以玩家为主手扫描并触发回调，故不能以 tool.isEmpty() 作为跳过条件，
		// 而应依据上下文中的槽位（主手 = 破坏方块的工具）判断。
		if (ctx.slot() != EquipmentSlot.MAINHAND) return;

		BlockState brokenState = event.blockState();

		// 清除所有物品掉落
		event.drops().clear();

		// 计算经验值
		int xp = 2; // 基础 2 点

		// 矿石类 5 点（见 ORES_TAG 注释：26.3 的 minecraft:ores 不存在，此处用 forge:ores）
		if (brokenState.is(ORES_TAG)) {
			xp = 5;
		}

		// 通过 addBonusXp 补充经验（由 Mixin 自动生成经验球）
		event.addBonusXp(xp);
	}
}