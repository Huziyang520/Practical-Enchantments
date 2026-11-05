// 腐毒效果：逐层独立计时，每 0.6 秒扣血并额外磨损护甲。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.effect.MerlinMobEffect;
import com.practicalenchantments.support.EffectLayers;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RotContent {
	public static final String NAMESPACE = "practical_enchantments";
	public static final ResourceLocation EFFECT_ID = new ResourceLocation(NAMESPACE, "rot");
	public static final int LAYER_TICKS = 500;
	public static final int DAMAGE_INTERVAL = 12;
	public static final float DAMAGE_PER_LAYER = 0.25F;
	public static final int COLOR = 0x4C8C2B;
	private static final EquipmentSlot[] ARMOR = {
			EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
	};
	private static final Map<UUID, Integer> TICKS = new ConcurrentHashMap<>();
	private static MerlinMobEffect effect;

	private RotContent() {
	}

	public static void register() {
		effect = MerlinApi.effects().register(EFFECT_ID)
				.category(MobEffectCategory.HARMFUL)
				.color(COLOR)
				.onTick(RotContent::onTick)
				.submit();
	}

	public static int layers(LivingEntity entity) {
		return EffectLayers.count(entity);
	}

	public static void add(LivingEntity target, int count) {
		if (effect == null || target == null || count <= 0 || target.level().isClientSide()) {
			return;
		}
		for (int i = 0; i < count; i++) {
			EffectLayers.push(target, LAYER_TICKS);
		}
		target.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, 0, false, true, true));
	}

	private static boolean onTick(ServerLevel level, LivingEntity entity, int amplifier) {
		if (entity.isDeadOrDying()) {
			EffectLayers.clear(entity);
			return false;
		}
		int layers = EffectLayers.tickDown(entity);
		UUID id = entity.getUUID();
		if (layers <= 0) {
			TICKS.remove(id);
			return false;
		}
		if (TICKS.merge(id, 1, Integer::sum) >= DAMAGE_INTERVAL) {
			TICKS.put(id, 0);
			if (entity.getHealth() > 1.0F) {
				hurt(level, entity, DAMAGE_PER_LAYER * layers);
			}
			wearArmor(entity, layers);
			chunks(level, entity, Math.min(6, 1 + layers));
		}
		return true;
	}

	private static void wearArmor(LivingEntity entity, int layers) {
		int extra = layers / 10;
		int bonus = entity.getRandom().nextFloat() * 10.0F < (layers % 10) ? 1 : 0;
		int total = extra + bonus;
		if (total <= 0) {
			return;
		}
		EquipmentSlot slot = ARMOR[entity.getRandom().nextInt(ARMOR.length)];
		ItemStack armor = entity.getItemBySlot(slot);
		if (!armor.isEmpty() && armor.isDamageableItem()) {
			armor.hurtAndBreak(total, entity, worn -> worn.broadcastBreakEvent(slot));
		}
	}

	private static void chunks(ServerLevel level, LivingEntity entity, int count) {
		BlockState state = Blocks.MOSS_BLOCK.defaultBlockState();
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
				entity.getX(), entity.getY() + entity.getBbHeight() * 0.6D, entity.getZ(),
				count, entity.getBbWidth() * 0.45D, entity.getBbHeight() * 0.35D, entity.getBbWidth() * 0.45D, 0.3D);
	}

	private static void hurt(ServerLevel level, LivingEntity entity, float amount) {
		if (amount <= 0.0F || entity.isDeadOrDying()) {
			return;
		}
		boolean silent = entity.isSilent();
		entity.setSilent(true);
		try {
			entity.invulnerableTime = 0;
			entity.hurt(source(level), amount);
		} finally {
			entity.setSilent(silent);
		}
	}

	private static DamageSource source(ServerLevel level) {
		return new DamageSource(level.registryAccess()
				.registryOrThrow(Registries.DAMAGE_TYPE)
				.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
						new ResourceLocation(NAMESPACE, "rot"))));
	}
}
