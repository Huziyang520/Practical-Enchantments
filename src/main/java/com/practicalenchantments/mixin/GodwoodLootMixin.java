// 神木气息击杀时生物掉落翻倍。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class GodwoodLootMixin {
	@Redirect(method = "dropFromLootTable(Lnet/minecraft/world/damagesource/DamageSource;Z)V", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems("
							+ "Lnet/minecraft/world/level/storage/loot/LootParams;J"
							+ "Ljava/util/function/Consumer;)V"))
	private void practicalenchantments$doubleLoot(LootTable table, LootParams params, long seed,
												Consumer<ItemStack> sink) {
		if (!KillMarks.consumeDoubled((LivingEntity) (Object) this)) {
			table.getRandomItems(params, seed, sink);
			return;
		}
		table.getRandomItems(params, seed, stack -> {
			sink.accept(stack.copy());
			sink.accept(stack.copy());
		});
	}
}
