// 流血效果：层数存于 EffectLayers，每层每 tick 结算 0.02 点，达 5 层引爆。

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class BleedContent {
	public static final String NAMESPACE = "practical_enchantments";
	public static final ResourceLocation EFFECT_ID = new ResourceLocation(NAMESPACE, "bleed");
	public static final int LAYER_TICKS = 100;
	public static final int MAX_LAYERS = 5;
	public static final float DAMAGE_PER_LAYER_TICK = 0.02F;
	public static final float DAMAGE_PER_LAYER = LAYER_TICKS * DAMAGE_PER_LAYER_TICK;
	public static final int COLOR = 0x8B0000;

	private static MerlinMobEffect effect;

	private BleedContent() {
	}

	public static void register() {
		effect = MerlinApi.effects().register(EFFECT_ID)
				.category(MobEffectCategory.HARMFUL)
				.color(COLOR)
				.onTick(BleedContent::onTick)
				.submit();
	}

	public static int layers(LivingEntity entity) {
		return EffectLayers.count(entity);
	}

	public static void add(LivingEntity target, int count) {
		if (effect == null || target == null || count <= 0 || target.level().isClientSide()) {
			return;
		}
		if (!(target.level() instanceof ServerLevel level)) {
			return;
		}
		for (int i = 0; i < count; i++) {
			EffectLayers.push(target, LAYER_TICKS);
			if (EffectLayers.count(target) >= MAX_LAYERS) {
				explode(level, target);
			}
			if (EffectLayers.count(target) > 0) {
				target.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, 0, false, true, true));
			} else {
				target.removeEffect(effect);
			}
		}
	}

	private static boolean onTick(ServerLevel level, LivingEntity entity, int amplifier) {
		if (entity.isDeadOrDying()) {
			EffectLayers.clear(entity);
			return false;
		}
		int layers = EffectLayers.tickDown(entity);
		if (layers <= 0) {
			return false;
		}
		hurt(level, entity, DAMAGE_PER_LAYER_TICK * layers);
		if ((entity.tickCount & 3) == 0) {
			chunks(level, entity, Blocks.REDSTONE_BLOCK.defaultBlockState(), Math.min(3, layers), 0.35D);
		}
		return true;
	}

	private static void explode(ServerLevel level, LivingEntity entity) {
		int remaining = EffectLayers.drainFirst(entity, MAX_LAYERS);
		if (remaining <= 0) {
			return;
		}
		chunks(level, entity, Blocks.REDSTONE_BLOCK.defaultBlockState(), 10, 0.85D);
		chunks(level, entity, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 10, 0.85D);
		hurt(level, entity, remaining * DAMAGE_PER_LAYER_TICK);
	}

	private static void chunks(ServerLevel level, LivingEntity entity, BlockState state, int count, double speed) {
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
				entity.getX(), entity.getY() + entity.getBbHeight() * 0.6D, entity.getZ(),
				count, entity.getBbWidth() * 0.45D, entity.getBbHeight() * 0.35D, entity.getBbWidth() * 0.45D, speed);
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
						new ResourceLocation(NAMESPACE, "bleed"))));
	}
}
