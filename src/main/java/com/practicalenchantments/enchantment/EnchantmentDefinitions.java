// Practical Enchantments 的 22 个附魔定义（1.20.1）。

package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.practicalenchantments.PracticalEnchantments;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class EnchantmentDefinitions {
	private static final Acquisition TABLE_AND_TRADE = new Acquisition(true, true, false, false, false, false);
	private static final Acquisition TABLE_ONLY = new Acquisition(true, false, false, false, false, false);
	private static final Acquisition TRADE_ONLY = new Acquisition(false, true, false, false, false, false);
	private static final Acquisition NEITHER = new Acquisition(false, false, false, false, false, false);

	private static final JsonArray DISGUISE_ITEMS = JsonParser.parseString(
		"[\"minecraft:skeleton_skull\",\"minecraft:wither_skeleton_skull\",\"minecraft:zombie_head\","
			+ "\"minecraft:creeper_head\",\"minecraft:piglin_head\",\"minecraft:dragon_head\","
			+ "\"minecraft:player_head\"]").getAsJsonArray();

	public static final ResourceLocation AERIAL_HASTE = id("aerial_haste");
	public static final ResourceLocation BEHEADING = id("beheading");
	public static final ResourceLocation BRIGHT = id("bright");
	public static final ResourceLocation CAT_ATTRACTION = id("cat_attraction");
	public static final ResourceLocation CAT_CHARM = id("cat_charm");
	public static final ResourceLocation CHINESE = id("chinese");
	public static final ResourceLocation DEMONIC_PACT = id("demonic_pact");
	public static final ResourceLocation DESTRUCTION = id("destruction");
	public static final ResourceLocation DISGUISE = id("disguise");
	public static final ResourceLocation EXECUTE = id("execute");
	public static final ResourceLocation FRENZY = id("frenzy");
	public static final ResourceLocation GENTLE_DESCENT = id("gentle_descent");
	public static final ResourceLocation INCINERATE = id("incinerate");
	public static final ResourceLocation LIFE_STEAL = id("life_steal");
	public static final ResourceLocation LUMBERJACK = id("lumberjack");
	public static final ResourceLocation PENETRATION = id("penetration");
	public static final ResourceLocation POWERFUL = id("powerful");
	public static final ResourceLocation SMASHING = id("smashing");
	public static final ResourceLocation SOUL_SIPHON = id("soul_siphon");
	public static final ResourceLocation UNDYING_DROP = id("undying_drop");
	public static final ResourceLocation VENOM = id("venom");
	public static final ResourceLocation WITHER_ASPECT = id("wither_aspect");

	private EnchantmentDefinitions() {
	}

	public static void registerAll() {
		aerialHaste();
		beheading();
		bright();
		catAttraction();
		catCharm();
		chinese();
		demonicPact();
		destruction();
		disguise();
		execute();
		frenzy();
		gentleDescent();
		incinerate();
		lifeSteal();
		lumberjack();
		penetration();
		powerful();
		smashing();
		soulSiphon();
		undyingDrop();
		venom();
		witherAspect();
	}

	private static void aerialHaste() {
		MerlinApi.enchantments()
			.register(AERIAL_HASTE)
			.translationKey(key("aerial_haste"))
			.weight(4)
			.maxLevel(1)
			.cost(8, 0, 25, 0)
			.anvilCost(2)
			.slots("head")
			.supportedItems("#minecraft:enchantable/head_armor")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.ARMOR_HEAD)
			.submit();
	}

	private static void beheading() {
		MerlinApi.enchantments()
			.register(BEHEADING)
			.translationKey(key("beheading"))
			.weight(1)
			.maxLevel(3)
			.cost(0, 0, 0, 0)
			.anvilCost(4)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static void bright() {
		MerlinApi.enchantments()
			.register(BRIGHT)
			.translationKey(key("bright"))
			.weight(10)
			.maxLevel(1)
			.cost(10, 0, 25, 0)
			.anvilCost(2)
			.slots("head")
			.supportedItems("#minecraft:enchantable/head_armor")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.ARMOR_HEAD)
			.submit();
	}

	private static void catAttraction() {
		MerlinApi.enchantments()
			.register(CAT_ATTRACTION)
			.translationKey(key("cat_attraction"))
			.weight(1)
			.maxLevel(1)
			.cost(8, 0, 28, 0)
			.anvilCost(2)
			.slots("legs")
			.supportedItems("#minecraft:enchantable/leg_armor")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static void catCharm() {
		MerlinApi.enchantments()
			.register(CAT_CHARM)
			.translationKey(key("cat_charm"))
			.weight(1)
			.maxLevel(1)
			.cost(12, 0, 35, 0)
			.anvilCost(3)
			.slots("head")
			.supportedItems("#minecraft:enchantable/head_armor")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static void chinese() {
		MerlinApi.enchantments()
			.register(CHINESE)
			.translationKey(key("chinese"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(0)
			.slots("chest")
			.supportedItems("#minecraft:enchantable/chest_armor")
			.acquisition(NEITHER)
			.submit();
	}

	private static void demonicPact() {
		MerlinApi.enchantments()
			.register(DEMONIC_PACT)
			.translationKey(key("demonic_pact"))
			.weight(1)
			.maxLevel(3)
			.cost(0, 0, 0, 0)
			.anvilCost(6)
			.slots("chest")
			.supportedItems("#minecraft:enchantable/chest_armor")
			.exclusiveSet(ExclusiveSets.DEATH_SAVE.deepCopy())
			.acquisition(NEITHER)
			.submit();
	}

	private static void destruction() {
		MerlinApi.enchantments()
			.register(DESTRUCTION)
			.translationKey(key("destruction"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(0)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/mining")
			.exclusiveSet(ExclusiveSets.MINING.deepCopy())
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static void disguise() {
		MerlinApi.enchantments()
			.register(DISGUISE)
			.translationKey(key("disguise"))
			.weight(1)
			.maxLevel(1)
			.cost(12, 0, 35, 0)
			.anvilCost(3)
			.slots("head")
			.supportedItems(DISGUISE_ITEMS)
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static void execute() {
		MerlinApi.enchantments()
			.register(EXECUTE)
			.translationKey(key("execute"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(5)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(NEITHER)
			.submit();
	}

	private static void frenzy() {
		MerlinApi.enchantments()
			.register(FRENZY)
			.translationKey(key("frenzy"))
			.weight(1)
			.maxLevel(2)
			.cost(0, 0, 0, 0)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static void gentleDescent() {
		MerlinApi.enchantments()
			.register(GENTLE_DESCENT)
			.translationKey(key("gentle_descent"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(2)
			.slots("feet")
			.supportedItems("#minecraft:enchantable/foot_armor")
			.exclusiveSet(ExclusiveSets.SLOW_FALL.deepCopy())
			.acquisition(NEITHER)
			.submit();
	}

	private static void incinerate() {
		MerlinApi.enchantments()
			.register(INCINERATE)
			.translationKey(key("incinerate"))
			.weight(1)
			.maxLevel(1)
			.cost(0, 0, 0, 0)
			.anvilCost(0)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.exclusiveSet(ExclusiveSets.LOOTING.deepCopy())
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static void lifeSteal() {
		MerlinApi.enchantments()
			.register(LIFE_STEAL)
			.translationKey(key("life_steal"))
			.weight(8)
			.maxLevel(5)
			.cost(4, 8, 24, 8)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.WEAPON)
			.submit();
	}

	private static void lumberjack() {
		MerlinApi.enchantments()
			.register(LUMBERJACK)
			.translationKey(key("lumberjack"))
			.weight(4)
			.maxLevel(1)
			.cost(8, 0, 25, 0)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("#minecraft:axes")
			.exclusiveSet(ExclusiveSets.MINING.deepCopy())
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.DIGGER)
			.submit();
	}

	private static void penetration() {
		MerlinApi.enchantments()
			.register(PENETRATION)
			.translationKey(key("penetration"))
			.weight(6)
			.maxLevel(5)
			.cost(6, 8, 28, 8)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/trident")
			.exclusiveSet(ExclusiveSets.TRIDENT_RIPTIDE.deepCopy())
			.acquisition(TABLE_ONLY)
			.category(EnchantmentCategory.TRIDENT)
			.submit();
	}

	private static void powerful() {
		MerlinApi.enchantments()
			.register(POWERFUL)
			.translationKey(key("powerful"))
			.weight(10)
			.maxLevel(5)
			.cost(1, 10, 15, 10)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("minecraft:crossbow")
			.exclusiveSet(ExclusiveSets.CROSSBOW.deepCopy())
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.CROSSBOW)
			.submit();
	}

	private static void smashing() {
		MerlinApi.enchantments()
			.register(SMASHING)
			.translationKey(key("smashing"))
			.weight(6)
			.maxLevel(5)
			.cost(5, 8, 28, 8)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/trident")
			.acquisition(TABLE_ONLY)
			.category(EnchantmentCategory.TRIDENT)
			.submit();
	}

	private static void soulSiphon() {
		MerlinApi.enchantments()
			.register(SOUL_SIPHON)
			.translationKey(key("soul_siphon"))
			.weight(7)
			.maxLevel(5)
			.cost(5, 7, 25, 7)
			.anvilCost(2)
			.slots("mainhand")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.WEAPON)
			.submit();
	}

	private static void undyingDrop() {
		MerlinApi.enchantments()
			.register(UNDYING_DROP)
			.translationKey(key("undying_drop"))
			.weight(1)
			.maxLevel(3)
			.cost(0, 0, 0, 0)
			.anvilCost(3)
			.slots("head", "chest", "legs", "feet")
			.supportedItems("#minecraft:enchantable/armor")
			.exclusiveSet(ExclusiveSets.DEATH_SAVE.deepCopy())
			.acquisition(NEITHER)
			.submit();
	}

	private static void venom() {
		MerlinApi.enchantments()
			.register(VENOM)
			.translationKey(key("venom"))
			.weight(6)
			.maxLevel(2)
			.cost(8, 8, 28, 8)
			.anvilCost(3)
			.slots("mainhand")
			.supportedItems("#minecraft:swords")
			.exclusiveSet(ExclusiveSets.BLADE_EFFECT.deepCopy())
			.acquisition(TABLE_AND_TRADE)
			.category(EnchantmentCategory.WEAPON)
			.submit();
	}

	private static void witherAspect() {
		MerlinApi.enchantments()
			.register(WITHER_ASPECT)
			.translationKey(key("wither_aspect"))
			.weight(1)
			.maxLevel(2)
			.cost(0, 0, 0, 0)
			.anvilCost(4)
			.slots("mainhand")
			.supportedItems("#minecraft:swords")
			.exclusiveSet(ExclusiveSets.BLADE_EFFECT.deepCopy())
			.acquisition(TRADE_ONLY)
			.submit();
	}

	private static String key(String path) {
		return "enchantment." + PracticalEnchantments.MOD_ID + "." + path;
	}

	private static ResourceLocation id(String path) {
		return new ResourceLocation(PracticalEnchantments.MOD_ID, path);
	}
}
