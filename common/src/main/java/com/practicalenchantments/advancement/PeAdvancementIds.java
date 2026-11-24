// 进度与自定义条件 id 的唯一来源，JSON 里的字符串必须与这里逐字一致。

package com.practicalenchantments.advancement;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.resources.Identifier;

public final class PeAdvancementIds {
	public static final Identifier ROOT = id("root");
	public static final Identifier UTTER_DESTRUCTION = id("utter_destruction");
	public static final Identifier CLEARED_FOR_TAKEOFF = id("cleared_for_takeoff");
	public static final Identifier GET_OVER_HERE = id("get_over_here");
	public static final Identifier ANTI_MATERIEL = id("anti_materiel");
	public static final Identifier EXPELLIARMUS = id("expelliarmus");
	public static final Identifier BUILT_DIFFERENT = id("built_different");
	public static final Identifier BIOHAZARD = id("biohazard");
	public static final Identifier ONE_SHOT_ONE_KILL = id("one_shot_one_kill");
	public static final Identifier NOW_THATS_SMITE = id("now_thats_smite");
	public static final Identifier WHOS_THE_BOSS_NOW = id("whos_the_boss_now");

	public static final Identifier C_ENCHANTED_ITEM = id("enchanted_item_obtained");
	public static final Identifier C_INCINERATE_KILL = id("incinerate_kill");
	public static final Identifier C_SURVIVAL_FLIGHT = id("survival_flight");
	public static final Identifier C_TRIDENT_PULL = id("trident_pull");
	public static final Identifier C_POWERFUL_CROSSBOW_HIT = id("powerful_crossbow_hit");
	public static final Identifier C_DISARM_DROP = id("disarm_drop");
	public static final Identifier C_ROT_100 = id("rot_100");
	public static final Identifier C_MUTUAL_DESTRUCTION_VILLAGER = id("mutual_destruction_villager");
	public static final Identifier C_BIG_BOW_HIT = id("big_bow_hit");
	public static final Identifier C_HEALING_ARROW_UNDEAD = id("healing_arrow_undead");
	public static final Identifier C_INTIMIDATE_IRON_GOLEM = id("intimidate_iron_golem");

	private PeAdvancementIds() {
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, path);
	}
}
