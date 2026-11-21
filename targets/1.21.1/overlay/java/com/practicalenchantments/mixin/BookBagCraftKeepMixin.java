// 书袋升级用合成台时保留原有内容（普通配方不复制输入物品的组件）。
// 1.21.1 的 assemble 多一个 HolderLookup.Provider 参数。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.BookBagItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class BookBagCraftKeepMixin {
	@Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
			at = @At("RETURN"))
	private void pe$keepContents(CraftingInput input, HolderLookup.Provider registries,
			CallbackInfoReturnable<ItemStack> callback) {
		ItemStack result = callback.getReturnValue();
		if (result == null || result.isEmpty() || !(result.getItem() instanceof BookBagItem)) {
			return;
		}
		for (int slot = 0; slot < input.size(); slot++) {
			ItemStack ingredient = input.getItem(slot);
			if (!(ingredient.getItem() instanceof BookBagItem)) {
				continue;
			}
			ItemContainerContents contents = ingredient.get(DataComponents.CONTAINER);
			if (contents != null) {
				result.set(DataComponents.CONTAINER, contents);
			}
			return;
		}
	}
}
