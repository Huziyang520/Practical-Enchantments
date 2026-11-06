// MezzConfig 联动：装了就把全部配置登记成它的客户端配置，没装就静默跳过。

package com.practicalenchantments.fabric.config;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.ConfigOption;
import com.practicalenchantments.config.ConfigSchema;
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
					.invoke(registration, "enchantments.ini", ConfigSchema.langKey("configuration"));
			Method addBoolean = categoryType.getMethod("addBoolean", String.class, boolean.class);
			Method addInteger = categoryType.getMethod("addInteger", String.class, int.class, int.class, int.class);
			Method addDouble = categoryType.getMethod("addDouble", String.class, double.class, double.class, double.class);
			Method buildValue = Class.forName(VALUE_BUILDER).getMethod("build");
			for (String section : ConfigSchema.sections()) {
				Object category = schemaType.getMethod("addCategory", String.class).invoke(schema, section);
				for (ConfigOption option : ConfigSchema.in(section)) {
					String name = option.path();
					Object builder = switch (option.kind()) {
						case BOOLEAN -> addBoolean.invoke(category, name, Boolean.parseBoolean(current(option)));
						case INTEGER -> addInteger.invoke(category, name,
								(int) Math.round(parseDouble(current(option), option.defaultInt())),
								option.intMin(), option.intMax());
						case DECIMAL -> addDouble.invoke(category, name,
								parseDouble(current(option), option.defaultDouble()), option.min(), option.max());
					};
					Object value = buildValue.invoke(builder);
					VALUES.put(option.key(), value);
					listen(option, value);
				}
			}
			schemaType.getMethod("build").invoke(schema);
			installed = true;
			LOGGER.info("[PracticalEnchantments] 已接入 MezzConfig，配置项：{}", VALUES.size());
		} catch (Exception | LinkageError failure) {
			VALUES.clear();
			installed = false;
			LOGGER.warn("[PracticalEnchantments] MezzConfig 接入失败，沿用自带配置文件", failure);
		}
	}

	/** 把界面上的改动写回配置文件与运行时快照。 */
	public static void push(String key, String newValue) {
		Object value = VALUES.get(key);
		if (value == null) {
			return;
		}
		try {
			ConfigOption option = ConfigSchema.get(key);
			Object typed = switch (option.kind()) {
				case BOOLEAN -> Boolean.parseBoolean(newValue);
				case INTEGER -> (int) Math.round(Double.parseDouble(newValue));
				case DECIMAL -> Double.parseDouble(newValue);
			};
			Class.forName(VALUE).getMethod("set", Object.class).invoke(value, typed);
		} catch (Exception | LinkageError failure) {
			LOGGER.warn("[PracticalEnchantments] 无法写入 MezzConfig 的 {}，仅本地生效", key, failure);
		}
	}

	private static String current(ConfigOption option) {
		Object value = VALUES.get(option.key());
		if (value == null) {
			return FabricVanillaTweaks.current(option.key());
		}
		try {
			Object raw = Class.forName(VALUE).getMethod("get").invoke(value);
			return raw == null ? option.defaultValue() : raw.toString();
		} catch (Exception | LinkageError failure) {
			return FabricVanillaTweaks.current(option.key());
		}
	}

	private static double parseDouble(String text, double fallback) {
		try {
			return Double.parseDouble(text.trim());
		} catch (NumberFormatException exception) {
			return fallback;
		}
	}

	private static void listen(ConfigOption option, Object value) throws ReflectiveOperationException {
		Class<?> listenerType = Class.forName(LISTENER);
		InvocationHandler handler = (proxy, method, args) -> {
			if ("onConfigValueChanged".equals(method.getName()) && args != null && args.length == 1) {
				Object next = args[0].getClass().getMethod("newValue").invoke(args[0]);
				if (next != null) {
					FabricVanillaTweaks.save(Map.of(option.key(), next.toString()));
				}
			}
			return null;
		};
		Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[] {listenerType}, handler);
		Class.forName(VALUE).getMethod("addListener", listenerType).invoke(value, listener);
	}
}
