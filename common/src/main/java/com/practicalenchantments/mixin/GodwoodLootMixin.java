package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.KillMarks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

	@Redirect(method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;"
			+ "Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;"
			+ "Ljava/util/function/BiConsumer;)Z", require = 1,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems("
							+ "Lnet/minecraft/world/level/storage/loot/LootParams;)"
							+ "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
	private ObjectArrayList<ItemStack> practicalenchantments$doubleLoot(LootTable table, LootParams params) {
		ObjectArrayList<ItemStack> drops = table.getRandomItems(params);
		if (!KillMarks.consumeDoubled((LivingEntity) (Object) this)) {
			return drops;
		}
		ObjectArrayList<ItemStack> doubled = new ObjectArrayList<>(drops.size() * 2);
		for (ItemStack drop : drops) {
			// 两份都是副本：原栈会被两个物品实体共享同一个实例，那样存档时会互相干扰。
			doubled.add(drop.copy());
			doubled.add(drop.copy());
		}
		return doubled;
	}
}
