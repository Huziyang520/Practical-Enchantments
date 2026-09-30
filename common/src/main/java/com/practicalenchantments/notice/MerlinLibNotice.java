package com.practicalenchantments.notice;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.config.PracticalConfig;
import net.minecraft.network.chat.Component;

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
 * <h2>开关</h2>
 * <p>{@code config/practical_enchantments/common.properties} 里的 {@code notice.merlinlib}
 * （true = 显示，false = 不显示）。</p>
 */
public final class MerlinLibNotice {

	/** 翻译键（语言文件里就是这个名字） */
	public static final String KEY = "practical_enchantments.notice.merlinlib";

	/** 兜底文本：客户端没有本模组时显示这条，与 lang/zh_cn.json 逐字一致 */
	public static final String FALLBACK =
		"Practical Enchantments:本模组将于1.3.0版本开始将前置更换为MerlinLib并改为客户端+服务端模组，不再依赖EnchantLib，并且将优先开发26.3版本，26.2版本和其他版本的移植将被搁置一段时间。该提示可在配置文件中关闭。如果你有好的新附魔想法，欢迎在GitHub或着CurseForge上提交。";

	/** 监听器只注册一次（服务器每次启动都会走到 register()，重复注册会导致重复提示） */
	private static boolean registered = false;

	private MerlinLibNotice() {
	}

	/**
	 * 注册"玩家进入游戏"提示。开关在发送时判定，所以改配置文件后重进世界即可生效。
	 */
	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		MerlinApi.lifecycle().onPlayerJoin(player -> {
			if (!PracticalConfig.isMerlinLibNoticeEnabled()) {
				return;
			}
			player.sendSystemMessage(Component.translatableWithFallback(KEY, FALLBACK));
		});
	}
}
