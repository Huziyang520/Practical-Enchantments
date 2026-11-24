// Jade 实体提示：显示仁击 / 义斩留下的层数，0 层不显示。

package com.practicalenchantments.compat.jade;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.support.CombatMarkTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class BenevolentMarkProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
	public static final BenevolentMarkProvider INSTANCE = new BenevolentMarkProvider();
	private static final Identifier UID =
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "combat_marks");
	private static final String BENEVOLENT = "pe_benevolent";
	private static final String RIGHTEOUS = "pe_righteous";

	private BenevolentMarkProvider() {
	}

	@Override
	public Identifier getUid() {
		return UID;
	}

	@Override
	public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
		if (!(accessor.getEntity() instanceof LivingEntity living)
				|| !(living.level() instanceof ServerLevel level)) {
			return;
		}
		CombatMarkTracker tracker = CombatMarkTracker.get(level);
		int benevolent = tracker.benevolent(living.getUUID());
		int righteous = tracker.righteous(living.getUUID());
		if (benevolent > 0) {
			tag.putInt(BENEVOLENT, benevolent);
		}
		if (righteous > 0) {
			tag.putInt(RIGHTEOUS, righteous);
		}
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
