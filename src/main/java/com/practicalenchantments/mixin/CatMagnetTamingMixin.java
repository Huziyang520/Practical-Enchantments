package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.AiRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 吸猫体质：喂鱼驯服必定成功（原版是 1/3）。
 */
@Mixin(Cat.class)
public class CatMagnetTamingMixin {

	/**
	 * 补一次必定成功的驯服。
	 *
	 * @param player 交互的玩家
	 * @param hand   交互的手
	 * @param info   原版返回值
	 */
	@Inject(method = "mobInteract", at = @At("RETURN"))
	private void practical$alwaysTame(Player player, InteractionHand hand,
									CallbackInfoReturnable<InteractionResult> info) {
		if (!(player.level() instanceof ServerLevel level)) {
			return;
		}
		Cat self = (Cat) (Object) this;
		if (self.isTame() || !AiRules.hasCatAttraction(player)) {
			return;
		}
		ItemStack held = player.getItemInHand(hand);
		if (!held.is(Items.COD) && !held.is(Items.SALMON)) {
			return;
		}
		self.tame(player);
		level.broadcastEntityEvent(self, (byte) 7);
	}
}
