package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.EntityCategory;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.LivingEntityTickEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * 伪装系列附魔(4合一)。
 *
 * <p>包含亡灵伪装、深海伪装、灾厄伪装、节肢伪装 4 个头盔附魔,
 * 穿戴对应头盔时玩家被分类为对应生物类别(亡灵/水生/灾厄/节肢),
 * 让对应怪物不主动攻击玩家。4 个附魔同属 disguise 互斥组。</p>
 *
 * <p>伪装系列全局 tick 回调:每 tick 检查所有 ServerPlayer 的头盔附魔,
 * 根据当前头盔设置/清除对应的 EntityCategory。
 * 玩家同时最多戴1个头盔(原版槽位限制),因此4个伪装附魔天然互斥,
 * 不戴伪装头盔时清除分类。</p>
 */
public final class DisguiseEnchantments {

	public static final String UNDEAD_DISGUISE_ID = MOD_ID + ":undead_disguise";
	public static final String AQUATIC_DISGUISE_ID = MOD_ID + ":aquatic_disguise";
	public static final String ILLAGER_DISGUISE_ID = MOD_ID + ":illager_disguise";
	public static final String ARTHROPOD_DISGUISE_ID = MOD_ID + ":arthropod_disguise";

	/** 伪装互斥组:4 个伪装附魔互相排斥(铁砧合并也拒绝共存) */
	public static final String DISGUISE_GROUP_REF = "#" + MOD_ID + ":exclusive_set/disguise";

	private static Holder<Enchantment> undeadDisguise;
	private static Holder<Enchantment> aquaticDisguise;
	private static Holder<Enchantment> illagerDisguise;
	private static Holder<Enchantment> arthropodDisguise;

	private DisguiseEnchantments() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(UNDEAD_DISGUISE_ID)
			.description("Undead Disguise")
			.supportedItems("#minecraft:enchantable/head_armor")
			.weight(5).maxLevel(1)
			.minCost(5, 8).maxCost(20, 8).anvilCost(2)
			.exclusiveSet(DISGUISE_GROUP_REF)
			.slots("head"));

		registrar.register(EnchantmentBuilder.create(AQUATIC_DISGUISE_ID)
			.description("Aquatic Disguise")
			.supportedItems("#minecraft:enchantable/head_armor")
			.weight(5).maxLevel(1)
			.minCost(5, 8).maxCost(20, 8).anvilCost(2)
			.exclusiveSet(DISGUISE_GROUP_REF)
			.slots("head"));

		registrar.register(EnchantmentBuilder.create(ILLAGER_DISGUISE_ID)
			.description("Illager Disguise")
			.supportedItems("#minecraft:enchantable/head_armor")
			.weight(5).maxLevel(1)
			.minCost(5, 8).maxCost(20, 8).anvilCost(2)
			.exclusiveSet(DISGUISE_GROUP_REF)
			.slots("head"));

		registrar.register(EnchantmentBuilder.create(ARTHROPOD_DISGUISE_ID)
			.description("Arthropod Disguise")
			.supportedItems("#minecraft:enchantable/head_armor")
			.weight(5).maxLevel(1)
			.minCost(5, 8).maxCost(20, 8).anvilCost(2)
			.exclusiveSet(DISGUISE_GROUP_REF)
			.slots("head"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		undeadDisguise = resolveEnchantment(registries, UNDEAD_DISGUISE_ID);
		aquaticDisguise = resolveEnchantment(registries, AQUATIC_DISGUISE_ID);
		illagerDisguise = resolveEnchantment(registries, ILLAGER_DISGUISE_ID);
		arthropodDisguise = resolveEnchantment(registries, ARTHROPOD_DISGUISE_ID);

		EnchantLibEvents.LIVING_ENTITY_TICK.register(DisguiseEnchantments::handleDisguiseTick);
	}

	private static void handleDisguiseTick(LivingEntityTickEvent event) {
		if (!(event.entity() instanceof ServerPlayer player)) {
			return;
		}
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		ItemEnchantments enchants = helmet.getEnchantments();

		if (enchants.getLevel(undeadDisguise) > 0) {
			EntityCategory.set(player, EntityCategory.Category.UNDEAD);
		} else if (enchants.getLevel(aquaticDisguise) > 0) {
			EntityCategory.set(player, EntityCategory.Category.AQUATIC);
		} else if (enchants.getLevel(illagerDisguise) > 0) {
			EntityCategory.set(player, EntityCategory.Category.ILLAGER);
		} else if (enchants.getLevel(arthropodDisguise) > 0) {
			EntityCategory.set(player, EntityCategory.Category.ARTHROPOD);
		} else {
			EntityCategory.clear(player);
		}
	}
}
