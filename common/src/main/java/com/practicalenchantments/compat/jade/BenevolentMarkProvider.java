// Jade 实体提示的服务端数据：只负责把仁击 / 义斩层数写进 server data。

package com.practicalenchantments.compat.jade;

import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.enchantment.support.CombatMarkTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public final class BenevolentMarkProvider implements IServerDataProvider<EntityAccessor> {
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
}

