// 联动附魔的总开关：联动模组不在时一条都不登记，游戏内也就完全看不到这些附魔。

package com.practicalenchantments.enchantment.linked;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.platform.Services;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.linked.aether.AetherGodwood;
import com.practicalenchantments.enchantment.linked.aether.AetherSkySlayer;
import com.practicalenchantments.enchantment.linked.twilightforest.TfNightSlayer;
import com.practicalenchantments.enchantment.linked.twilightforest.TfQuench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LinkedEnchantments {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	public static final String TWILIGHT_FOREST = "twilightforest";
	public static final String AETHER_II = "aether_ii";
	public static final String AETHER = "aether";
	private static boolean twilightForest;
	private static boolean aether;
	private static String aetherNamespace = AETHER_II;

	private LinkedEnchantments() {
	}

	public static void registerContent() {
		twilightForest = Services.PLATFORM.isModLoaded(TWILIGHT_FOREST);
		boolean aetherTwo = Services.PLATFORM.isModLoaded(AETHER_II);
		aether = aetherTwo || Services.PLATFORM.isModLoaded(AETHER);
		aetherNamespace = aetherTwo ? AETHER_II : AETHER;

		if (twilightForest) {
			TfQuench.registerContent();
			TfNightSlayer.registerContent();
		}
		if (aether) {
			AetherGodwood.registerContent(aetherNamespace);
			AetherSkySlayer.registerContent(aetherNamespace);
		}
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		if (twilightForest) {
			register("暮色森林", () -> {
				TfQuench.registerCallbacks(registrar);
				TfNightSlayer.registerCallbacks(registrar);
			});
		}
		if (aether) {
			register("天境", () -> {
				AetherGodwood.registerCallbacks(registrar, aetherNamespace);
				AetherSkySlayer.registerCallbacks(registrar);
			});
		}
	}

	private static void register(String modName, Runnable registration) {
		try {
			registration.run();
		} catch (RuntimeException failure) {
			LOGGER.error("[PracticalEnchantments] 联动附魔（{}）未能在注册表里找到，本次已跳过", modName, failure);
		}
	}
}
