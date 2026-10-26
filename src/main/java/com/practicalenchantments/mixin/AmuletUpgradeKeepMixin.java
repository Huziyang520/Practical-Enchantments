// 护身符升级保留附魔：合成台算出产物后，把网格里旧护身符的附魔组件搬到产物上。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.AmuletSupport;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftingMenu.class)
public abstract class AmuletUpgradeKeepMixin {
	@Redirect(
			method = "slotChangedCraftingGrid",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"),
			require = 1)
	private static void practicalenchantments$keepEnchantments(ResultContainer container, int slot, ItemStack result,
			AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots,
			ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe) {
		if (AmuletSupport.isAmulet(result)) {
			for (int i = 0; i < craftSlots.getContainerSize(); i++) {
				ItemStack input = craftSlots.getItem(i);
				if (AmuletSupport.isAmulet(input) && !input.getEnchantments().isEmpty()) {
					result.set(DataComponents.ENCHANTMENTS, input.getEnchantments());
					break;
				}
			}
		}
		container.setItem(slot, result);
	}
}
