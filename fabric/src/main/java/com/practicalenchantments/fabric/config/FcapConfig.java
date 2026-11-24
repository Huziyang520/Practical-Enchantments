// Fabric 侧配置：用 Forge Config API Port 生成与 NeoForge 同一份 SPEC，界面因此和 NeoForge 一致。

package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.ConfigOption;
import com.practicalenchantments.config.ConfigSchema;
import com.practicalenchantments.config.Tunables;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class FcapConfig {
	private static final Map<String, ModConfigSpec.ConfigValue<?>> VALUES = new LinkedHashMap<>();
	public static final ModConfigSpec SPEC = build();

	private FcapConfig() {
	}

	public static void install() {
		ConfigRegistry.INSTANCE.register(PracticalEnchantments.MOD_ID, type(), SPEC);
	}

	public static void apply() {
		if (!SPEC.isLoaded()) {
			Tunables.applyAll(key -> null);
			return;
		}
		Tunables.applyAll(key -> {
			ModConfigSpec.ConfigValue<?> value = VALUES.get(key);
			return value == null ? null : String.valueOf(value.get());
		});
	}

	private static ModConfig.Type type() {
		for (String candidate : new String[] {"SYNCED", "COMMON", "SERVER"}) {
			try {
				return ModConfig.Type.valueOf(candidate);
			} catch (IllegalArgumentException removedOnThisLoader) {
				// 换一个仍然存在的类型
			}
		}
		return ModConfig.Type.COMMON;
	}

	private static ModConfigSpec build() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		for (String section : ConfigSchema.sections()) {
			builder.translation(ConfigSchema.langKey(section)).push(section);
			String open = null;
			for (ConfigOption option : ConfigSchema.in(section)) {
				String parent = option.parent();
				if (!parent.equals(open)) {
					if (open != null) {
						builder.pop();
					}
					if (!parent.isEmpty()) {
						builder.translation(ConfigSchema.langKey(section + "." + parent)).push(parent);
					}
					open = parent;
				}
				VALUES.put(option.key(), define(builder, option));
			}
			if (open != null && !open.isEmpty()) {
				builder.pop();
			}
			builder.pop();
		}
		return builder.build();
	}

	private static ModConfigSpec.ConfigValue<?> define(ModConfigSpec.Builder builder, ConfigOption option) {
		var entry = builder.translation(PracticalEnchantments.MOD_ID + ".configuration." + option.key());
		return switch (option.kind()) {
			case BOOLEAN -> entry.define(option.name(), option.defaultBool());
			case INTEGER -> entry.comment("范围 " + option.intMin() + " – " + option.intMax())
					.defineInRange(option.name(), option.defaultInt(), option.intMin(), option.intMax());
			case DECIMAL -> entry.comment("范围 " + (float) option.min() + " – " + (float) option.max())
					.defineInRange(option.name(), option.defaultDouble(), option.min(), option.max());
		};
	}
}
