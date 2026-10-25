// 村民交易：两条附魔书的代码登记。

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

@Mod.EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VillagerTradeRegistration {
	private static final int ARMORER_LEVEL = 3;
	private static final int LIBRARIAN_LEVEL = 3;

	private VillagerTradeRegistration() {
	}

	@SubscribeEvent
	public static void onVillagerTrades(VillagerTradesEvent event) {
		VillagerProfession profession = event.getType();

		if (profession == VillagerProfession.ARMORER) {
			addTrade(event, ARMORER_LEVEL, new BrightHelmetTrade());
		} else if (profession == VillagerProfession.LIBRARIAN) {
			addTrade(event, LIBRARIAN_LEVEL, new DisguiseBookTrade());
		}
	}

	private static void addTrade(VillagerTradesEvent event, int level,
								 net.minecraft.world.entity.npc.VillagerTrades.ItemListing listing) {
		event.getTrades()
			.computeIfAbsent(level, key -> new java.util.ArrayList<>())
			.add(listing);
	}

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
			return new MerchantOffer(cost, helmet, 3, 10, 0.2F);
		}
	}

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

	private static Enchantment enchantment(String id) {
		return net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(id));
	}
}
