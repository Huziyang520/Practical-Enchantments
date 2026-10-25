// 粉碎附魔：投掷三叉戟击中方块时瞬间破坏。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.DeathSaveSupport;
import com.practicalenchantments.enchantment.TridentPullSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class TridentSmashMixin {
	private static final ItemStack[] VIRTUAL_PICKAXES = {
		new ItemStack(Items.WOODEN_PICKAXE),
		new ItemStack(Items.STONE_PICKAXE),
		new ItemStack(Items.IRON_PICKAXE),
		new ItemStack(Items.DIAMOND_PICKAXE),
		new ItemStack(Items.NETHERITE_PICKAXE)
	};

	@Inject(method = "onHitBlock", at = @At("HEAD"))
	private void practicalenchantments$smashBlock(BlockHitResult result, CallbackInfo ci) {
		if (!((Object) this instanceof ThrownTrident trident)) {
			return;
		}
		if (!(trident.level() instanceof ServerLevel level)) {
			return;
		}
		int smashLevel = DeathSaveSupport.getLevel(trident.getWeaponItem(), "smashing");
		if (smashLevel <= 0) {
			return;
		}

		BlockPos pos = result.getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (state.isAir()) {
			return;
		}

		if (state.getDestroySpeed(level, pos) < 0) {
			return;
		}

		ItemStack virtualPickaxe = VIRTUAL_PICKAXES[Math.min(smashLevel, VIRTUAL_PICKAXES.length) - 1];

		if (state.requiresCorrectToolForDrops() && !virtualPickaxe.isCorrectToolForDrops(state)) {
			return;
		}

		Entity owner = trident.getOwner();
		Block.dropResources(state, level, pos, level.getBlockEntity(pos), owner, virtualPickaxe);
		state.spawnAfterBreak(level, pos, virtualPickaxe, true);
		level.destroyBlock(pos, false);

		if (DeathSaveSupport.getLevelFull(trident.getWeaponItem(), "minecraft:loyalty") > 0
			&& DeathSaveSupport.getLevel(trident.getWeaponItem(), "penetration") > 0
			&& owner instanceof net.minecraft.world.entity.player.Player player) {
			TridentPullSupport.startItemPull(player.getUUID());
		}
	}
}
