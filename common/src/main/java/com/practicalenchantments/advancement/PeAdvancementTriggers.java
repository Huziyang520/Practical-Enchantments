// 进度触发点：所有附魔代码只调用这里，不直接拼 id。

package com.practicalenchantments.advancement;

import com.huziyang520.merlinlib.api.AdvancementApi;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class PeAdvancementTriggers {
	public static final int BIG_BOW_DAMAGE = 30;
	public static final double BIG_BOW_MIN_DISTANCE = 50.0D;
	public static final int ROT_LAYER_TARGET = 100;

	private PeAdvancementTriggers() {
	}

	public static void onPlayerJoin(ServerPlayer player) {
		scanEnchantedItem(player);
	}

	public static void scan(ServerLevel level) {
		for (ServerPlayer player : level.players()) {
			scanEnchantedItem(player);
		}
	}

	public static void scanEnchantedItem(ServerPlayer player) {
		if (!holdsEnchantedItem(player)) {
			return;
		}
		AdvancementApi.trigger(player, PeAdvancementIds.C_ENCHANTED_ITEM);
		AdvancementApi.award(player, PeAdvancementIds.ROOT);
	}

	public static void incinerateKill(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_INCINERATE_KILL);
	}

	public static void survivalFlight(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_SURVIVAL_FLIGHT);
	}

	public static void tridentPull(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_TRIDENT_PULL);
	}

	public static void powerfulCrossbowHit(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_POWERFUL_CROSSBOW_HIT);
	}

	public static void disarmDrop(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_DISARM_DROP);
	}

	public static void rotApplied(ServerPlayer player, int totalLayers) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_ROT_100, totalLayers);
	}

	public static void mutualDestructionVillager(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_MUTUAL_DESTRUCTION_VILLAGER);
	}

	public static void bigBowHit(ServerPlayer player, float damage) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_BIG_BOW_HIT, (int) Math.ceil(damage));
	}

	public static void healingArrowUndead(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_HEALING_ARROW_UNDEAD);
	}

	public static void intimidateIronGolem(ServerPlayer player) {
		AdvancementApi.trigger(player, PeAdvancementIds.C_INTIMIDATE_IRON_GOLEM);
	}

	private static boolean holdsEnchantedItem(ServerPlayer player) {
		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			if (hasEnchantment(stack)) {
				return true;
			}
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (hasEnchantment(player.getItemBySlot(slot))) {
				return true;
			}
		}
		for (int index = 0; index < player.getEnderChestInventory().getContainerSize(); index++) {
			if (hasEnchantment(player.getEnderChestInventory().getItem(index))) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasEnchantment(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
		if (stored != null && !stored.isEmpty()) {
			return true;
		}
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		return enchantments != null && !enchantments.isEmpty();
	}

	public static List<ServerPlayer> players(ServerLevel level) {
		return level.players();
	}
}
