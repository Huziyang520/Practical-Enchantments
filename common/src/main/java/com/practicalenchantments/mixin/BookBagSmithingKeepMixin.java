// 书袋用锻造台升级时保留原有内容（原版锻造不复制输入物品的组件）。

package com.practicalenchantments.mixin;

import com.practicalenchantments.content.PeContent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTransformRecipe.class)
public class BookBagSmithingKeepMixin {
	@Inject(method = "assemble", at = @At("RETURN"))
	private void pe$keepContents(SmithingRecipeInput input, CallbackInfoReturnable<ItemStack> callback) {
		ItemStack result = callback.getReturnValue();
		if (result == null || result.isEmpty()
				|| !result.is(PeContent.item(PeContent.BOOK_BAG_NETHERITE))) {
			return;
		}
		ItemContainerContents contents = input.base().get(DataComponents.CONTAINER);
		if (contents != null) {
			result.set(DataComponents.CONTAINER, contents);
		}
	}
}
