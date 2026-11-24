// 2.0 迁移：chinese -> flight，清掉旧 gentle_descent / undying_drop；逐区块幂等。

package com.practicalenchantments.migration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.support.VerdictTracker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.Container;

public final class PeLegacyMigration {
	private static final Identifier CHINESE = id("chinese");
	private static final Identifier GENTLE_DESCENT = id("gentle_descent");
	private static final Identifier UNDYING_DROP = id("undying_drop");
	private static final Identifier FLIGHT = id("flight");
	private static final int CHUNK_RADIUS = 8;
	private static boolean started;

	private PeLegacyMigration() {
	}

	public static void onServerStarted(MinecraftServer server) {
		if (started) {
			return;
		}
		started = true;
		for (ServerLevel level : server.getAllLevels()) {
			VerdictTracker.get(level).expire();
			migrateLoaded(level, false);
		}
	}

	public static void onPlayerJoin(ServerPlayer player) {
		if (player.level() instanceof ServerLevel level) {
			MigrationData data = data(level);
			removedThisRun = 0;
			int stacks = migratePlayer(player, level.registryAccess());
			if (stacks > 0) {
				PracticalEnchantments.info("[PE2.0] migrated {} stacks, removed {} legacy enchantments",
						stacks, removedThisRun);
				data.setDirty();
			}
		}
	}

	public static void tickPeriodic(ServerLevel level) {
		migrateLoaded(level, true);
	}

	private static void migrateLoaded(ServerLevel level, boolean periodic) {
		MigrationData data = data(level);
		HolderLookup.Provider registries = level.registryAccess();
		int stacks = 0;
		removedThisRun = 0;
		for (ServerPlayer player : level.players()) {
			stacks += migratePlayer(player, registries);
		}
		for (Entity entity : level.getAllEntities()) {
			if (entity instanceof LivingEntity living) {
				stacks += migrateLiving(living, registries);
			} else if (entity instanceof ItemEntity item) {
				stacks += migrateStack(item.getItem(), registries) ? 1 : 0;
			}
		}
		Set<Long> scanned = data.chunks;
		for (ServerPlayer player : level.players()) {
			ChunkPos center = player.chunkPosition();
			for (int dx = -CHUNK_RADIUS; dx <= CHUNK_RADIUS; dx++) {
				for (int dz = -CHUNK_RADIUS; dz <= CHUNK_RADIUS; dz++) {
					long key = key(center.x() + dx, center.z() + dz);
					if (scanned.contains(key)) {
						continue;
					}
					LevelChunk chunk = level.getChunkSource().getChunkNow(center.x() + dx, center.z() + dz);
					if (chunk == null) {
						continue;
					}
					stacks += migrateChunk(chunk, registries);
					scanned.add(key);
					data.setDirty();
				}
			}
		}
		if (stacks > 0) {
			PracticalEnchantments.info("[PE2.0] migrated {} stacks, removed {} legacy enchantments",
					stacks, removedThisRun);
		}
	}

	private static int migratePlayer(ServerPlayer player, HolderLookup.Provider registries) {
		int stacks = 0;
		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			stacks += migrateStack(stack, registries) ? 1 : 0;
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			stacks += migrateStack(player.getItemBySlot(slot), registries) ? 1 : 0;
		}
		for (int index = 0; index < player.getEnderChestInventory().getContainerSize(); index++) {
			stacks += migrateStack(player.getEnderChestInventory().getItem(index), registries) ? 1 : 0;
		}
		return stacks;
	}

	private static int migrateLiving(LivingEntity living, HolderLookup.Provider registries) {
		int stacks = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			stacks += migrateStack(living.getItemBySlot(slot), registries) ? 1 : 0;
		}
		stacks += migrateStack(living.getMainHandItem(), registries) ? 1 : 0;
		stacks += migrateStack(living.getOffhandItem(), registries) ? 1 : 0;
		return stacks;
	}

	private static int migrateChunk(LevelChunk chunk, HolderLookup.Provider registries) {
		int stacks = 0;
		for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
			if (!(blockEntity instanceof Container container)) {
				continue;
			}
			for (int index = 0; index < container.getContainerSize(); index++) {
				ItemStack stack = container.getItem(index);
				if (migrateStack(stack, registries)) {
					container.setItem(index, stack);
					stacks++;
				}
			}
		}
		return stacks;
	}

	private static int removedThisRun;

	private static boolean migrateStack(ItemStack stack, HolderLookup.Provider registries) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		boolean changed = migrateComponent(stack, DataComponents.ENCHANTMENTS, registries);
		changed |= migrateComponent(stack, DataComponents.STORED_ENCHANTMENTS, registries);
		return changed;
	}

	private static boolean migrateComponent(ItemStack stack,
											net.minecraft.core.component.DataComponentType<ItemEnchantments> type,
											HolderLookup.Provider registries) {
		ItemEnchantments current = stack.get(type);
		if (current == null || current.isEmpty()) {
			return false;
		}
		Holder<Enchantment> flight = registries.lookupOrThrow(Registries.ENCHANTMENT)
				.get(ResourceKey.create(Registries.ENCHANTMENT, FLIGHT)).orElse(null);
		int flightLevel = flight == null ? 0 : current.getLevel(flight);
		int chineseLevel = 0;
		int before = current.size();
		ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
		for (Holder<Enchantment> holder : current.keySet()) {
			if (is(holder, CHINESE)) {
				chineseLevel = Math.max(chineseLevel, current.getLevel(holder));
			}
		}
		mutable.removeIf(holder -> is(holder, CHINESE) || is(holder, GENTLE_DESCENT)
				|| is(holder, UNDYING_DROP));
		int removedCount = before - mutable.keySet().size();
		int finalLevel = Math.max(flightLevel, chineseLevel);
		if (chineseLevel > 0 && flight != null && finalLevel > 0) {
			mutable.set(flight, finalLevel);
		}
		if (removedCount <= 0 && finalLevel == flightLevel) {
			return false;
		}
		removedThisRun += removedCount;
		stack.set(type, mutable.toImmutable());
		return true;
	}

	private static boolean is(Holder<Enchantment> holder, Identifier id) {
		return holder.unwrapKey().map(key -> key.identifier().equals(id)).orElse(false);
	}

	private static MigrationData data(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(MigrationData.TYPE);
	}

	private static long key(int x, int z) {
		return ((long) x << 32) | (z & 0xFFFFFFFFL);
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, path);
	}

	public static final class MigrationData extends SavedData {
		public static final SavedDataType<MigrationData> TYPE = new SavedDataType<>(
				Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "migrated_2_0"),
				MigrationData::new,
				codec(),
				DataFixTypes.SAVED_DATA_MAP_DATA);

		private final Set<Long> chunks;

		public MigrationData() {
			this(new HashSet<>());
		}

		private MigrationData(Set<Long> chunks) {
			this.chunks = new HashSet<>(chunks);
		}

		private static Codec<MigrationData> codec() {
			return RecordCodecBuilder.create(instance -> instance.group(
					Codec.LONG.listOf().optionalFieldOf("chunks", List.of())
							.forGetter(data -> new ArrayList<>(data.chunks))
			).apply(instance, list -> new MigrationData(new HashSet<>(list))));
		}
	}
}
