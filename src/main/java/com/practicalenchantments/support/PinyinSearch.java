// 可选联动「通用拼音搜索」：装了它就按拼音/首字母匹配，没装退回普通包含判断。

package com.practicalenchantments.support;

import java.lang.reflect.Method;
import java.util.Locale;

public final class PinyinSearch {
	private static final String TARGET = "me.towdium.jecharacters.utils.Match";
	private static Method contains;
	private static boolean resolved;

	private PinyinSearch() {
	}

	public static boolean matches(String text, String needle) {
		if (needle.isEmpty()) {
			return true;
		}
		Method method = containsMethod();
		if (method != null) {
			try {
				Object result = method.invoke(null, text, needle);
				if (result instanceof Boolean matched) {
					return matched;
				}
			} catch (ReflectiveOperationException | RuntimeException ignored) {
			}
		}
		return text.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
	}

	private static synchronized Method containsMethod() {
		if (!resolved) {
			resolved = true;
			try {
				contains = Class.forName(TARGET).getMethod("contains", CharSequence.class, CharSequence.class);
			} catch (ReflectiveOperationException | LinkageError ignored) {
				contains = null;
			}
		}
		return contains;
	}
}
