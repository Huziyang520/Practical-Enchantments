package com.practicalenchantments.neoforge;

import com.enchantlib.api.EnchantmentApi;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * NeoForge 平台入口。
 *
 * <p>与 Fabric 不同，NeoForge 没有任意 key 的 entrypoint 扫描，必须在 {@code @Mod}
 * 构造器中显式把附魔入口交给 EnchantLib。</p>
 *
 * <p>「吸血」需要护甲减免后的实际伤害，直接订阅 {@link LivingDamageEvent.Post}
 * 桥接到 common 的 {@code LifeStealEnchantment.onAfterDamage}。</p>
 *
 * <p>mixin 配置由 {@code META-INF/neoforge.mods.toml} 的 {@code [[mixins]]} 声明
 * （NeoForge 不会自动发现 jar 内的 mixin 配置）。</p>
 */
@Mod(PracticalEnchantments.MOD_ID)
public final class PracticalEnchantmentsNeoForge {

	public PracticalEnchantmentsNeoForge(IEventBus modEventBus) {
		// 显式注册附魔入口（NeoForge 无 entrypoint 自动扫描）
		EnchantmentApi.register(new PracticalEnchantments());

		// LivingDamageEvent.Post → AFTER_DAMAGE 桥接（吸血）
		NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, event ->
			LifeStealEnchantment.onAfterDamage(
				event.getEntity(), event.getSource(), event.getOriginalDamage(),
				event.getBlockedDamage(), event.getBlockedDamage() > 0));
	}
}
