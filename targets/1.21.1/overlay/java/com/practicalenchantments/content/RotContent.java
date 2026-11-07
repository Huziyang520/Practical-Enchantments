// 腐毒效果：层数交给 MerlinLib 的 LayeredEffect，每 0.6 秒扣血并额外磨损护甲。

package com.practicalenchantments.content;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.effect.LayeredEffect;
import com.huziyang520.merlinlib.effect.MerlinMobEffect;
import com.practicalenchantments.support.ImmunityReset;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public final class RotContent {
	public static final String NAMESPACE = "practical_enchantments";
	public static final ResourceLocation EFFECT_ID = ResourceLocation.fromNamespaceAndPath(NAMESPACE, "rot");
	public static final int COLOR = 0x4C8C2B;
	private static final EquipmentSlot[] ARMOR = {
			EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
	};

	private static LayeredEffect LAYERS;
	private static MerlinMobEffect effect;

	private RotContent() {
	}

	public static void register() {
		effect = MerlinApi.effects().register(EFFECT_ID)
				.category(MobEffectCategory.HARMFUL)
				.color(COLOR)
				.onTick(RotContent::onTick)
				.submit();
		LAYERS = new LayeredEffect(Tunables.rotLayerTicks(), 999);
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
			LAYERS.push(target);
			LAYERS.syncDisplay(target, holder);
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
		if (layers <= 0) {
			scheduleDisplay(level, entity, holder);
			return false;
		}
		if (entity.tickCount % Tunables.rotDamageIntervalTicks() == 0) {
			// 每层各自结算：各自的伤害、各自的护甲磨损，磨损量再按当前层数叠加一次
			for (int layer = 0; layer < layers && !entity.isDeadOrDying(); layer++) {
				if (entity.getHealth() > 1.0F) {
					hurt(level, entity, (float) Tunables.rotDamagePerLayer());
				}
				wearArmor(entity, layers);
			}
			chunks(level, entity, Math.min(6, 1 + layers));
			rotSound(level, entity);
		}
		scheduleDisplay(level, entity, holder);
		return true;
	}

	private static void wearArmor(LivingEntity entity, int layers) {
		int extra = layers * Tunables.rotArmorWearPer10Layers() / 10;
		int bonus = entity.getRandom().nextFloat() * 10.0F < (layers % 10) ? 1 : 0;
		int total = extra + bonus;
		if (total <= 0) {
			return;
		}
		EquipmentSlot slot = ARMOR[entity.getRandom().nextInt(ARMOR.length)];
		ItemStack armor = entity.getItemBySlot(slot);
		if (!armor.isEmpty() && armor.isDamageableItem()) {
			armor.hurtAndBreak(total, entity, slot);
		}
	}

	private static void chunks(ServerLevel level, LivingEntity entity, int count) {
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MOSS_BLOCK.defaultBlockState()),
				entity.getX(), entity.getY() + entity.getBbHeight() * 0.6D, entity.getZ(),
				count, entity.getBbWidth() * 0.45D, entity.getBbHeight() * 0.35D, entity.getBbWidth() * 0.45D, 0.3D);
	}

	private static void rotSound(ServerLevel level, LivingEntity entity) {
		level.playSound(null, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
				SoundEvents.SLIME_BLOCK_STEP, SoundSource.PLAYERS, 0.18F,
				0.5F + entity.getRandom().nextFloat() * 0.2F);
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
		int want = Math.max(1, Math.min(255, instance.getAmplifier() + 1));
		int longest = LAYERS.longest(entity);
		boolean freshCast = layers <= 0
				? instance.getDuration() > Tunables.rotLayerTicks() || instance.getAmplifier() > 0
				: instance.getDuration() > longest + 5;
		if (!freshCast) {
			return layers;
		}
		for (int i = 0; i < want; i++) {
			LAYERS.push(entity, instance.getDuration());
		}
		return LAYERS.count(entity);
	}

	private static void hurt(ServerLevel level, LivingEntity entity, float amount) {
		if (amount <= 0.0F || entity.isDeadOrDying()) {
			return;
		}
		ImmunityReset.zero(entity);
		entity.hurt(source(level), amount);
	}

	private static DamageSource source(ServerLevel level) {
		Holder<DamageType> type = level.registryAccess()
				.lookupOrThrow(Registries.DAMAGE_TYPE)
				.getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
						ResourceLocation.fromNamespaceAndPath(NAMESPACE, "rot")));
		return new DamageSource(type);
	}
}
