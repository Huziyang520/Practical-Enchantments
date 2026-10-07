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
 *
 * <p>原版 {@link Player#getDestroySpeed(BlockState)} 在玩家不在地面时会将挖掘速度
 * 除以 5（空中速度仅剩 20%）。本 Mixin 在该方法返回时，若玩家佩戴了浮空速掘附魔的
 * 头盔且处于空中，则将返回值乘回 5，使空中挖掘速度恢复到与地面一致。</p>
 *
 * <p>仅当玩家处于空中时原版才会应用 ÷5，因此这里仅在 {@code !onGround()} 时修正，
 * 不会影响地面挖掘速度。</p>
 *
 * <h2>1.20.1：注入点是直移</h2>
 *
 * <p>{@code Player#getDestroySpeed(BlockState)} 在本版本存在且是可注入的实例方法
 * （javap 见下），注入 RETURN 这一定位与 26.3 完全相同：</p>
 *
 * <pre>
 * public float net.minecraft.world.entity.player.Player.getDestroySpeed(net.minecraft.world.level.block.state.BlockState);
 *   descriptor: (Lnet/minecraft/world/level/block/state/BlockState;)F
 * </pre>
 *
 * <p>并用 {@code javap -c} 核对了"惩罚实际发生在这个被注入的方法里"这一前提——
 * {@code getDestroySpeed} 只是转调 {@code getDigSpeed(BlockState, BlockPos)}，
 * 而 {@code getDigSpeed} 的字节码里正是：</p>
 *
 * <pre>
 *   181: aload_0
 *   182: invokevirtual #927   // Method onGround:()Z
 *   185: ifne          194
 *   188: fload_3
 *   189: ldc_w         #1368  // float 5.0f
 *   192: fdiv
 *   193: fstore_3
 * </pre>
 *
 * <p>所以 {@code getDestroySpeed} 的返回值里确实已经含了那次 ÷5，在 RETURN 处乘回 5
 * 就能精确抵消，与原版 {@code onGround()} 判定口径一致。</p>
 *
 * <h2>1.20.1：附魔句柄改成按 ID 取对象</h2>
 *
 * <p>26.3 读的是 {@code AerialHasteEnchantment.HOLDER}（{@code Holder<Enchantment>}，
 * 由该类的 {@code registerCallbacks} 在数据包读完后填值，mixin 里判空）。
 * 1.20.1 没有 Holder 这一层，而那个字段由另一位负责的
 * {@code AerialHasteEnchantment} 拥有，其最终类型尚未定；为了不把本类绑在别人的字段形状上，
 * 这里只依赖该类的 {@code ID} 常量（协作约定里 {@code *Enchantment} 一定导出 ID），
 * 用 {@link BuiltInRegistries#ENCHANTMENT} 按 id 取对象并缓存。</p>
 *
 * <p>这个取法有一个额外好处：查不到时返回 {@code null} 而不是抛异常。26.3 的 HOLDER
 * 判空是"还没注册"，这里的 null 是"没注册或已被配置禁用"，两者都应当安静地不生效——
 * 而这个方法每次挖掘都会被调用，抛异常是不可接受的。</p>
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
