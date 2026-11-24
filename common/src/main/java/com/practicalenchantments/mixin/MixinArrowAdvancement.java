// 弓箭进度：单次最终伤害 30 点以上、50 格外，以及治疗箭命中亡灵。

package com.practicalenchantments.mixin;

import com.practicalenchantments.advancement.PeAdvancementTriggers;
import com.practicalenchantments.enchantment.weapon.ProjectileRangeSupport;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinArrowAdvancement {
	@Inject(method = "actuallyHurt", at = @At("RETURN"))
	private void pe$arrowProgress(ServerLevel level, DamageSource source, float amount, CallbackInfo info) {
		if (!(source.getDirectEntity() instanceof AbstractArrow arrow)
				|| !(source.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		LivingEntity victim = (LivingEntity) (Object) this;
		if (amount >= PeAdvancementTriggers.BIG_BOW_DAMAGE
				&& ProjectileRangeSupport.horizontalDistance(player, victim)
				> PeAdvancementTriggers.BIG_BOW_MIN_DISTANCE) {
			PeAdvancementTriggers.bigBowHit(player, amount);
		}
		if (victim.isInvertedHealAndHarm() && isHealingArrow(arrow)) {
			PeAdvancementTriggers.healingArrowUndead(player);
		}
	}

	private static boolean isHealingArrow(AbstractArrow arrow) {
		PotionContents contents = arrow.getPickupItemStackOrigin().get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return false;
		}
		return contents.potion()
				.flatMap(holder -> holder.unwrapKey().map(key -> key.identifier().getPath()))
				.map(path -> path.equals("healing") || path.equals("strong_healing"))
				.orElse(false);
	}
}
