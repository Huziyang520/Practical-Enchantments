package com.practicalenchantments;

import com.enchantlib.api.EnchantmentEntrypoint;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.ExclusiveGroupRegistrar;
import com.enchantlib.api.LootInjectionRegistrar;
import com.enchantlib.api.VillagerTradeRegistrar;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.enchantment.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Practical Enchantments 模组入口。
 */
public final class PracticalEnchantments implements EnchantmentEntrypoint {

	public static final String MOD_ID = "practical_enchantments";

	@Override
	public void onRegisterEnchantments(EnchantmentRegistrar registrar) {
		ChineseEnchantment.register(registrar);
		LumberjackEnchantment.register(registrar);
		PowerfulEnchantment.register(registrar);
		BrightEnchantment.register(registrar);
		DestructionEnchantment.register(registrar);
		IncinerateEnchantment.register(registrar);
		AerialHasteEnchantment.register(registrar);
	}

	@Override
	public void onRegisterExclusiveGroups(ExclusiveGroupRegistrar registrar) {
		// 暂无互斥组
	}

	@Override
	public void onRegisterLootInjections(LootInjectionRegistrar registrar) {
		// 暂未配置
	}

	@Override
	public void onRegisterVillagerTrades(VillagerTradeRegistrar registrar) {
		// 暂未配置
	}

	@Override
	public void onRegisterEventCallbacks(EnchantmentEventRegistrar registrar,
										 HolderLookup.Provider registries) {
		ChineseEnchantment.registerCallbacks(registrar, registries);
		LumberjackEnchantment.registerCallbacks(registrar, registries);
		PowerfulEnchantment.registerCallbacks(registrar, registries);
		BrightEnchantment.registerCallbacks(registrar, registries);
		DestructionEnchantment.registerCallbacks(registrar, registries);
		IncinerateEnchantment.registerCallbacks(registrar, registries);
		AerialHasteEnchantment.registerCallbacks(registrar, registries);
	}

	/**
	 * 通过注册表解析附魔 Holder。
	 */
	public static Holder<Enchantment> resolveEnchantment(HolderLookup.Provider registries, String id) {
		Identifier enchantId = Identifier.parse(id);
		ResourceKey<Enchantment> resourceKey = ResourceKey.create(Registries.ENCHANTMENT, enchantId);
		return registries.lookupOrThrow(Registries.ENCHANTMENT)
			.get(resourceKey)
			.orElseThrow(() -> new IllegalStateException("Practical Enchantments: 附魔未注册: " + id));
	}
}
