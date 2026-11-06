// 流血效果：层数交给 MerlinLib 的 LayeredEffect，每层每 tick 0.02 点，满 5 层爆开。

package com.practicalenchantments.content;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.effect.LayeredEffect;
import com.huziyang520.merlinlib.effect.MerlinMobEffect;
import com.huziyang520.merlinlib.particle.ParticleApi;
import com.practicalenchantments.support.ImmunityReset;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class BleedContent {
	public static final String NAMESPACE = "practical_enchantments";
	public static final Identifier EFFECT_ID = Identifier.fromNamespaceAndPath(NAMESPACE, "bleed");
	/** 每 10 tick 结算一次：总伤害不变，但目标不会每 tick 抽一下。 */
	public static final int COLOR = 0x8B0000;
	public static final String BURST_PROFILE = "blood_burst";

	private static LayeredEffect LAYERS;
	private static MerlinMobEffect effect;

	private BleedContent() {
	}

	public static void register() {
		effect = MerlinApi.effects().register(EFFECT_ID)
				.category(MobEffectCategory.HARMFUL)
				.color(COLOR)
				.onTick(BleedContent::onTick)
				.submit();
		LAYERS = new LayeredEffect(Tunables.bleedLayerTicks(), Tunables.bleedMaxLayers());
				LayeredEffect.register(EFFECT_ID.toString(), LAYERS);
		LAYERS.bindDisplay(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect));
	}

	public static int layers(LivingEntity entity) {
		return LAYERS.count(entity);
	}

	public static void add(LivingEntity target, int count) {
		if (effect == null || target == null || count <= 0 || target.level().isClientSide()) {
			return;
		}
		Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
		for (int i = 0; i < count; i++) {
			boolean overflowed = LAYERS.push(target);
			LAYERS.syncDisplay(target, holder);
			if (overflowed && target.level() instanceof ServerLevel level) {
				explode(level, target);
			}
		}
	}

	private static boolean onTick(ServerLevel level, LivingEntity entity, int amplifier) {
		if (entity.isDeadOrDying()) {
			LAYERS.clear(entity);
			return false;
		}
		Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
		int layers = LAYERS.tickDown(entity, 1);
		layers = syncFromCommand(level, entity, holder, layers);
		// 任何途径堆过 5 层都直接爆，不只在武器命中时
		while (layers > Tunables.bleedMaxLayers()) {
			explode(level, entity);
			layers = LAYERS.count(entity);
		}
		if (layers <= 0) {
			scheduleDisplay(level, entity, holder);
			return false;
		}
		if (entity.tickCount % Tunables.bleedPulseTicks() == 0) {
			// 每层各扣 0.02/tick，逐层累加后一次结算
			float total = 0.0F;
			for (int layer = 0; layer < layers; layer++) {
				total += Tunables.bleedDamagePerLayerTick() * Tunables.bleedPulseTicks();
			}
			hurt(level, entity, total);
			bleedSound(level, entity);
			chunks(level, entity, Blocks.REDSTONE_BLOCK.defaultBlockState(), Math.min(3, layers), 0.3D);
		}
		scheduleDisplay(level, entity, holder);
		return true;
	}

	/** 满层爆开：这几层剩下的伤害一次结清，粒子和音效都比平时重。 */
	private static void explode(ServerLevel level, LivingEntity entity) {
		int remaining = LAYERS.drain(entity, Tunables.bleedMaxLayers());
		if (remaining <= 0) {
			return;
		}
		double burstY = entity.getY() + entity.getBbHeight() * 0.55D;
		// 血块走数据包定义的粒子档案（颜色、大小、块类型都在 data/.../merlin/particles 里）
		MerlinApi.particles().burst(level, ParticleApi.profileId(NAMESPACE, BURST_PROFILE),
				entity.getX(), burstY, entity.getZ(), 0.95D);
		chunks(level, entity, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 10, 1.0D);
		level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				SoundEvents.SLIME_BLOCK_BREAK, SoundSource.PLAYERS, 1.0F, 0.45F);
		level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				SoundEvents.HONEY_BLOCK_BREAK, SoundSource.PLAYERS, 0.85F, 0.4F);
		hurt(level, entity, remaining * Tunables.bleedDamagePerLayerTick());
	}

	private static void bleedSound(ServerLevel level, LivingEntity entity) {
		level.playSound(null, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
				SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.PLAYERS, 0.16F,
				0.45F + entity.getRandom().nextFloat() * 0.2F);
	}

	private static void chunks(ServerLevel level, LivingEntity entity, BlockState state, int count, double speed) {
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
				entity.getX(), entity.getY() + entity.getBbHeight() * 0.6D, entity.getZ(),
				count, entity.getBbWidth() * 0.45D, entity.getBbHeight() * 0.35D, entity.getBbWidth() * 0.45D, speed);
	}

	private static void scheduleDisplay(ServerLevel level, LivingEntity entity, Holder<MobEffect> holder) {
		// 效果实例不能在 applyEffectTick 里增删（那次迭代还在跑），推到本 tick 的末尾再做
		level.getServer().execute(() -> LAYERS.syncDisplay(entity, holder));
	}

	/**
	 * /effect 施加的实例没有层记录：按 amplifier 补层，重复施加（时长被刷新）时继续叠加，
	 * 这样指令也能测血爆。
	 */
	private static int syncFromCommand(ServerLevel level, LivingEntity entity, Holder<MobEffect> holder, int layers) {
		MobEffectInstance instance = entity.getEffect(holder);
		if (instance == null) {
			return layers;
		}
		int want = Math.max(1, Math.min(Tunables.bleedMaxLayers(), instance.getAmplifier() + 1));
		int longest = LAYERS.longest(entity);
		boolean freshCast = layers <= 0
				? instance.getDuration() > Tunables.bleedLayerTicks() || instance.getAmplifier() > 0
				: instance.getDuration() > longest + 5;
		if (!freshCast) {
			return layers;
		}
		// 已经满层时再施加一次 = 又叠了一层，直接爆
		int push = layers >= Tunables.bleedMaxLayers() ? 1 : want;
		for (int i = 0; i < push; i++) {
			boolean overflowed = LAYERS.push(entity, instance.getDuration());
			if (overflowed && level != null) {
				explode(level, entity);
			}
		}
		return LAYERS.count(entity);
	}

	private static void hurt(ServerLevel level, LivingEntity entity, float amount) {
		if (amount <= 0.0F || entity.isDeadOrDying()) {
			return;
		}
		ImmunityReset.zero(entity);
		entity.hurtServer(level, source(level), amount);
	}

	private static DamageSource source(ServerLevel level) {
		Holder<DamageType> type = level.registryAccess()
				.lookupOrThrow(Registries.DAMAGE_TYPE)
				.getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
						Identifier.fromNamespaceAndPath(NAMESPACE, "bleed")));
		return new DamageSource(type);
	}
}
