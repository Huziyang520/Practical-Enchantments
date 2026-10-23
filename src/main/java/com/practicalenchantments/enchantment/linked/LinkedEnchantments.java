package com.practicalenchantments.enchantment.linked;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.huziyang520.merlinlib.platform.Services;
import com.practicalenchantments.PracticalEnchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 联动附魔的总开关：联动模组不在时一条都不登记，游戏内也就完全看不到这些附魔。
 */
public final class LinkedEnchantments {

	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);

	/** 暮色森林的模组 id。 */
	public static final String TWILIGHT_FOREST = "twilightforest";
	/** 天境二的模组 id。 */
	public static final String AETHER_II = "aether_ii";
	/** 天境（延续版）的模组 id，天境二缺席时作为后备。 */
	public static final String AETHER = "aether";

	private static boolean twilightForest;
	private static boolean aether;
	private static String aetherNamespace = AETHER_II;

	private LinkedEnchantments() {
	}

	/** 由模组入口在构建期调用一次。 */
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

	/**
	 * 登记事件回调，附魔已进注册表之后调用一次。
	 *
	 * @param registrar 事件注册器
	 */
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

	/** 注册表里没有联动附魔时只记日志：联动内容出问题不该让整个世界打不开。 */
	private static void register(String modName, Runnable registration) {
		try {
			registration.run();
		} catch (RuntimeException failure) {
			LOGGER.error("[PracticalEnchantments] 联动附魔（{}）未能在注册表里找到，本次已跳过", modName, failure);
		}
	}
}
