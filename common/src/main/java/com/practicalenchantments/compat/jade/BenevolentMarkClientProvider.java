// Jade 客户端组件：1.21.6 起数据 provider 与组件 provider 必须是两个类。

package com.practicalenchantments.compat.jade;

import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class BenevolentMarkClientProvider implements IEntityComponentProvider {
	public static final BenevolentMarkClientProvider INSTANCE = new BenevolentMarkClientProvider();
	private static final Identifier UID =
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "combat_marks");
	private static final String BENEVOLENT = "pe_benevolent";
	private static final String RIGHTEOUS = "pe_righteous";

	private BenevolentMarkClientProvider() {
	}

	@Override
	public Identifier getUid() {
		return UID;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		CompoundTag tag = accessor.getServerData();
		int benevolent = tag.getIntOr(BENEVOLENT, 0);
		int righteous = tag.getIntOr(RIGHTEOUS, 0);
		if (benevolent > 0) {
			tooltip.add(Component.translatable("jade.practical_enchantments.benevolent_marks", benevolent));
		}
		if (righteous > 0) {
			tooltip.add(Component.translatable("jade.practical_enchantments.righteous_marks", righteous));
		}
	}
}
