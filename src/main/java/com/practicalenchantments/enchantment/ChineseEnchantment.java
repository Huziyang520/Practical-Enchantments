package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.EntityCounter;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.event.GlobalEvents;
import com.huziyang520.merlinlib.event.LivingEntityTickEvent;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 中国人附魔 - 胸甲 I 级
 * 效果：装备后解锁创造飞行；空中受击耐久消耗 ×3；拿下胸甲立即恢复正常
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
 * <li><b>{@code Identifier} → {@code ResourceLocation}；{@code Identifier.fromNamespaceAndPath(a, b)}
 * → {@code new ResourceLocation(a, b)}</b>：见 {@code BrightEnchantment} 类注释第 3 条。</li>
 *
 * <li><b>{@code chestItem.getEnchantments().getLevel(HOLDER)} →
 * {@code EnchantmentHelper.getItemEnchantmentLevel(HOLDER, chestItem)}</b>：见
 * {@code BrightEnchantment} 类注释第 4 条。</li>
 *
 * <li><b>{@code hurtAndBreak(int, LivingEntity, EquipmentSlot)} →
 * {@code hurtAndBreak(int, LivingEntity, Consumer<LivingEntity>)}</b>：见
 * {@code BrightEnchantment} 类注释第 5 条。这里广播的是 {@code EquipmentSlot.CHEST}，
 * 与 26.3 传入的槽位一致。</li>
 *
 * <li><b>⚠️ 最要紧的一条：{@code abilities.flying} 的清理是本模组自己做的，1.20.1 没有加载器
 * 帮忙兜底。</b>26.3 原注释里的分析（已逐条复核，在 1.20.1 上同样成立）：
 * <ul>
 *   <li>原版 {@code ServerPlayer} <b>从不写</b> {@code abilities.flying}
 *       （javap 全类扫描：{@code Abilities} 的字段是 public，但 {@code ServerPlayer} 里只读不写），
 *       所以收回 {@code mayfly} 之后 {@code flying} 仍然是 {@code true}；</li>
 *   <li>客户端 {@code LocalPlayer#aiStep} 里"双击取消飞行"整段被 {@code if (abilities.mayfly)}
 *       包住，{@code mayfly} 为 false 时直接跳过，于是玩家卡在半空、又没法用双击把飞行关掉；</li>
 *   <li>NeoForge 之所以看不出问题，是它给 {@code ServerPlayer#tick()} 打了兜底补丁
 *       （{@code if (this.getAbilities().flying && !this.mayFly()) { ... }}）。
 *       <b>1.20.1 Forge 是否有同款补丁，本移植没有去核实</b>——但即使有，主动清干净也不会有副作用
 *       （清一个已经该为 false 的字段），所以这里保留 26.3 的写法。</li>
 * </ul>
 * 换言之：这一行不是"照抄 Fabric 的补丁"，而是<b>不依赖任何加载器补丁的正确写法</b>。</li>
 *
 * <li><b>{@code abilities.mayfly} 的判定顺序原样保留。</b>{@code else if (abilities.mayfly &&
 * EntityCounter.get(...) > 0)} 两道条件缺一不可：只有 {@code mayfly} 为 true <b>且</b>标记是本模组
 * 自己下的，才动手收回。原版创造/旁观模式与其它模组授予的飞行权限一律不碰。</li>
 *
 * <li><b>没有其它改动。</b>{@code POST_HURT} 事件的 {@code target()/amount()}、
 * {@code player.onGround()}、{@code player.onUpdateAbilities()}、
 * {@code player.getItemBySlot(EquipmentSlot.CHEST)}、{@code MobEffects} 相关调用全部同形。</li>
 * </ol>
 */
public final class ChineseEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":chinese";

	/** EntityCounter 标记：飞行权限由本附魔授予（用于只撤销自己授予的飞行） */
	private static final ResourceLocation FLY_GRANTED =
		new ResourceLocation(PracticalEnchantments.MOD_ID, "chinese_fly_granted");

	/** 本附魔对象（注册回调时解析）。26.3 为 {@code Holder<Enchantment>}。 */
	private static Enchantment HOLDER;


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		HOLDER = PracticalEnchantments.resolveEnchantment(ID);

		// 飞行解锁/撤销：使用全局 LIVING_ENTITY_TICK，拿下胸甲时（即便身上无其他附魔装备）
		// 也能可靠地撤销飞行，避免"永久飞行"。
		GlobalEvents.enableLivingEntityTick();
		GlobalEvents.LIVING_ENTITY_TICK.register(ChineseEnchantment::onLivingTick);

		// 空中受击耐久 ×3
		registrar.register(HOLDER, BuiltInEvents.POST_HURT, ChineseEnchantment::onPostHurt);
	}

	private static void onLivingTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) return;

		boolean hasChinese = hasChinese(player);
		var abilities = player.getAbilities();

		if (hasChinese) {
			// 解锁创造飞行（仅开放飞行权限），并标记"飞行由本附魔授予"
			if (!abilities.mayfly) {
				abilities.mayfly = true;
				player.onUpdateAbilities();
			}
			EntityCounter.set(player, FLY_GRANTED, 1);
		} else if (abilities.mayfly && EntityCounter.get(player, FLY_GRANTED) > 0) {
			// 胸甲被拿下或耐久归零：只撤销本附魔授予的飞行权限。
			// 原版创造/旁观模式以及其他模组授予的 mayfly 一律不碰。
			EntityCounter.set(player, FLY_GRANTED, 0);
			abilities.mayfly = false;
			// ⚠️ 必须把 flying 一并收回，否则会卡在半空中飞下不来：
			// ① 原版 ServerPlayer 从不写 abilities.flying（javap 实证：整个类只读不写），
			//    收回 mayfly 后 flying 仍是 true；
			// ② 客户端 LocalPlayer#aiStep 里"双击取消飞行 / 收飞行状态"整段逻辑被
			//    `if (abilities.mayfly)` 包住（javap 实证：mayfly 为 false 时 ifeq 直接跳过整块），
			//    于是玩家既保持飞行、又再也没法用双击把飞行关掉。
			// NeoForge 之所以"看起来正常"，是它给 ServerPlayer#tick() 打了兜底补丁；
			// 1.20.1 是否有同款补丁未经核实，但主动清干净不会有副作用，故保留。
			abilities.flying = false;
			player.onUpdateAbilities();
		}
	}

	private static void onPostHurt(BuiltInEvents.PostHurtEvent event, EnchantmentContext ctx) {
		if (!(event.target() instanceof ServerPlayer player)) return;

		// 仅对空中受击生效
		if (player.onGround()) return;

		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty()) return;

		// 空中受击：额外造成 2 倍正常单件护甲耐久消耗，合计约 3 倍
		int extra = 2 * Math.max(1, (int) (event.amount() / 4));
		chestItem.hurtAndBreak(extra, player, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
	}

	/** 判断玩家胸甲上是否有中国人附魔 */
	private static boolean hasChinese(ServerPlayer player) {
		ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestItem.isEmpty() || HOLDER == null) return false;
		return EnchantmentHelper.getItemEnchantmentLevel(HOLDER, chestItem) > 0;
	}
}