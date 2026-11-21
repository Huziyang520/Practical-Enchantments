// Jade 提示：进化强度、有效书架/附魔书、台面物品。

package com.practicalenchantments.compat.jade;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class EvolutionTableProvider implements IComponentProvider<BlockAccessor> {
	public static final EvolutionTableProvider INSTANCE = new EvolutionTableProvider();
	private static final ResourceLocation UID =
			ResourceLocation.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "evolution_table");

	private EvolutionTableProvider() {
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (!(accessor.getBlockEntity() instanceof EvolutionTableBlockEntity table)) {
			return;
		}
		ItemStack displayed = table.displayed();
		if (!displayed.isEmpty()) {
			tooltip.add(Component.translatable("jade.practical_enchantments.evolution_table.item",
					displayed.getHoverName()));
		}
		if (table.isWorking()) {
			tooltip.add(Component.translatable("jade.practical_enchantments.evolution_table.working"));
		}
		tooltip.add(Component.translatable("jade.practical_enchantments.evolution_table.power",
				table.strength(), EvolutionTableBlockEntity.MAX_STRENGTH));
		tooltip.add(Component.translatable("jade.practical_enchantments.evolution_table.shelves",
				table.shelves(), table.books()));
	}
}
