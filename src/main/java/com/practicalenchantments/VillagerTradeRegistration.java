// 村民交易：26.3 的两条数据包交易（data/practical_enchantments/villager_trade/* 加 data/minecraft/tags/villager_trade/*）在 1.21.1 ...

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

public final class VillagerTradeRegistration {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int ARMORER_LEVEL = 3;
	private static final int LIBRARIAN_LEVEL = 3;

	private VillagerTradeRegistration() {
	}

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

	private static void addTrade(VillagerTradesEvent event, int level, VillagerTrades.ItemListing listing) {
		event.getTrades()
			.computeIfAbsent(level, key -> new ArrayList<>())
			.add(listing);
	}

	private static Holder<Enchantment> resolve(HolderLookup.RegistryLookup<Enchantment> enchantments, String id) {
		ResourceLocation key = ResourceLocation.parse(id);
		return enchantments.get(ResourceKey.create(Registries.ENCHANTMENT, key))
			.map(holder -> (Holder<Enchantment>) holder)
			.orElseGet(() -> {
				LOGGER.warn("[PracticalEnchantments] 村民交易跳过：附魔未注册 {}", id);
				return null;
			});
	}

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
			return new MerchantOffer(cost, helmet, 3, 10, 0.2F);
		}
	}

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
