// Jade 入口：只注册客户端组件，服务端不需要额外数据。

package com.practicalenchantments.compat.jade;

import com.practicalenchantments.content.EvolutionTableBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin("practical_enchantments")
public class PeJadePlugin implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(EvolutionTableProvider.INSTANCE, EvolutionTableBlock.class);
	}
}
