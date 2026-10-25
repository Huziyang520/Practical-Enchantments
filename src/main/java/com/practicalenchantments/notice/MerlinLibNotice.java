// 进服预告的文案与开关（文案在语言文件的 practical_enchantments.notice.merlinlib 键里）。

package com.practicalenchantments.notice;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.api.NoticeMode;
import com.practicalenchantments.config.PracticalConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class MerlinLibNotice {
	public static final String KEY = "practical_enchantments.notice.merlinlib";

	public static final String FALLBACK =
		"Practical Enchantments: Starting from version 1.3.0, this mod will switch its dependency to MerlinLib and become a client + server mod, no longer depending on EnchantLib. Development will prioritize version 1.20.1, while synchronization/porting for 26.2, 26.3, and other versions will be temporarily suspended for a while. If you have good ideas for new enchantments, welcome to submit them on GitHub or CurseForge. This notice can be disabled in the config file or in MerlinLib's config screen.";

	private static final ResourceLocation ID =
		ResourceLocation.fromNamespaceAndPath("practical_enchantments", "merlinlib_change");

	private MerlinLibNotice() {
	}

	public static void register() {
		MerlinApi.notices().register(ID, NoticeMode.EVERY_JOIN,
			Component.translatableWithFallback(KEY, FALLBACK),
			PracticalConfig.isMerlinLibNoticeEnabled());
	}

	public static void registerEarly() {
		MerlinApi.notices().register(ID, NoticeMode.EVERY_JOIN,
			Component.translatableWithFallback(KEY, FALLBACK), true);
	}
}
