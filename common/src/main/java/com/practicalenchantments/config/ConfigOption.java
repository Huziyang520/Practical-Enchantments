// 一条配置项的定义：类型、默认值与取值范围。界面的读写全部以本表为准。

package com.practicalenchantments.config;

public record ConfigOption(String section, String path, Kind kind, String defaultValue,
		int intMin, int intMax, double min, double max) {
	public enum Kind {
		BOOLEAN, INTEGER, DECIMAL
	}

	public static ConfigOption bool(String section, String path, boolean defaultValue) {
		return new ConfigOption(section, path, Kind.BOOLEAN, Boolean.toString(defaultValue), 0, 0, 0.0D, 0.0D);
	}

	public static ConfigOption integer(String section, String path, int defaultValue, int min, int max) {
		return new ConfigOption(section, path, Kind.INTEGER, Integer.toString(defaultValue), min, max, min, max);
	}

	public static ConfigOption decimal(String section, String path, double defaultValue, double min, double max) {
		return new ConfigOption(section, path, Kind.DECIMAL, Double.toString(defaultValue), 0, 0, min, max);
	}

	public String key() {
		return section + "." + path;
	}

	public String name() {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	public String parent() {
		int cut = path.lastIndexOf('.');
		return cut < 0 ? "" : path.substring(0, cut);
	}

	public boolean defaultBool() {
		return Boolean.parseBoolean(defaultValue);
	}

	public int defaultInt() {
		return (int) Math.round(Double.parseDouble(defaultValue));
	}

	public double defaultDouble() {
		return Double.parseDouble(defaultValue);
	}
}
