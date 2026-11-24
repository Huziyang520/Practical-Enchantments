// 落矢：箭命中方块或实体后在落点上方排队降箭；自身命中不再触发。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.enchantment.support.ArrowfallQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class ArrowfallEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":arrowfall";
	private static Holder<Enchantment> HOLDER;

	private ArrowfallEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		HOLDER = PracticalEnchantments.resolveEnchantment(registries, ID);
	}

	public static void onHit(ServerLevel level, AbstractArrow arrow, HitResult hitResult) {
		if (HOLDER == null || ArrowfallQueue.isGenerated(arrow)) {
			return;
		}
		if (!(arrow.getOwner() instanceof ServerPlayer player)) {
			return;
		}
		ItemStack weapon = arrow.getWeaponItem();
		int enchantLevel = weapon.isEmpty() ? 0 : weapon.getEnchantments().getLevel(HOLDER);
		if (enchantLevel <= 0) {
			return;
		}
		Vec3 location = hitResult.getLocation();
		BlockPos center = BlockPos.containing(location);
		if (!level.isLoaded(center)) {
			return;
		}
		int count = Tunables.arrowfallBaseArrows() + Tunables.arrowfallArrowsPerLevel() * enchantLevel;
		if (player.getRandom().nextDouble() * 100.0D < Tunables.arrowfallExtraChancePercent(enchantLevel)) {
			count += player.getRandom().nextInt(Math.max(1, enchantLevel)) + 1;
		}
		if (player.getRandom().nextDouble() * 100.0D < Tunables.arrowfallArrowRainChancePercent(enchantLevel)) {
			int min = Tunables.arrowfallArrowRainMin();
			int max = Math.max(min, Tunables.arrowfallArrowRainMax());
			count = min + player.getRandom().nextInt(max - min + 1);
		}
		ItemStack ammo = arrow.getPickupItemStackOrigin().copy();
		if (ammo.isEmpty()) {
			ammo = new ItemStack(Items.ARROW);
		}
		ItemStack weaponCopy = weapon.copy();
		int fireTicks = arrow.getRemainingFireTicks();
		long now = level.getGameTime();
		int radius = Tunables.arrowfallRadius();
		int minDelay = Tunables.arrowfallDelayMinTicks();
		int maxDelay = Math.max(minDelay, Tunables.arrowfallDelayMaxTicks());
		int spawned = 0;
		for (int attempt = 0; attempt < count * 4 && spawned < count; attempt++) {
			double offsetX = (player.getRandom().nextDouble() * 2.0D - 1.0D) * radius;
			double offsetZ = (player.getRandom().nextDouble() * 2.0D - 1.0D) * radius;
			double y = location.y + 3.0D + player.getRandom().nextDouble() * 3.0D;
			Vec3 origin = new Vec3(location.x + offsetX, y, location.z + offsetZ);
			BlockPos pos = BlockPos.containing(origin);
			if (!level.isLoaded(pos) || !level.getBlockState(pos).isAir()) {
				continue;
			}
			int delay = minDelay + player.getRandom().nextInt(maxDelay - minDelay + 1);
			ArrowfallQueue.enqueue(level, player.getUUID(), origin, ammo, weaponCopy, fireTicks, now + delay);
			spawned++;
		}
	}
}
