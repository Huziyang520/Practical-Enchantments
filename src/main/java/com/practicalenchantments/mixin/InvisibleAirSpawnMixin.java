// 风王结界只从怪物手里的剑来：生物生成时给一把没附魔的剑小概率挂上。

package com.practicalenchantments.mixin;

import com.practicalenchantments.enchantment.InvisibleAirEnchantment;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class InvisibleAirSpawnMixin {
	private static final float SPAWN_CHANCE = 0.02F;

	@Inject(method = "enchantSpawnedWeapon", at = @At("TAIL"), require = 1)
	private void practicalenchantments$grantInvisibleAir(RandomSource random, float chanceMultiplier, CallbackInfo info) {
		Mob self = (Mob) (Object) this;
		ItemStack weapon = self.getMainHandItem();
		if (weapon.isEmpty() || !weapon.is(ItemTags.SWORDS) || !weapon.getEnchantmentTags().isEmpty()) {
			return;
		}
		if (random.nextFloat() >= SPAWN_CHANCE) {
			return;
		}
		weapon.enchant(InvisibleAirEnchantment.enchantment(), 1);
	}
}
