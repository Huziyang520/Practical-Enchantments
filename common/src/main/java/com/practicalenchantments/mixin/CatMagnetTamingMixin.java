package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.AiRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 吸猫体质：喂鱼驯服必定成功（原版是 1/3）。
 *
 * <p>取巧在**原版判定之后**补一次：不要复制它那段"喂鱼—消耗—随机数"的逻辑（版本一变就错），
 * 只在"这次用对了鱼、猫还没被驯服、玩家穿着该附魔"时直接标记为已驯服并播放原版的驯服粒子事件。
 * 原版失败时已吞掉的那条鱼不补回来——这本来就是"一定成功"的直观表现。</p>
 *
 * <p>只在服务端做（客户端只会看到一个结果），并且每 tick 的交互只会走到一次。</p>
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
