// 进服预告：文案在语言文件的 {@value #KEY} 键里，#FALLBACK 是无语言文件时的兜底。

package com.practicalenchantments.notice;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.api.NoticeMode;
import com.practicalenchantments.config.PracticalConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class MerlinLibNotice {
	public static final String KEY = "practical_enchantments.notice.merlinlib";

	public static final String FALLBACK =
		"Practical Enchantments: if you have an idea for this mod, a new enchantment for example, or run into any problem while playing, tell the author. This notice can be switched off in the MerlinLib configuration screen.";

	private static final Identifier ID =
		Identifier.fromNamespaceAndPath("practical_enchantments", "merlinlib_change");

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
