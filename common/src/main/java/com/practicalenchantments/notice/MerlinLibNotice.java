package com.practicalenchantments.notice;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.api.NoticeMode;
import com.practicalenchantments.config.PracticalConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * <h2>要改这段文案，改这三处（其他地方不用动）</h2>
 * <ol>
 *   <li>{@code assets/practical_enchantments/lang/zh_cn.json} → 键 {@value #KEY}</li>
 *   <li>{@code assets/practical_enchantments/lang/en_us.json} → 同一个键</li>
 *   <li>本类的 {@link #FALLBACK}（客户端没装本模组、拿不到语言文件时的兜底文本，必须与 zh_cn 逐字一致）</li>
 * </ol>
 * <p>另有 {@code assets/practical_enchantments/enchant_sync/lang/zh_cn.json} 与
 * {@code en_us.json} 里的同名键，那是"服务端资源包推送链路"的镜像，改文案时一并同步。</p>
 *
 * <h2>怎么发出去</h2>
 * <p>走 MerlinLib 的进服通知功能（{@code MerlinApi.notices()}）：本模组只说"发什么、什么时候发"，
 * 三种时机（每次进入世界 / 只显示一次 / 首次进入世界）、玩家与服务端的开关、以及设置界面里那份
 * 模组清单都由库负责。这里选择的是 {@link NoticeMode#EVERY_JOIN}，与历史上的进服提示行为一致。</p>
 *
 * <p>注册发生在 MerlinLib 的"服务器启动完成"时机，且排在 {@code PracticalConfig.load()} 之后，
 * 所以 {@code common.properties} 里的开关可以作为本模组声明的<b>默认值</b>传进去；同一个 id
 * 重复注册只是覆盖，换世界再次启动服务器不会重复提示。</p>
 *
 * <h2>开关</h2>
 * <p>{@code config/practical_enchantments/common.properties} 里的 {@code notice.merlinlib}
 * （true = 显示，false = 不显示）。它现在代表"默认"，玩家/管理员还可以在 MerlinLib 的
 * 「通用 → 编辑各模组聊天栏提示」里按模组覆盖这个默认。</p>
 */
public final class MerlinLibNotice {

	/** 翻译键（语言文件里就是这个名字） */
	public static final String KEY = "practical_enchantments.notice.merlinlib";

	/** 兜底文本：客户端没有本模组时显示这条，与 lang/zh_cn.json 逐字一致 */
	public static final String FALLBACK =
		"Practical Enchantments:本模组将于1.3.0版本开始将前置更换为MerlinLib并改为客户端+服务端模组，不再依赖EnchantLib，并且将优先开发26.3版本，26.2版本和其他版本的移植将被搁置一段时间。该提示可在配置文件中关闭。如果你有好的新附魔想法，欢迎在GitHub或着CurseForge上提交。";

	/** 通知 id：命名空间就是归属模组，MerlinLib 的设置界面按它分组 */
	private static final Identifier ID =
		Identifier.fromNamespaceAndPath("practical_enchantments", "merlinlib_change");

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
}
