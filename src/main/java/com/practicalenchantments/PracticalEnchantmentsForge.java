package com.practicalenchantments;

import com.practicalenchantments.config.ConfigUi;
import com.practicalenchantments.config.ForgeConfig;
import com.practicalenchantments.enchantment.LifeStealEnchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Forge 入口：装配本模组，并承载「吸血」的伤害事件桥接。
 */
@Mod(PracticalEnchantments.MOD_ID)
@Mod.EventBusSubscriber(modid = PracticalEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PracticalEnchantmentsForge {

	public PracticalEnchantmentsForge() {
		PracticalEnchantments.bootstrap();

		ModLoadingContext.get().registerConfig(ForgeConfig.type(), ForgeConfig.SPEC);
		// Loading 与 Reloading 都是 ModConfigEvent 的子类，按父类型挂一次即可两条都收到。
		FMLJavaModLoadingContext.get().getModEventBus()
				.addListener((ModConfigEvent event) -> ForgeConfig.apply());
		ForgeConfig.apply();

		// 两套可选配置界面都只住在客户端；DistExecutor 让专用服务器上 ConfigUi 整个类不被加载。
		if (FMLEnvironment.dist == Dist.CLIENT) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ConfigUi::install);
		}
	}

	/**
	 * 「吸血」的加载器桥接：Forge 在护甲/魔法减免之后、扣血之前触发 {@code LivingDamageEvent}，
	 * 拿到的正是本次实际造成的伤害；该事件不带盾牌格挡信息，本附魔也不用它。
	 *
	 * @param event 受伤事件；{@code getEntity()} 是被击者，{@code getSource()} 是伤害来源
	 */
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		LifeStealEnchantment.onAfterDamage(event.getEntity(), event.getSource(), event.getAmount(), 0.0F, false);
	}
}
