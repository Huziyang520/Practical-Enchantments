package com.practicalenchantments.config;

import com.practicalenchantments.PracticalEnchantments;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Practical Enchantments 通用配置（服务端）。
 *
 * <p>文件位置：{@code config/practical_enchantments/common.properties}。
 * 首次运行会自动生成一份带中文注释的模板；改完保存后重进世界 / 重启服务器生效。</p>
 *
 * <p>本模组是服务端模组，只读服务端这一份配置，不需要与客户端同步。</p>
 *
 * <h2>1.20.1 移植：模板从文本块改成字符串拼接</h2>
 *
 * <p>本类逻辑是直移：路径、键名、默认值、日志、异常兜底都与 26.3 逐字相同。唯一改的是
 * {@link #TEMPLATE} 的写法。26.3 用 Java 文本块（{@code """}）写模板，这里改成
 * {@link String#join}{@code ("\n", ...) + "\n"}。</p>
 *
 * <p><b>如实记下这一点：</b>文本块自 Java 15 起就是标准语法，本工程用 JDK 17 编译，
 * 文本块其实<b>可以</b>编译，所以这不是编译器逼出来的差异，而是照着本工程的移植约定
 * （目标 Java 17、不使用文本块与 {@code Math.clamp}）改的。两种写法产生的字节完全一致：
 * 文本块会把行尾规范化为 {@code \n}，拼接版也显式用 {@code \n} 并在末尾补一个换行，
 * 生成的 {@code common.properties} 与 26.3 一模一样。若日后要还原成文本块，直接删掉
 * {@code TEMPLATE_LINES} 即可，语义没有任何差别。</p>
 */
public final class PracticalConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);

	/** 配置目录名：{@code config/<此名>/} */
	private static final String DIR_NAME = "practical_enchantments";

	/** 配置文件名 */
	private static final String FILE_NAME = "common.properties";

	/** 配置键：玩家进入游戏时是否显示"前置更换为 MerlinLib"的预告 */
	public static final String KEY_NOTICE_MERLINLIB = "notice.merlinlib";

	/**
	 * 生成模板时写入的整份文件内容，按行存放。
	 *
	 * <p>{@link Properties#store} 会丢掉注释，故模板手写（UTF-8 无 BOM）。</p>
	 */
	private static final String[] TEMPLATE_LINES = {
		"# Practical Enchantments —— 通用配置（服务端）",
		"# 改完保存后：重进世界 / 重启服务器生效。",
		"#",
		"# 是否在玩家进入游戏时，在聊天栏显示「前置将由 EnchantLib 更换为 MerlinLib」的预告：",
		"#   true  = 显示（默认）",
		"#   false = 不显示",
		"#",
		"# 预告的文案不在本文件里，而在语言文件里，键名：practical_enchantments.notice.merlinlib",
		"#   assets/practical_enchantments/lang/zh_cn.json",
		"#   assets/practical_enchantments/lang/en_us.json",
		"#   （assets/practical_enchantments/enchant_sync/lang/ 下的同名键是资源包推送链路的镜像）",
		"#",
		"# 本项现在代表「默认值」：提示由 MerlinLib 的进服通知功能发送，还可在",
		"# MerlinLib 设置界面「通用 → 编辑各模组聊天栏提示」里按模组覆盖它。",
		"notice.merlinlib=true"
	};

	/** 模板全文：与 26.3 文本块产生的字节一致（{@code \n} 行尾 + 末尾换行）。 */
	private static final String TEMPLATE = String.join("\n", TEMPLATE_LINES) + "\n";

	/** 是否显示 MerlinLib 更换预告（默认 true） */
	private static volatile boolean merlinLibNoticeEnabled = true;

	private PracticalConfig() {
	}

	/**
	 * 是否显示 MerlinLib 更换预告。
	 *
	 * @return true 表示显示
	 */
	public static boolean isMerlinLibNoticeEnabled() {
		return merlinLibNoticeEnabled;
	}

	/**
	 * 读取配置：文件不存在则先按模板生成，再解析。服务器每次启动时调用一次。
	 */
	public static void load() {
		// 与 MerlinLib 自己的配置文件同一套约定：相对路径（由游戏目录解析）两端一致，不需要平台服务。
		// 最终路径：config/practical_enchantments/common.properties
		Path file = Path.of("config", DIR_NAME, FILE_NAME);
		try {
			if (!Files.isRegularFile(file)) {
				Files.createDirectories(file.getParent());
				Files.writeString(file, TEMPLATE, StandardCharsets.UTF_8);
				LOGGER.info("[PracticalEnchantments] 已生成默认配置文件: {}", file.toAbsolutePath());
			}

			Properties props = new Properties();
			try (Reader reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
				props.load(reader);
			}

			boolean enabled = Boolean.parseBoolean(props.getProperty(KEY_NOTICE_MERLINLIB, "true").trim());
			merlinLibNoticeEnabled = enabled;
			LOGGER.info("[PracticalEnchantments] 配置已加载: {}= {} ({})", KEY_NOTICE_MERLINLIB, enabled, file.toAbsolutePath());
		} catch (IOException e) {
			LOGGER.error("[PracticalEnchantments] 读取配置失败 {}，沿用默认配置（notice.merlinlib=true）: {}",
				file.toAbsolutePath(), e.getMessage(), e);
		}
	}
}
