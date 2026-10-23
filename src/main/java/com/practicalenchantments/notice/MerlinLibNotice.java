package com.practicalenchantments.notice;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.api.NoticeMode;
import com.practicalenchantments.config.PracticalConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 进服预告的文案与开关（文案在语言文件的 practical_enchantments.notice.merlinlib 键里）。
 */
public final class MerlinLibNotice {

	/** 翻译键（语言文件里就是这个名字） */
	public static final String KEY = "practical_enchantments.notice.merlinlib";

	/** 兜底文本：客户端没有本模组时显示这条，与 lang/en_us.json 逐字一致 */
	public static final String FALLBACK =
		"Practical Enchantments: Starting from version 1.3.0, this mod will switch its dependency to MerlinLib and become a client + server mod, no longer depending on EnchantLib. Development will prioritize version 1.20.1, while synchronization/porting for 26.2, 26.3, and other versions will be temporarily suspended for a while. If you have good ideas for new enchantments, welcome to submit them on GitHub or CurseForge. This notice can be disabled in the config file or in MerlinLib's config screen.";

	/** 通知 id：命名空间就是归属模组，MerlinLib 的设置界面按它分组 */
	private static final ResourceLocation ID =
		ResourceLocation.fromNamespaceAndPath("practical_enchantments", "merlinlib_change");

	private MerlinLibNotice() {
	}

	/**
	 * 注册"玩家进入游戏"提示：交给 MerlinLib 按通知的时机与开关发送。
	 *
	 * <p>{@code enabledByDefault} 取本模组自己配置里的值，因此老配置文件里的 {@code false} 依旧有效，
	 * 只是现在还能被 MerlinLib 的逐模组开关覆盖。</p>
	 */
	public static void register() {
		MerlinApi.notices().register(ID, NoticeMode.EVERY_JOIN,
			Component.translatableWithFallback(KEY, FALLBACK),
			PracticalConfig.isMerlinLibNoticeEnabled());
	}

	/**
	 * 在模组构造期先登记一次（默认开启）。
	 *
	 * <p>MerlinLib 的「编辑各模组聊天栏提示」是客户端界面：它只列**已经注册过通知的模组**。本模组的正式注册
	 * 发生在服务器启动完成（那时才读得到自己的配置），于是"没进过世界就看不到本模组那一行"。
	 * 这里先用默认值登记一次让界面立刻能看到，进世界后再由 {@link #register()} 用配置里的真默认值覆盖
	 * （同一 id 重复注册是覆盖）。</p>
	 */
	public static void registerEarly() {
		MerlinApi.notices().register(ID, NoticeMode.EVERY_JOIN,
			Component.translatableWithFallback(KEY, FALLBACK), true);
	}
}
