// 护身符饰品栏门面：Curios 反射访问 + 副手槽位，前置缺失时静默降级为“未装备”。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.platform.Services;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredItem;

public final class AmuletSupport {
	private static final String CURIOS_ID = "curios";
	private static final EquipmentSlot[] WORN_SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND};
	private static final String CURIOS_API = "top.theillusivec4.curios.api.CuriosApi";
	private static Set<Item> amulets;
	private static Boolean curiosLoaded;

	private AmuletSupport() {
	}

	public static boolean isAvailable() {
		return curiosLoaded();
	}

	public static boolean isAmulet(ItemStack stack) {
		return !stack.isEmpty() && amulets().contains(stack.getItem());
	}

	public static boolean isWearing(LivingEntity entity, Item item) {
		return entity.getItemBySlot(EquipmentSlot.OFFHAND).is(item)
				|| curiosScan(entity, stack -> stack.is(item));
	}

	public static int amuletLevel(LivingEntity entity, Holder<Enchantment> enchantment) {
		return curiosAmuletLevel(entity, enchantment);
	}

	public static int wornLevel(Player player, Holder<Enchantment> enchantment) {
		int best = amuletLevel(player, enchantment);
		for (EquipmentSlot slot : WORN_SLOTS) {
			best = Math.max(best, player.getItemBySlot(slot).getEnchantments().getLevel(enchantment));
		}
		return best;
	}

	public static float penaltyFactor(ItemStack stack) {
		if (stack.is(PeContent.NETHERITE_AMULET.get())) {
			return 0.05F;
		}
		if (stack.is(PeContent.DIAMOND_AMULET.get())) {
			return 0.25F;
		}
		if (stack.is(PeContent.GOLDEN_AMULET.get())) {
			return 0.5F;
		}
		return 0.75F;
	}

	public static int repairCostCap(ItemStack stack) {
		if (!isAmulet(stack)) {
			return Integer.MAX_VALUE;
		}
		if (stack.is(PeContent.NETHERITE_AMULET.get())) {
			return 1;
		}
		if (stack.is(PeContent.DIAMOND_AMULET.get())) {
			return 2;
		}
		if (stack.is(PeContent.GOLDEN_AMULET.get())) {
			return 3;
		}
		return 4;
	}

	private static Set<Item> amulets() {
		Set<Item> cached = amulets;
		if (cached != null) {
			return cached;
		}
		Set<Item> built = new HashSet<>();
		for (DeferredItem<Item> amulet : PeContent.AMULETS) {
			Item item = amulet.get();
			if (item != null) {
				built.add(item);
			}
		}
		if (built.isEmpty()) {
			return built;
		}
		amulets = built;
		return built;
	}

	private static boolean curiosLoaded() {
		if (curiosLoaded == null) {
			curiosLoaded = Services.PLATFORM.isModLoaded(CURIOS_ID);
		}
		return curiosLoaded;
	}

	public static java.util.List<ItemStack> equippedAmuletStacks(LivingEntity entity) {
		java.util.List<ItemStack> found = new java.util.ArrayList<>();
		java.util.Set<ItemStack> seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
		if (!curiosLoaded()) {
			return found;
		}
		for (int guard = 0; guard < 16; guard++) {
			ItemStack[] hit = {null};
			curiosScan(entity, stack -> {
				if (isAmulet(stack) && !seen.contains(stack)) {
					hit[0] = stack;
					return true;
				}
				return false;
			});
			if (hit[0] == null) {
				break;
			}
			seen.add(hit[0]);
			found.add(hit[0]);
		}
		return found;
	}

	private static int curiosAmuletLevel(LivingEntity entity, Holder<Enchantment> enchantment) {
		if (!curiosLoaded()) {
			return 0;
		}
		int[] best = {0};
		curiosScan(entity, stack -> {
			if (isAmulet(stack)) {
				best[0] = Math.max(best[0], stack.getEnchantments().getLevel(enchantment));
			}
			return false;
		});
		return best[0];
	}

	private static boolean curiosScan(LivingEntity entity, Predicate<ItemStack> visitor) {
		if (!curiosLoaded()) {
			return false;
		}
		Object handler = curiosHandler(entity);
		if (handler == null) {
			return false;
		}
		try {
			Object found = handler.getClass()
					.getMethod("findFirstCurio", Predicate.class)
					.invoke(handler, visitor);
			return found instanceof Optional<?> optional && optional.isPresent();
		} catch (ReflectiveOperationException | RuntimeException e) {
			return false;
		}
	}

	private static Object curiosHandler(LivingEntity entity) {
		try {
			Object inventory = Class.forName(CURIOS_API)
					.getMethod("getCuriosInventory", LivingEntity.class)
					.invoke(null, entity);
			Optional<?> resolved = inventory instanceof Optional<?> optional ? optional : resolveLazy(inventory);
			return resolved.isPresent() ? resolved.get() : null;
		} catch (ReflectiveOperationException | RuntimeException e) {
			return null;
		}
	}

	private static Optional<?> resolveLazy(Object inventory) {
		if (inventory == null) {
			return Optional.empty();
		}
		try {
			Object resolved = inventory.getClass().getMethod("resolve").invoke(inventory);
			return resolved instanceof Optional<?> optional ? optional : Optional.empty();
		} catch (ReflectiveOperationException | RuntimeException e) {
			return Optional.empty();
		}
	}
}
