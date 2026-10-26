// 护身符饰品栏门面：Curios 反射访问 + 副手槽位，前置缺失时静默降级为“未装备”。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.platform.Services;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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

	public static int amuletLevel(LivingEntity entity, Enchantment enchantment) {
		if (!curiosLoaded()) {
			return 0;
		}
		int[] best = {0};
		curiosScan(entity, stack -> {
			if (isAmulet(stack)) {
				best[0] = Math.max(best[0], EnchantmentHelper.getEnchantments(stack)
						.getOrDefault(enchantment, 0));
			}
			return false;
		});
		return best[0];
	}

	public static int wornLevel(Player player, Enchantment enchantment) {
		int best = amuletLevel(player, enchantment);
		for (EquipmentSlot slot : WORN_SLOTS) {
			best = Math.max(best, EnchantmentHelper.getEnchantments(player.getItemBySlot(slot))
					.getOrDefault(enchantment, 0));
		}
		return best;
	}

	public static float penaltyFactor(ItemStack stack) {
		if (stack.is(PeContent.item(PeContent.NETHERITE_AMULET))) {
			return 0.05F;
		}
		if (stack.is(PeContent.item(PeContent.DIAMOND_AMULET))) {
			return 0.25F;
		}
		if (stack.is(PeContent.item(PeContent.GOLDEN_AMULET))) {
			return 0.5F;
		}
		return 0.75F;
	}

	public static int repairCostCap(ItemStack stack) {
		if (!isAmulet(stack)) {
			return Integer.MAX_VALUE;
		}
		if (stack.is(PeContent.item(PeContent.NETHERITE_AMULET))) {
			return 1;
		}
		if (stack.is(PeContent.item(PeContent.DIAMOND_AMULET))) {
			return 2;
		}
		if (stack.is(PeContent.item(PeContent.GOLDEN_AMULET))) {
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
		for (ResourceLocation id : PeContent.amulets()) {
			Item item = PeContent.item(id);
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

	public static List<ItemStack> equippedAmuletStacks(LivingEntity entity) {
		List<ItemStack> found = new ArrayList<>();
		Set<ItemStack> seen = Collections.newSetFromMap(new IdentityHashMap<>());
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
