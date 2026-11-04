// 弩的装填表在 1.20.1 是私有 NBT，用 @Invoker 走原版自己的读写方法。

package com.practicalenchantments.mixin;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.List;

@Mixin(CrossbowItem.class)
public interface CrossbowChargedAccessor {
	@Invoker("addChargedProjectile")
	static void practicalenchantments$add(ItemStack weapon, ItemStack ammo) {
		throw new AssertionError();
	}

	@Invoker("getChargedProjectiles")
	static List<ItemStack> practicalenchantments$charged(ItemStack weapon) {
		throw new AssertionError();
	}
}
