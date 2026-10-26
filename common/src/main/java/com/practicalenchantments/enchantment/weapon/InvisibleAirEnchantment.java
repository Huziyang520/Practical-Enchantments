// 风王结界：命中翻倍伤害、攻击距离加一半，带此附魔的剑在背包里显示为不可见。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class InvisibleAirEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":invisible_air";
	public static final Identifier HIDDEN_MODEL = Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "invisible_air");
	private static Holder<Enchantment> holder;

	private InvisibleAirEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, InvisibleAirEnchantment::onDamage);
		registrar.register(holder, BuiltInEvents.ENTITY_TICK, InvisibleAirEnchantment::onTick);
	}

	public static Holder<Enchantment> holder(RegistryAccess access) {
		Holder<Enchantment> cached = holder;
		if (cached != null) {
			return cached;
		}
		return access.lookupOrThrow(Registries.ENCHANTMENT)
				.get(ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse(ID)))
				.orElse(null);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext context) {
		if (event.source().getEntity() != event.attacker()) {
			return;
		}
		event.damage().setValue(event.damage().getValue() * 2.0F);
	}

	private static void onTick(BuiltInEvents.EntityTickEvent event, EnchantmentContext context) {
		if (!(event.entity() instanceof Player player) || (player.tickCount & 15) != 0) {
			return;
		}
		Holder<Enchantment> current = holder;
		if (current == null) {
			return;
		}
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (stack.isEmpty() || stack.getEnchantments().getLevel(current) <= 0) {
				continue;
			}
			if (!HIDDEN_MODEL.equals(stack.get(DataComponents.ITEM_MODEL))) {
				stack.set(DataComponents.ITEM_MODEL, HIDDEN_MODEL);
			}
		}
	}
}
