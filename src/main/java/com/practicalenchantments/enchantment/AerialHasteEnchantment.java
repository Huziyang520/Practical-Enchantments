package com.practicalenchantments.enchantment;

import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 浮空速掘附魔
 * 消除空中挖掘速度惩罚
 *
 * <h2>1.20.1 移植说明（相对 26.3 的强制差异）</h2>
 * <ol>
 * <li><b>{@code registerCallbacks} 少一个参数。</b>26.3 的签名是
 * {@code registerCallbacks(EnchantmentEventRegistrar, HolderLookup.Provider)}，那个
 * {@code HolderLookup.Provider} 是给动态注册表用的：26.3 的附魔住在数据包驱动的注册表里，只有拿到
 * provider 才能解析出 {@code Holder}。1.20.1 的附魔在模组构造期就进了静态注册表
 * （{@code BuiltInRegistries.ENCHANTMENT}），任何时刻都能按 id 直接取到对象，所以 provider 这个
 * 参数没有存在意义，调用方 {@code PracticalEnchantments.registerEventCallbacks()} 也只传 registrar。
 * 本类的 {@code registerCallbacks} 因此是单参数版本。</li>
 *
 * <li><b>{@code Holder<Enchantment>} → {@code Enchantment}。</b>26.3 的 {@code HOLDER} 字段类型是
 * {@code Holder<Enchantment>}。1.20.1 没有「附魔重载」这件事——静态注册表建好就不再重建，不存在
 * 需要 Holder 间接层来保持正确的场景，{@code PracticalEnchantments.resolveEnchantment(String)} 直接
 * 返回 {@code Enchantment}。字段类型随之变成 {@code Enchantment}。</li>
 *
 * <li><b>本类的 {@code HOLDER} 字段在 1.20.1 无人读取。</b>这是刻意保留的形状，不是遗留物：
 * 26.3 里浮空速掘的效果完全落在 {@code PlayerGetDestroySpeedMixin} 上，本类只负责把 HOLDER 填好。
 * 1.20.1 的 {@code PlayerGetDestroySpeedMixin} 为了不把自身绑在别人的字段形状上，改成自己按
 * {@code AerialHasteEnchantment.ID} 查 {@code BuiltInRegistries.ENCHANTMENT} 并缓存
 * （查不到返回 {@code null}、安静判为未附魔）。两条路都指向同一个附魔对象，行为一致；字段保留是为了
 * 与 26.3 的注册流程逐行对应，且任何将来的读者仍可直接取用。</li>
 *
 * <li>其余部分（id 字符串、私有构造器、无事件回调）与 26.3 完全一致。</li>
 * </ol>
 */
public final class AerialHasteEnchantment {

	public static final String ID = PracticalEnchantments.MOD_ID + ":aerial_haste";

	/**
	 * 本附魔对象（供 Mixin 判断头盔是否佩戴本附魔）。
	 *
	 * <p>26.3 为 {@code Holder<Enchantment>}；1.20.1 无 Holder 层，见类注释第 2、3 条。</p>
	 */
	public static volatile Enchantment HOLDER;


	public static void registerCallbacks(EnchantmentEventRegistrar registrar) {
		HOLDER = PracticalEnchantments.resolveEnchantment(ID);
		// 效果由 Mixin PlayerGetDestroySpeedMixin 实现，无需注册事件回调
	}
}