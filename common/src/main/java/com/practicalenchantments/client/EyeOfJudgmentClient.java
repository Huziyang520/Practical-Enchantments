// 审判之眼客户端：只在本机给范围内的生物打白色发光描边，不写服务端实体数据。

package com.practicalenchantments.client;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.content.AmuletSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EyeOfJudgmentClient {
	private static final Identifier ID = Identifier.fromNamespaceAndPath(
			PracticalEnchantments.MOD_ID, "eye_of_judgment");
	private static final Map<UUID, Entity> TRACKED = new HashMap<>();
	private static final Map<UUID, Boolean> ORIGINAL = new HashMap<>();
	private static final EquipmentSlot[] WORN_SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND};
	private static ClientLevel lastLevel;
	private static int counter;

	private EyeOfJudgmentClient() {
	}

	public static void tick() {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		ClientLevel level = minecraft.level;
		if (player == null || level == null) {
			return;
		}
		if (level != lastLevel) {
			clearAll();
			lastLevel = level;
		}
		counter++;
		if (counter % Math.max(1, Tunables.eyeOfJudgmentScanIntervalTicks()) == 0) {
			rescan(player, level);
		}
		// 服务端每 tick 都可能重发实体共享标记；客户端每 tick 重新盖上白色描边。
		for (Entity entity : TRACKED.values()) {
			if (entity != null && entity.isAlive() && entity.level() == level) {
				entity.setGlowingTag(true);
			}
		}
	}

	private static void rescan(Player player, ClientLevel level) {
		int enchantLevel = level(player);
		if (enchantLevel <= 0) {
			clearAll();
			return;
		}
		int radius = Tunables.eyeOfJudgmentRadius(enchantLevel);
		List<LivingEntity> found = new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class,
				player.getBoundingBox().inflate(radius),
				entity -> entity != player && entity.isAlive()
						&& entity.distanceToSqr(player) <= (double) radius * radius));
		if (found.size() > Tunables.eyeOfJudgmentMaxTargets()) {
			found = found.subList(0, Tunables.eyeOfJudgmentMaxTargets());
		}
		Map<UUID, Entity> keep = new HashMap<>();
		for (LivingEntity entity : found) {
			UUID id = entity.getUUID();
			keep.put(id, entity);
			if (!TRACKED.containsKey(id)) {
				ORIGINAL.put(id, entity.isCurrentlyGlowing());
			}
		}
		for (Map.Entry<UUID, Entity> entry : TRACKED.entrySet()) {
			if (keep.containsKey(entry.getKey())) {
				continue;
			}
			restore(entry.getKey(), entry.getValue());
		}
		TRACKED.clear();
		TRACKED.putAll(keep);
	}

	private static int level(Player player) {
		int best = 0;
		for (EquipmentSlot slot : WORN_SLOTS) {
			best = Math.max(best, level(player.getItemBySlot(slot)));
		}
		for (ItemStack stack : AmuletSupport.equippedAmuletStacks(player)) {
			best = Math.max(best, level(stack));
		}
		return best;
	}

	private static int level(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		ItemEnchantments enchantments = stack.getEnchantments();
		for (var entry : enchantments.entrySet()) {
			if (entry.getKey().unwrapKey().map(key -> key.identifier().equals(ID)).orElse(false)) {
				return entry.getIntValue();
			}
		}
		return 0;
	}

	private static void restore(UUID id, Entity entity) {
		Boolean original = ORIGINAL.remove(id);
		if (entity != null && entity.isAlive() && !Boolean.TRUE.equals(original)) {
			entity.setGlowingTag(false);
		}
	}

	public static void clearAll() {
		for (Map.Entry<UUID, Entity> entry : TRACKED.entrySet()) {
			restore(entry.getKey(), entry.getValue());
		}
		TRACKED.clear();
		ORIGINAL.clear();
	}
}
