package com.practicalenchantments;

import com.practicalenchantments.enchantment.BrightEnchantment;
import com.practicalenchantments.enchantment.ChineseEnchantment;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import org.slf4j.Logger;

/**
 * 村民交易：26.3 的两条数据包交易（{@code data/practical_enchantments/villager_trade/*} 加
 * {@code data/minecraft/tags/villager_trade/*}）在 1.21.1 上没有落脚点，改为代码登记。
 *
 * <h2>为什么必须改</h2>
 *
 * <p>1.21.1 <b>没有 {@code villager_trade} 注册表</b>（javap 实证：{@code Registries} 里只有
 * {@code VILLAGER_PROFESSION} / {@code VILLAGER_TYPE}，没有 trade 键），村民交易仍是原版
 * {@code VillagerTrades} 里的静态表，扩展点是 NeoForge 的 {@link VillagerTradesEvent}：
 * 事件里拿得到职业与「等级 → 交易列表」的映射，往对应等级追加 {@code ItemListing} 即可。
 * 因此那 4 个 JSON（2 条交易 + 2 个 {@code minecraft} 标签）在本线已删除，语义在本类重建。</p>
 *
 * <h2>附魔从哪来</h2>
 *
 * <p>1.21.1 的附魔是数据包动态注册表，{@code BuiltInRegistries.ENCHANTMENT} 里没有它们，
 * 只能在拿得到 {@code RegistryAccess} 的时候解析成 {@code Holder}。
 * {@link VillagerTradesEvent#getRegistryAccess()} 正好提供了它，所以在事件里解析一次、
 * 把 {@code Holder} 交给两条交易，避免每次 {@code getOffer} 都查表。</p>
 *
 * <h2>两条交易的原样还原</h2>
 *
 * <ul>
 *   <li>盔甲匠 3 级：18 绿宝石 → 附魔「明朗 I」的钻石头盔，3 次，10 经验，声望折扣 0.2</li>
 *   <li>图书管理员 3 级：24 绿宝石 → 附魔「伪装 I」的附魔书，3 次，15 经验，声望折扣 0.2</li>
 * </ul>
 *
 * <p>{@code reputation_discount} 在原版就是 {@code MerchantOffer} 的 {@code priceMultiplier}
 * （最后一个 float），直接传 0.2F。附魔书用 {@link EnchantedBookItem#createForEnchantment}
 * 生成 —— 把书当普通物品 {@code enchant} 只会得到「一本被附魔的书」，书里不会写着那个附魔。</p>
 */
public final class VillagerTradeRegistration {

	/** 本模组日志。 */
	private static final Logger LOGGER = LogUtils.getLogger();

	/** 盔甲匠 3 级：卖附魔「明朗 I」的钻石头盔。 */
	private static final int ARMORER_LEVEL = 3;
	/** 图书管理员 3 级：卖附魔「伪装 I」的附魔书。 */
	private static final int LIBRARIAN_LEVEL = 3;

	private VillagerTradeRegistration() {
	}

	/**
	 * 收到某职业的交易表时，把本模组的交易追加到对应等级。
	 *
	 * @param event 职业与其等级 → 交易列表，另带注册表访问
	 */
	public static void onVillagerTrades(VillagerTradesEvent event) {
		VillagerProfession profession = event.getType();
		HolderLookup.RegistryLookup<Enchantment> enchantments =
			event.getRegistryAccess().lookupOrThrow(Registries.ENCHANTMENT);

		if (profession == VillagerProfession.ARMORER) {
			Holder<Enchantment> bright = resolve(enchantments, BrightEnchantment.ID);
			if (bright != null) {
				addTrade(event, ARMORER_LEVEL, new BrightHelmetTrade(bright));
			}
		} else if (profession == VillagerProfession.LIBRARIAN) {
			Holder<Enchantment> disguise = resolve(enchantments, "practical_enchantments:disguise");
			if (disguise != null) {
				addTrade(event, LIBRARIAN_LEVEL, new DisguiseBookTrade(disguise));
			}
		}
	}

	/**
	 * 往某职业的某一等级追加一条交易。
	 *
	 * <p>用 {@code computeIfAbsent} 而不是直接 {@code get(...).add(...)}：本版本每个职业的 5 个等级
	 * 都由原版预先填好了列表，所以 {@code get} 正常情况下不会是 null——但这是一条依赖原版内部实现的
	 * 假设，而一旦不成立就是村民职业注册时的 NPE。空列表比崩溃好。</p>
	 *
	 * @param event   交易表事件
	 * @param level   职业等级
	 * @param listing 要追加的交易
	 */
	private static void addTrade(VillagerTradesEvent event, int level, VillagerTrades.ItemListing listing) {
		event.getTrades()
			.computeIfAbsent(level, key -> new ArrayList<>())
			.add(listing);
	}

	/**
	 * 按 id 解析附魔 Holder。
	 *
	 * @param enchantments 附魔注册表
	 * @param id           附魔全名
	 * @return Holder，未注册时返回 {@code null} 并记一条日志
	 */
	private static Holder<Enchantment> resolve(HolderLookup.RegistryLookup<Enchantment> enchantments, String id) {
		ResourceLocation key = ResourceLocation.parse(id);
		return enchantments.get(ResourceKey.create(Registries.ENCHANTMENT, key))
			.map(holder -> (Holder<Enchantment>) holder)
			.orElseGet(() -> {
				// 附魔没注册成功时启动阶段已有更明确的日志；这里只说明这条交易被跳过。
				LOGGER.warn("[PracticalEnchantments] 村民交易跳过：附魔未注册 {}", id);
				return null;
			});
	}

	/**
	 * 盔甲匠交易：18 绿宝石 → 一枚附魔「明朗 I」的钻石头盔。
	 *
	 * <p>{@code getOffer} 返回 {@code null} 表示本次不提供该交易（原版约定），比抛异常让整个
	 * 村民职业注册失败要好。</p>
	 */
	private static final class BrightHelmetTrade implements VillagerTrades.ItemListing {

		private final Holder<Enchantment> enchantment;

		private BrightHelmetTrade(Holder<Enchantment> enchantment) {
			this.enchantment = enchantment;
		}

		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource random) {
			ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
			helmet.enchant(this.enchantment, 1);

			ItemCost cost = new ItemCost(Items.EMERALD, 18);
			// MerchantOffer(buy, sell, maxUses, xp, priceMultiplier)
			return new MerchantOffer(cost, helmet, 3, 10, 0.2F);
		}
	}

	/**
	 * 图书管理员交易：24 绿宝石 → 一本附魔「伪装 I」的附魔书。
	 */
	private static final class DisguiseBookTrade implements VillagerTrades.ItemListing {

		private final Holder<Enchantment> enchantment;

		private DisguiseBookTrade(Holder<Enchantment> enchantment) {
			this.enchantment = enchantment;
		}

		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource random) {
			ItemStack book = EnchantedBookItem.createForEnchantment(
				new EnchantmentInstance(this.enchantment, 1));

			ItemCost cost = new ItemCost(Items.EMERALD, 24);
			return new MerchantOffer(cost, book, 3, 15, 0.2F);
		}
	}
}
