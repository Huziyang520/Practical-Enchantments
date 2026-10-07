package com.practicalenchantments.mixin;

import com.huziyang520.merlinlib.mixin.AbstractArrowAccessor;
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

/**
 * 粉碎附魔：投掷三叉戟击中方块时瞬间破坏。
 *
 * <p>ThrownTrident 不重写 {@code onHitBlock}，所以注入打在父类
 * {@link AbstractArrow#onHitBlock(BlockHitResult)} HEAD，用 instanceof 过滤出三叉戟。
 * 不取消原方法——插中方块后该插就插、忠诚该回回。</p>
 *
 * <p>挖掘等级用对应等级的镐作为"虚拟工具"判定 {@code isCorrectToolForDrops}：
 * 达不到等级的方块<b>不破坏</b>（防止低等级粉碎炸毁高等级矿物）。基岩等
 * 不可破坏方块（destroyTime &lt; 0）永远跳过。掉落物与经验按虚拟镐生成。</p>
 *
 * <h2>1.20.1：注入点直移</h2>
 *
 * <p>javap 复核（与 26.3 同名同描述符，且 {@code ThrownTrident} 确实没有覆写它）：</p>
 *
 * <pre>
 * protected void net.minecraft.world.entity.projectile.AbstractArrow.onHitBlock(net.minecraft.world.phys.BlockHitResult);
 *   descriptor: (Lnet/minecraft/world/phys/BlockHitResult;)V
 * </pre>
 *
 * <h2>1.20.1 的强制分歧：取"这枚三叉戟是什么"的方式</h2>
 *
 * <p>26.3 用 {@code trident.getWeaponItem()}。<b>1.20.1 的 {@code AbstractArrow} 没有这个方法</b>——
 * 用 javap 列全成员确认，整个类里没有任何返回 {@code ItemStack} 的公开成员。本版本对应的是
 * {@code protected abstract ItemStack getPickupItem()}，它对三叉戟返回的就是
 * {@code tridentItem} 的副本（{@code javap -c} 看到的是
 * {@code getfield tridentItem; invokevirtual ItemStack.copy(); areturn}），
 * 正是这里要的"投掷时那份武器快照"。</p>
 *
 * <p>它是 {@code protected}，本包无法直接调用；MerlinLib 已经为此提供了公开的 Mixin 访问器
 * {@link AbstractArrowAccessor}（其 {@code merlinlib$getPickupItem()} 用 {@code @Invoker}
 * 暴露同一方法，并已登记进 {@code merlinlib.mixins.json}）。这里复用它，而不是另造一个访问器
 * 去改 PE 自己的 mixin 配置。</p>
 *
 * <p>其余调用逐个 javap 复核过：{@code BlockState#getDestroySpeed(BlockGetter, BlockPos)}、
 * {@code BlockState#requiresCorrectToolForDrops()}、{@code BlockState#spawnAfterBreak(ServerLevel, BlockPos, ItemStack, boolean)}
 * （均在 {@code BlockBehaviour$BlockStateBase} 上声明）、
 * {@code ItemStack#isCorrectToolForDrops(BlockState)}、
 * 6 参 {@code Block#dropResources(...)}、以及
 * {@code Level#destroyBlock(BlockPos, boolean)}（{@code LevelWriter} 的默认方法，本类经由
 * {@code Level → LevelAccessor → LevelSimulatedRW → LevelWriter} 继承到）——签名全部一致。</p>
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

	@Inject(method = "onHitBlock(Lnet/minecraft/world/phys/BlockHitResult;)V",
		at = @At("HEAD"), require = 1)
	private void practicalenchantments$smashBlock(BlockHitResult result, CallbackInfo ci) {
		if (!((Object) this instanceof ThrownTrident trident)) {
			return;
		}
		if (!(trident.level() instanceof ServerLevel level)) {
			return;
		}
		// 1.20.1 没有 AbstractArrow#getWeaponItem；借 MerlinLib 的访问器读 getPickupItem()，
		// 对三叉戟即"投掷时那份 tridentItem 的副本"。
		ItemStack weapon = ((AbstractArrowAccessor) trident).merlinlib$getPickupItem();
		int smashLevel = DeathSaveSupport.getLevel(weapon, "smashing");
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
		if (DeathSaveSupport.getLevelFull(weapon, "minecraft:loyalty") > 0
			&& DeathSaveSupport.getLevel(weapon, "penetration") > 0
			&& owner instanceof net.minecraft.world.entity.player.Player player) {
			TridentPullSupport.startItemPull(player.getUUID());
		}
	}
}
