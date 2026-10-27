// MezzConfig 联动：装了就把三项原版调整登记成 MezzConfig 的客户端配置，没装就静默跳过。

package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.VanillaTweaks;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MezzConfigBridge {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final String CONFIGS = "net.mezzdev.config.api.Configs";
	private static final String LISTENER = "net.mezzdev.config.api.value.change.IConfigValueChangeListener";
	private static final Map<String, Object> VALUES = new LinkedHashMap<>();
	private static boolean installed;

	private MezzConfigBridge() {
	}

	public static boolean installed() {
		return installed;
	}

	public static void install() {
		if (installed) {
			return;
		}
		Class<?> configs;
		try {
			configs = Class.forName(CONFIGS);
		} catch (ClassNotFoundException absent) {
			return;
		}
		try {
			Object registration = configs.getMethod("forMod", String.class)
					.invoke(null, PracticalEnchantments.MOD_ID);
			Object schema = registration.getClass()
					.getMethod("createClientSchemaBuilder", String.class, String.class)
					.invoke(registration, "tweaks.ini", VanillaTweaks.SECTION_LANG_KEY);
			Object category = schema.getClass().getMethod("addCategory", String.class)
					.invoke(schema, VanillaTweaks.SECTION_KEY);
			Method addBoolean = category.getClass().getMethod("addBoolean", String.class, boolean.class);
			for (String key : FabricVanillaTweaks.KEYS) {
				Object builder = addBoolean.invoke(category, key, VanillaTweaks.defaultValue(key));
				Object value = builder.getClass().getMethod("build").invoke(builder);
				VALUES.put(key, value);
				listen(key, value);
			}
			schema.getClass().getMethod("build").invoke(schema);
			installed = true;
			applyAll();
			LOGGER.info("[PracticalEnchantments] 已接入 MezzConfig，配置项：{}", VALUES.keySet());
		} catch (Exception | LinkageError failure) {
			VALUES.clear();
			installed = false;
			LOGGER.warn("[PracticalEnchantments] MezzConfig 接入失败，沿用自带配置文件", failure);
		}
	}

	public static boolean value(String key) {
		Object value = VALUES.get(key);
		if (value == null) {
			return VanillaTweaks.value(key);
		}
		try {
			Object current = value.getClass().getMethod("get").invoke(value);
			return current instanceof Boolean enabled ? enabled : VanillaTweaks.value(key);
		} catch (Exception failure) {
			return VanillaTweaks.value(key);
		}
	}

	public static void push(String key, boolean newValue) {
		Object value = VALUES.get(key);
		if (value == null) {
			return;
		}
		try {
			value.getClass().getMethod("set", Object.class).invoke(value, newValue);
		} catch (Exception failure) {
			LOGGER.warn("[PracticalEnchantments] 无法写入 MezzConfig 的 {}，仅本地生效", key, failure);
		}
	}

	private static void applyAll() {
		VanillaTweaks.apply(
				value(VanillaTweaks.KEY_INFINITY_CROSSBOW),
				value(VanillaTweaks.KEY_INFINITY_NO_ARROW),
				value(VanillaTweaks.KEY_INFINITY_SPECTRAL));
	}

	private static void listen(String key, Object value) {
		try {
			Class<?> listenerType = Class.forName(LISTENER);
			Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[] {listenerType},
					(proxy, method, args) -> {
						if ("onConfigValueChanged".equals(method.getName()) && args != null && args.length == 1) {
							Object next = args[0].getClass().getMethod("newValue").invoke(args[0]);
							if (next instanceof Boolean enabled) {
								VanillaTweaks.apply(key, enabled);
							}
						}
						return null;
					});
			value.getClass().getMethod("addListener", listenerType).invoke(value, listener);
		} catch (Exception | LinkageError failure) {
			LOGGER.warn("[PracticalEnchantments] MezzConfig 的 {} 未接上变更监听", key, failure);
		}
	}
}
