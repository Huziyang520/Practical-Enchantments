package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.AerialHasteEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 浮空速掘：消除空中挖掘速度惩罚。
 */
@Mixin(Player.class)
public abstract class PlayerGetDestroySpeedMixin {

	/** 浮空速掘附魔对象缓存（注册完成后才可能非空，见类 javadoc） */
	private static Enchantment practical$aerialHaste;

	@Inject(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F",
		at = @At("RETURN"), cancellable = true, require = 1)
	private void practicalenchantments$cancelAirbornePenalty(BlockState state, CallbackInfoReturnable<Float> cir) {
		@SuppressWarnings("DataFlowIssue")
		Player self = (Player) (Object) this;

		// 仅当玩家在空中时才存在 ÷5 惩罚
		if (self.onGround()) {
			return;
		}

		// 头盔佩戴浮空速掘附魔时，乘回 5 抵消惩罚
		if (hasAerialHaste(self)) {
			cir.setReturnValue(cir.getReturnValueF() * 5.0f);
		}
	}

	private static boolean hasAerialHaste(Player player) {
		Enchantment enchantment = practical$aerialHaste;
		if (enchantment == null) {
			// AerialHasteEnchantment.ID 由该类自己维护；查不到就是没注册/被禁用，安静返回 false。
			enchantment = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(AerialHasteEnchantment.ID));
			if (enchantment == null) {
				return false;
			}
			practical$aerialHaste = enchantment;
		}
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		if (helmet.isEmpty()) {
			return false;
		}
		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, helmet) > 0;
	}
}
