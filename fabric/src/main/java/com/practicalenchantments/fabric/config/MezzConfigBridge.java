// MezzConfig 联动：装了就把三项原版调整登记成 MezzConfig 的客户端配置，没装就静默跳过。

package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.VanillaTweaks;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MezzConfigBridge {
	private static final Logger LOGGER = LoggerFactory.getLogger(PracticalEnchantments.MOD_ID);
	private static final String CONFIGS = "net.mezzdev.config.api.Configs";
	private static final String REGISTRATION = "net.mezzdev.config.api.IConfigRegistration";
	private static final String SCHEMA = "net.mezzdev.config.api.schema.builder.IConfigSchemaBuilder";
	private static final String CATEGORY = "net.mezzdev.config.api.schema.builder.IConfigCategoryBuilder";
	private static final String VALUE_BUILDER = "net.mezzdev.config.api.value.builder.IConfigValueBuilder";
	private static final String VALUE = "net.mezzdev.config.api.value.IConfigValue";
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
		try {
			Class<?> configs = Class.forName(CONFIGS);
			Class<?> registrationType = Class.forName(REGISTRATION);
			Class<?> schemaType = Class.forName(SCHEMA);
			Class<?> categoryType = Class.forName(CATEGORY);
			Object registration = configs.getMethod("forMod", String.class).invoke(null, PracticalEnchantments.MOD_ID);
			Object schema = registrationType.getMethod("createClientSchemaBuilder", String.class, String.class)
					.invoke(registration, "tweaks.ini", VanillaTweaks.SECTION_LANG_KEY);
			Object category = schemaType.getMethod("addCategory", String.class)
					.invoke(schema, VanillaTweaks.SECTION_KEY);
			Method addBoolean = categoryType.getMethod("addBoolean", String.class, boolean.class);
			Method buildValue = Class.forName(VALUE_BUILDER).getMethod("build");
			for (String key : FabricVanillaTweaks.KEYS) {
				Object builder = addBoolean.invoke(category, key, VanillaTweaks.defaultValue(key));
				Object value = buildValue.invoke(builder);
				VALUES.put(key, value);
				listen(key, value);
			}
			schemaType.getMethod("build").invoke(schema);
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
			Object current = Class.forName(VALUE).getMethod("get").invoke(value);
			return current instanceof Boolean enabled ? enabled : VanillaTweaks.value(key);
		} catch (Exception | LinkageError failure) {
			return VanillaTweaks.value(key);
		}
	}

	public static void push(String key, boolean newValue) {
		Object value = VALUES.get(key);
		if (value == null) {
			return;
		}
		try {
			Class.forName(VALUE).getMethod("set", Object.class).invoke(value, newValue);
		} catch (Exception | LinkageError failure) {
			LOGGER.warn("[PracticalEnchantments] 无法写入 MezzConfig 的 {}，仅本地生效", key, failure);
		}
	}

	private static void applyAll() {
		VanillaTweaks.apply(
				value(VanillaTweaks.KEY_INFINITY_CROSSBOW),
				value(VanillaTweaks.KEY_INFINITY_NO_ARROW),
				value(VanillaTweaks.KEY_INFINITY_SPECTRAL));
	}

	private static void listen(String key, Object value) throws ReflectiveOperationException {
		Class<?> listenerType = Class.forName(LISTENER);
		InvocationHandler handler = (proxy, method, args) -> {
			if ("onConfigValueChanged".equals(method.getName()) && args != null && args.length == 1) {
				Object next = args[0].getClass().getMethod("newValue").invoke(args[0]);
				if (next instanceof Boolean enabled) {
					VanillaTweaks.apply(key, enabled);
				}
			}
			return null;
		};
		Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[] {listenerType}, handler);
		Class.forName(VALUE).getMethod("addListener", listenerType).invoke(value, listener);
	}
}
