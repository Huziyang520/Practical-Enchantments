package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.EnchantmentEventRegistrar;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 静默契约附魔(silent_pact)。
 *
 * <p>胸甲 II 级,压制自然回血 + 击杀回血。</p>
 */
public final class SilentPactEnchantment {

	public static final String SILENT_PACT_ID = MOD_ID + ":silent_pact";

	private static Holder<Enchantment> silentPact;

	private SilentPactEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(SILENT_PACT_ID)
			.description("Silent Pact")
			.supportedItems("#minecraft:enchantable/chest_armor")
			.weight(2).maxLevel(2)
			.minCost(10, 10).maxCost(40, 10).anvilCost(6)
			.slots("chest"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		silentPact = resolveEnchantment(registries, SILENT_PACT_ID);

		EnchantLibEvents.FOOD_REGEN.register(SilentPactEnchantment::handleSilentPactRegen);
		ServerLivingEntityEvents.AFTER_DEATH.register(SilentPactEnchantment::handleSilentPactKill);
	}

	/**
	 * 静默契约自然回血压制:玩家胸甲持 silent_pact 时取消自然回血。
	 *
	 * <p>仅取消 FoodData.tick 的 heal 调用,药水治疗、金苹果等不受影响。</p>
	 */
	private static void handleSilentPactRegen(com.enchantlib.event.FoodRegenEvent event) {
		ServerPlayer player = event.player();
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chest.getEnchantments().getLevel(silentPact) > 0) {
			event.setCancelled(true);
		}
	}

	/**
	 * 静默契约击杀回血:玩家击杀实体时,若胸甲持 silent_pact,回 2*level HP。
	 *
	 * <p>用全局 ServerLivingEntityEvents.AFTER_DEATH 而非 POST_KILL,
	 * 因为 POST_KILL 扫描 ATTACK_SLOTS(不扫胸甲),而 silent_pact 是胸甲附魔。</p>
	 */
	private static void handleSilentPactKill(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
		if (!(source.getEntity() instanceof ServerPlayer killer)) {
			return;
		}
		ItemStack chest = killer.getItemBySlot(EquipmentSlot.CHEST);
		int level = chest.getEnchantments().getLevel(silentPact);
		if (level > 0) {
			killer.heal(2.0F * level);
		}
	}
}
