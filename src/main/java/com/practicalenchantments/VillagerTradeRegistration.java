package com.practicalenchantments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 村民交易：两条附魔书的代码登记。
 */
@Mod.EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VillagerTradeRegistration {

	/** 盔甲匠 3 级：卖附魔「明朗 I」的钻石头盔。 */
	private static final int ARMORER_LEVEL = 3;
	/** 图书管理员 3 级：卖附魔「伪装 I」的附魔书。 */
	private static final int LIBRARIAN_LEVEL = 3;

	private VillagerTradeRegistration() {
	}

	/**
	 * 收到某职业的交易表时，把本模组的交易追加到对应等级。
	 *
	 * @param event 职业与其等级 → 交易列表
	 */
	@SubscribeEvent
	public static void onVillagerTrades(VillagerTradesEvent event) {
		VillagerProfession profession = event.getType();

		if (profession == VillagerProfession.ARMORER) {
			addTrade(event, ARMORER_LEVEL, new BrightHelmetTrade());
		} else if (profession == VillagerProfession.LIBRARIAN) {
			addTrade(event, LIBRARIAN_LEVEL, new DisguiseBookTrade());
		}
	}

	/**
	 * 往某职业的某一等级追加一条交易。
	 *
	 * <p>用 {@code computeIfAbsent} 而不是直接 {@code get(...).add(...)}：本版本每个职业的 5 个等级
	 * 都由原版预先填好了列表，所以 {@code get} 正常情况下不会是 null——但这是一条依赖原版内部实现的
	 * 假设，而一旦不成立就是村民职业注册时的 NPE，代价与收益不成比例。空列表比崩溃好。</p>
	 *
	 * @param event   交易表事件
	 * @param level   职业等级
	 * @param listing 要追加的交易
	 */
	private static void addTrade(VillagerTradesEvent event, int level,
								 net.minecraft.world.entity.npc.VillagerTrades.ItemListing listing) {
		event.getTrades()
			.computeIfAbsent(level, key -> new java.util.ArrayList<>())
			.add(listing);
	}

	/**
	 * 盔甲匠交易：18 绿宝石 → 一枚附魔「明朗 I」的钻石头盔。
	 *
	 * <p>附魔只有 1 级，与数据包定义一致。找不到附魔时返回 {@code null}——Forge 约定
	 * {@code getOffer} 返回 {@code null} 表示本次不提供该交易，这比抛异常让整个村民职业注册失败要好。
	 * 附魔取不到只可能发生在"本模组的附魔没注册成功"，那种情况在启动时已经有更明确的日志了。</p>
	 */
	private static final class BrightHelmetTrade implements net.minecraft.world.entity.npc.VillagerTrades.ItemListing {

		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource random) {
			Enchantment bright = enchantment("practical_enchantments:bright");
			if (bright == null) {
				return null;
			}
			ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
			helmet.enchant(bright, 1);

			ItemStack cost = new ItemStack(Items.EMERALD, 18);
			// MerchantOffer(buy, sell, maxUses, xp, priceMultiplier)
			return new MerchantOffer(cost, helmet, 3, 10, 0.2F);
		}
	}

	/**
	 * 图书管理员交易：24 绿宝石 → 一本附魔「伪装 I」的附魔书。
	 *
	 * <p>附魔书的内容写进它自己的存储列表，这一点与装备不同：装备要用
	 * {@code ItemStack#enchant}，附魔书要用
	 * {@link EnchantedBookItem#addEnchantment(ItemStack, EnchantmentInstance)}。把书当成普通物品来
	 * {@code enchant} 会让它变成"一本被附魔的书"，而不是"写着某个附魔的书"，村民界面里也不会显示。</p>
	 */
	private static final class DisguiseBookTrade implements net.minecraft.world.entity.npc.VillagerTrades.ItemListing {

		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource random) {
			Enchantment disguise = enchantment("practical_enchantments:disguise");
			if (disguise == null) {
				return null;
			}
			ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(disguise, 1));

			ItemStack cost = new ItemStack(Items.EMERALD, 24);
			return new MerchantOffer(cost, book, 3, 15, 0.2F);
		}
	}

	/**
	 * 按 id 取附魔。
	 *
	 * @param id 附魔全名
	 * @return 附魔，未注册时为 {@code null}
	 */
	private static Enchantment enchantment(String id) {
		return net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(id));
	}
}
