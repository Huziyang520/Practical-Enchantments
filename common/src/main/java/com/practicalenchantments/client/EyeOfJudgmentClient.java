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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EyeOfJudgmentClient {
	private static final Identifier ID = Identifier.fromNamespaceAndPath(
			PracticalEnchantments.MOD_ID, "eye_of_judgment");
	private static final Map<UUID, Entity> TRACKED = new HashMap<>();
	private static final Map<UUID, Boolean> ORIGINAL = new HashMap<>();
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
		if (counter % Math.max(1, Tunables.eyeOfJudgmentScanIntervalTicks()) != 0) {
			return;
		}
		int enchantLevel = level(player, level);
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
				ORIGINAL.put(id, entity.hasGlowingTag());
			}
			entity.setGlowingTag(true);
		}
		for (Map.Entry<UUID, Entity> entry : TRACKED.entrySet()) {
			if (keep.containsKey(entry.getKey())) {
				continue;
			}
			Entity entity = entry.getValue();
			if (entity != null && entity.isAlive() && !Boolean.TRUE.equals(ORIGINAL.get(entry.getKey()))) {
				entity.setGlowingTag(false);
			}
			ORIGINAL.remove(entry.getKey());
		}
		TRACKED.clear();
		TRACKED.putAll(keep);
	}

	private static int level(Player player, ClientLevel level) {
		Holder<Enchantment> holder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
				.get(ResourceKey.create(Registries.ENCHANTMENT, ID)).orElse(null);
		return holder == null ? 0 : AmuletSupport.wornLevel(player, holder);
	}

	public static void clearAll() {
		for (Map.Entry<UUID, Entity> entry : TRACKED.entrySet()) {
			Entity entity = entry.getValue();
			if (entity != null && entity.isAlive() && !Boolean.TRUE.equals(ORIGINAL.get(entry.getKey()))) {
				entity.setGlowingTag(false);
			}
		}
		TRACKED.clear();
		ORIGINAL.clear();
	}
}
