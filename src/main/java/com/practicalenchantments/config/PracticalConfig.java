// Practical Enchantments 通用配置（服务端）。

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

public final class PracticalConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final String DIR_NAME = "practical_enchantments";
	private static final String FILE_NAME = "common.properties";
	public static final String KEY_NOTICE_MERLINLIB = "notice.merlinlib";

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
	private static final String TEMPLATE = String.join("\n", TEMPLATE_LINES) + "\n";
	private static volatile boolean merlinLibNoticeEnabled = true;

	private PracticalConfig() {
	}

	public static boolean isMerlinLibNoticeEnabled() {
		return merlinLibNoticeEnabled;
	}

	public static void load() {
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
