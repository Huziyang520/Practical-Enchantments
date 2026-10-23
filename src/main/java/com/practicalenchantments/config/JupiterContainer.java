package com.practicalenchantments.config;

import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.container.FileConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.practicalenchantments.PracticalEnchantments;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jupiter 版「原版附魔调整」界面：镜像写在 Jupiter 自己的 jcfg 里，权威存储仍是 {@link ForgeConfig} 的 toml。
 */
public final class JupiterContainer extends FileConfigContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);

	/** Jupiter 自己写的那份镜像。 */
	private static final String FILE = "config/practical_enchantments/settings.jcfg";

	/** 界面上收集到的值，玩家保存时写回配置。 */
	private final Map<String, Boolean> pending = new LinkedHashMap<>();

	public JupiterContainer() {
		super(new ResourceLocation(PracticalEnchantments.MOD_ID, VanillaTweaks.SECTION_KEY),
				Component.translatable(VanillaTweaks.SECTION_LANG_KEY), FILE);
	}

	/** 按配置文件此刻的值建页签。 */
	@Override
	public void init() {
		ConfigGroup group = createTab(VanillaTweaks.SECTION_KEY,
				Component.translatable(VanillaTweaks.SECTION_LANG_KEY));
		for (String key : ForgeConfig.KEYS) {
			group.addEntry(entry(key));
		}
	}

	/** 按权威的配置文件重建页签：jcfg 只是镜像，读回它会让过期的镜像盖过手改过的 toml。 */
	@Override
	public void load() {
		this.configTabs.clear();
		init();
	}

	/** 先写 Jupiter 的镜像，再把同一批值写回 toml。 */
	@Override
	public void save() {
		try {
			Files.createDirectories(Path.of(FILE).getParent());
		} catch (IOException exception) {
			LOGGER.warn("[PracticalEnchantments] 无法创建 {} 所在目录", FILE, exception);
		}
		super.save();
		if (this.pending.isEmpty()) {
			return;
		}
		Map<String, Boolean> values = new LinkedHashMap<>(this.pending);
		this.pending.clear();
		ForgeConfig.write(values);
	}

	private ConfigEntry<?> entry(String key) {
		return BooleanEntry.builder(Component.translatable(VanillaTweaks.nameKey(key)), VanillaTweaks.value(key))
				.key(key)
				.tooltip(Component.translatable(VanillaTweaks.tooltipKey(key)))
				.callback((chosen, initial, reset) -> this.pending.put(key, chosen))
				.build();
	}
}
