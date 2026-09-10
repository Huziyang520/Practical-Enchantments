package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.util.SmeltingLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 自动烧炼附魔(auto_smelt)。
 *
 * <p>镐/铲 I 级,破坏方块直接掉落熔炼产物。使用
 * {@link SmeltingLookup#smeltOrOriginal(ItemStack)} 便捷方法,
 * 对每个掉落物尝试熔炼,无配方的物品保持原样。</p>
 */
public final class AutoSmeltEnchantment {

	public static final String AUTO_SMELT_ID = MOD_ID + ":auto_smelt";

	private AutoSmeltEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(AUTO_SMELT_ID)
			.description("Auto Smelt")
			.supportedItems("#minecraft:enchantable/mining")
			.weight(2).maxLevel(1)
			.minCost(15, 0).maxCost(65, 0).anvilCost(8)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> autoSmelt = resolveEnchantment(registries, AUTO_SMELT_ID);
		registrar.register(autoSmelt, BuiltInEvents.MODIFY_BLOCK_DROPS,
			AutoSmeltEnchantment::onAutoSmelt);
	}

	/**
	 * 自动烧炼回调:将方块掉落物转换为熔炼产物。
	 *
	 * <p>使用 {@link SmeltingLookup#smeltOrOriginal(ItemStack)} 便捷方法,
	 * 对每个掉落物尝试熔炼,无配方的物品保持原样。</p>
	 */
	private static void onAutoSmelt(BuiltInEvents.ModifyBlockDropsEvent event, EnchantmentContext ctx) {
		event.transformDrops(SmeltingLookup::smeltOrOriginal);
	}
}
