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

/**
 * 神木气息击杀时生物掉落翻倍。
 */
@Mixin(LivingEntity.class)
public abstract class GodwoodLootMixin {

	/**
	 * {@code dropFromLootTable} 不收回列表，而是把每个掉落推给一个消费者
	 * （{@code getRandomItems(LootParams, long, Consumer)}）；翻倍即把每个掉落推两次。
	 */
	@Redirect(method = "dropFromLootTable(Lnet/minecraft/world/damagesource/DamageSource;Z)V", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems("
							+ "Lnet/minecraft/world/level/storage/loot/LootParams;J"
							+ "Ljava/util/function/Consumer;)V"))
	private void practicalenchantments$doubleLoot(LootTable table, LootParams params, long seed,
			Consumer<ItemStack> consumer) {
		if (!KillMarks.consumeDoubled((LivingEntity) (Object) this)) {
			table.getRandomItems(params, seed, consumer);
			return;
		}
		// 两份都是副本：原栈会被两个物品实体共享同一个实例，那样存档时会互相干扰。
		table.getRandomItems(params, seed, drop -> {
			consumer.accept(drop.copy());
			consumer.accept(drop.copy());
		});
	}
}
