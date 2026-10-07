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
 * 村民交易：把 26.3 的两条数据包交易改成代码登记。
 *
 * <h2>为什么必须改</h2>
 *
 * <p>26.3 的村民交易是动态注册表条目（{@code data/practical_enchantments/villager_trade/*} 加
 * {@code data/minecraft/tags/villager_trade/*}）。1.20.1 <b>没有这个注册表</b>，村民交易是硬编码在
 * 原版 {@code VillagerTrades} 里的静态表。1.20.1 的扩展点是 Forge 的 {@code VillagerTradesEvent}：
 * 事件里拿得到职业和"等级 → 交易列表"的映射，往对应等级追加 {@code ItemListing} 即可。</p>
 *
 * <h2>为什么这个类不叫 VillagerTrades（记录一次真实的编译期教训）</h2>
 *
 * <p>本类最初就叫 {@code VillagerTrades}，同时 {@code import net.minecraft.world.entity.npc.VillagerTrades}。
 * 两者简单名相同，javac 直接报 <i>"VillagerTrades is already defined in this compilation unit"</i>：
 * 一个编译单元里，自己声明的类型与 import 进来的类型不能同名。</p>
 *
 * <p>三种绕法（改名、全体写全限定名、只 import 内层接口）里选了改名：本类要实现的
 * {@link net.minecraft.world.entity.npc.VillagerTrades.ItemListing} 必须能被引用，而写全限定名的代码
 * 在每个方法签名里都会变成噪音；改名只花一次，且名字本身也更准确——它做的是"登记交易"，不是
 * "交易表"。</p>
 *
 * <h2>事件总线</h2>
 *
 * <p>挂在 <b>游戏事件总线</b> 上（{@code Bus.FORGE}，也是 {@code @Mod.EventBusSubscriber} 的默认值，
 * 这里仍写出来以免靠记忆）。交易表是在村民职业注册时构建的，那是游戏事件而不是模组加载事件。</p>
 *
 * <h2>两条交易的原样还原</h2>
 *
 * <p>数据包里的 {@code gives.components} 在 1.20.1 没有对应物（那是数据组件体系），所以附魔改用本
 * 版本的写法写上：装备用 {@code ItemStack.enchant(Enchantment, int)}，附魔书用
 * {@link EnchantedBookItem#addEnchantment(ItemStack, EnchantmentInstance)}。其余数值（
 * {@code max_uses}、{@code wants.count}、{@code xp}、{@code reputation_discount}）逐一对应到
 * {@code MerchantOffer} 的构造参数。</p>
 *
 * <p>两条交易的原定义：</p>
 * <ul>
 *   <li>盔甲匠 3 级：18 绿宝石 → 附魔「明朗 I」的钻石头盔，3 次，10 经验，声望折扣 0.2</li>
 *   <li>图书管理员 3 级：24 绿宝石 → 附魔「伪装 I」的附魔书，3 次，15 经验，声望折扣 0.2</li>
 * </ul>
 *
 * <p>注意本版本 {@code MerchantOffer} 没有单独的"声望折扣"参数，它是交易自身的
 * {@code priceMultiplier}（最后一个 float）。数据包里的 {@code reputation_discount} 语义与此一致，
 * 所以直接传 0.2F。</p>
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
