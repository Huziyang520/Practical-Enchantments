// 进度触发点：所有附魔代码只调用这里，不直接拼 id。

package com.practicalenchantments.advancement;

import com.huziyang520.merlinlib.api.AdvancementApi;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class PeAdvancementTriggers {
	public static final int BIG_BOW_DAMAGE = 60;
	public static final int ROT_LAYER_TARGET = 100;

	// 腐毒层数按施加者累计，不按单个目标当前层数（层会随时间掉光）。
	private static final Map<UUID, Integer> ROT_APPLIED = new ConcurrentHashMap<>();

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

	public static void rotApplied(ServerPlayer player, int layers) {
		if (layers <= 0) {
			return;
		}
		int total = ROT_APPLIED.merge(player.getUUID(), layers, Integer::sum);
		if (total < ROT_LAYER_TARGET) {
			return;
		}
		ROT_APPLIED.remove(player.getUUID());
		AdvancementApi.trigger(player, PeAdvancementIds.C_ROT_100, total);
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
