package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.DeathSaveSupport;
import com.practicalenchantments.enchantment.TridentPullSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 粉碎附魔：投掷三叉戟击中方块时瞬间破坏。
 */
@Mixin(AbstractArrow.class)
public abstract class TridentSmashMixin {

	/** 等级 → 虚拟镐（Ⅰ 木 → Ⅴ 下界合金） */
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
		// 基岩/末地传送门框架等不可破坏方块（destroyTime < 0）直接跳过，
		// 三叉戟照常插上方块，不粉碎
		if (state.getDestroySpeed(level, pos) < 0) {
			return;
		}

		ItemStack virtualPickaxe = VIRTUAL_PICKAXES[Math.min(smashLevel, VIRTUAL_PICKAXES.length) - 1];
		// 挖掘等级匹配：需要工具且虚拟镐等级不够的方块不破坏（原本只破坏不掉落，
		// 会导致低等级粉碎白白炸掉高等级矿物）
		if (state.requiresCorrectToolForDrops() && !virtualPickaxe.isCorrectToolForDrops(state)) {
			return;
		}

		Entity owner = trident.getOwner();
		// 用虚拟镐生成掉落物（决定时运类掉落这里没有时运，按基础掉落）
		Block.dropResources(state, level, pos, level.getBlockEntity(pos), owner, virtualPickaxe);
		// 矿石经验
		state.spawnAfterBreak(level, pos, virtualPickaxe, true);
		// destroyBlock 自带破坏粒子（levelEvent 2001）+ 方块更新
		level.destroyBlock(pos, false);

		// 粉碎 × 忠诚：方块命中路径不走 onHitEntity，牵引窗口必须在这里显式启动，
		// 否则粉碎掉落物永远拉不回来（坑 30 根因）
		// 忠诚 + 粉碎 + 贯穿 三者齐备才牵引掉落物：README 一直这么写，代码此前只查了忠诚，
		// 于是"没有贯穿也能把方块拉回来"。
		if (DeathSaveSupport.getLevelFull(trident.getWeaponItem(), "minecraft:loyalty") > 0
			&& DeathSaveSupport.getLevel(trident.getWeaponItem(), "penetration") > 0
			&& owner instanceof net.minecraft.world.entity.player.Player player) {
			TridentPullSupport.startItemPull(player.getUUID());
		}
	}
}
