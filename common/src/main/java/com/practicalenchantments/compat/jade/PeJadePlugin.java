// Jade 入口：服务端数据 provider 与客户端组件 provider 分开注册。

package com.practicalenchantments.compat.jade;

import com.practicalenchantments.content.EvolutionTableBlock;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin("practical_enchantments")
public class PeJadePlugin implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerEntityDataProvider(BenevolentMarkProvider.INSTANCE, LivingEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(EvolutionTableProvider.INSTANCE, EvolutionTableBlock.class);
		registration.registerEntityComponent(BenevolentMarkClientProvider.INSTANCE, LivingEntity.class);
	}
}
