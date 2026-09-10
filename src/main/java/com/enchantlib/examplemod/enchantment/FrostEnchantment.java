package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.LivingEntityTickEvent;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 冰霜附魔(frost)。
 *
 * <p>剑 II 级,攻击施加缓慢 + 旋转冰方块展示实体 + 雪花粒子。</p>
 */
public final class FrostEnchantment {

	public static final String FROST_ID = MOD_ID + ":frost";

	/** 每个目标生成的冰方块展示实体数量 */
	private static final int FROST_ICE_BLOCK_COUNT = 3;
	/** 目标 UUID → 冰霜视觉状态(展示实体列表 + 起止 tick) */
	private static final ConcurrentHashMap<UUID, IceVisual> FROST_VISUALS = new ConcurrentHashMap<>();

	private static final java.lang.reflect.Method DISPLAY_SET_TRANSFORMATION;
	private static final java.lang.reflect.Method DISPLAY_SET_INTERPOLATION_DURATION;
	private static final java.lang.reflect.Method BLOCK_DISPLAY_SET_BLOCK_STATE;

	private static Holder<MobEffect> slownessEffect;

	static {
		try {
			DISPLAY_SET_TRANSFORMATION = Display.class.getDeclaredMethod("setTransformation", Transformation.class);
			DISPLAY_SET_TRANSFORMATION.setAccessible(true);
			DISPLAY_SET_INTERPOLATION_DURATION = Display.class.getDeclaredMethod("setTransformationInterpolationDuration", int.class);
			DISPLAY_SET_INTERPOLATION_DURATION.setAccessible(true);
			BLOCK_DISPLAY_SET_BLOCK_STATE = Display.BlockDisplay.class.getDeclaredMethod("setBlockState", BlockState.class);
			BLOCK_DISPLAY_SET_BLOCK_STATE.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError("EnchantLib ExampleMod: 无法解析 Display API 方法: " + e.getMessage());
		}
	}

	private FrostEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(FROST_ID)
			.description("Frost")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(3).maxLevel(2)
			.minCost(8, 8).maxCost(30, 8).anvilCost(4)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> frost = resolveEnchantment(registries, FROST_ID);

		slownessEffect = registries.lookupOrThrow(Registries.MOB_EFFECT)
			.get(ResourceKey.create(Registries.MOB_EFFECT, Identifier.parse("minecraft:slowness")))
			.orElseThrow(() -> new IllegalStateException("EnchantLib ExampleMod: 缓慢效果未注册"));

		registrar.register(frost, BuiltInEvents.POST_ATTACK,
			FrostEnchantment::onFrostAttack);
		EnchantLibEvents.LIVING_ENTITY_TICK.register(FrostEnchantment::handleFrostVisualTick);
		// 死亡时清除冰块:死亡后 LIVING_ENTITY_TICK 不再触发,需显式监听避免冰方块残留
		ServerLivingEntityEvents.AFTER_DEATH.register(FrostEnchantment::onEntityDeath);
	}

	/** 反射调用 {@link Display.BlockDisplay#setBlockState(BlockState)}。 */
	private static void setDisplayBlockState(Display.BlockDisplay display, BlockState state) {
		try {
			BLOCK_DISPLAY_SET_BLOCK_STATE.invoke(display, state);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/** 反射调用 {@link Display#setTransformation(Transformation)}。 */
	private static void setDisplayTransformation(Display display, Transformation transformation) {
		try {
			DISPLAY_SET_TRANSFORMATION.invoke(display, transformation);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/** 反射调用 {@link Display#setTransformationInterpolationDuration(int)}。 */
	private static void setDisplayInterpolationDuration(Display display, int duration) {
		try {
			DISPLAY_SET_INTERPOLATION_DURATION.invoke(display, duration);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 单个冰方块展示实体的自转参数(每个冰块有独立的旋转轴、速度、缩放、微小偏移,
	 * 实现不规律自转效果)。
	 */
	private static final class IceDisplay {
		final Display.BlockDisplay display;
		/** 归一化的自转轴(每个冰块随机不同,产生不规律视觉) */
		final Vector3f axis;
		/** 自转角速度(弧度/tick,每个冰块随机不同) */
		final float speed;
		/** 相对目标身体中心的微小偏移(避免 3 块完全重叠导致 z-fighting) */
		final float ox;
		final float oy;
		final float oz;
		/** 缩放比例 */
		final float scale;

		IceDisplay(Display.BlockDisplay display, Vector3f axis, float speed,
				float ox, float oy, float oz, float scale) {
			this.display = display;
			this.axis = axis;
			this.speed = speed;
			this.ox = ox;
			this.oy = oy;
			this.oz = oz;
			this.scale = scale;
		}
	}

	/**
	 * 冰霜视觉状态:跟踪一个目标身上的多个冰方块展示实体。
	 *
	 * <p>每 tick 由 {@link #handleFrostVisualTick} 更新位置(跟随目标身体中心)与
	 * 自转(每块绕各自随机轴旋转),到期后统一 discard 所有展示实体。</p>
	 */
	private static final class IceVisual {
		final List<IceDisplay> displays;
		final int startTick;
		final int endTick;

		IceVisual(List<IceDisplay> displays, int startTick, int endTick) {
			this.displays = displays;
			this.startTick = startTick;
			this.endTick = endTick;
		}
	}

	/**
	 * 冰霜附魔回调:攻击后对目标施加缓慢效果、在目标身体中心原点生成 3 个缩小的冰方块展示实体,
	 * 每块绕各自随机轴不规律自转,并发送雪花粒子。
	 *
	 * <p>缓慢时长:60 tick(3秒) + 20 * level;缓慢等级:level( lvl1=缓慢I, lvl2=缓慢II)。
	 * 冰方块视觉:在目标身体中心生成 {@value #FROST_ICE_BLOCK_COUNT} 个冰方块展示实体
	 * ({@link Display.BlockDisplay},方块状态 = {@link Blocks#ICE}),全部位于身体中心原点
	 * (仅微小偏移避免 z-fighting),每块有独立的随机自转轴 + 随机角速度,产生不规律自转效果。
	 * duration 结束后展示实体自动 discard。
	 * 粒子:在目标位置发送 10 个雪花粒子。</p>
	 */
	private static void onFrostAttack(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		int duration = 60 + 20 * ctx.level();
		int amplifier = ctx.level() - 1;
		target.addEffect(new MobEffectInstance(slownessEffect, duration, amplifier));

		ServerLevel serverLevel = event.level();

		IceVisual old = FROST_VISUALS.remove(target.getUUID());
		if (old != null) {
			for (IceDisplay oldIce : old.displays) {
				if (!oldIce.display.isRemoved()) {
					oldIce.display.discard();
				}
			}
		}

		BlockState iceState = Blocks.ICE.defaultBlockState();
		double centerY = target.getY() + target.getBbHeight() * 0.5;
		double cx = target.getX();
		double cz = target.getZ();
		java.util.concurrent.ThreadLocalRandom rng = java.util.concurrent.ThreadLocalRandom.current();
		List<IceDisplay> iceDisplays = new ArrayList<>(FROST_ICE_BLOCK_COUNT);
		for (int i = 0; i < FROST_ICE_BLOCK_COUNT; i++) {
			Vector3f axis = new Vector3f(
				rng.nextFloat() * 2.0F - 1.0F,
				rng.nextFloat() * 2.0F - 1.0F,
				rng.nextFloat() * 2.0F - 1.0F).normalize();
			float speed = 0.12F + rng.nextFloat() * 0.16F;
			float scale = 0.30F + rng.nextFloat() * 0.12F;
			float ox = (rng.nextFloat() * 2.0F - 1.0F) * 0.06F;
			float oy = (rng.nextFloat() * 2.0F - 1.0F) * 0.06F;
			float oz = (rng.nextFloat() * 2.0F - 1.0F) * 0.06F;

			Display.BlockDisplay display = new Display.BlockDisplay(EntityTypes.BLOCK_DISPLAY, serverLevel);
			display.setPos(cx + ox, centerY + oy, cz + oz);
			setDisplayBlockState(display, iceState);
			setDisplayTransformation(display, new Transformation(
				new Vector3f(0.0F, 0.0F, 0.0F),
				new Quaternionf(),
				new Vector3f(scale, scale, scale),
				new Quaternionf()));
			setDisplayInterpolationDuration(display, 2);
			serverLevel.addFreshEntity(display);
			iceDisplays.add(new IceDisplay(display, axis, speed, ox, oy, oz, scale));
		}
		int currentTick = serverLevel.getServer().getTickCount();
		FROST_VISUALS.put(target.getUUID(), new IceVisual(iceDisplays, currentTick, currentTick + duration));

		serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
			target.getX(), target.getY() + 1.0, target.getZ(),
			10, 0.3, 0.5, 0.3, 0.05);
	}

	/**
	 * 冰霜视觉 tick 回调:每 tick 更新冰方块展示实体的位置(跟随目标身体中心原点)与
	 * 自转(每块绕各自随机轴旋转),duration 到期或目标移除后 discard 所有展示实体。
	 *
	 * <p>此回调注册到全局 {@link EnchantLibEvents#LIVING_ENTITY_TICK},对所有 LivingEntity 触发,
	 * 但通过 {@link #FROST_VISUALS} 快速过滤无视觉的目标(未命中则立即返回)。</p>
	 */
	private static void handleFrostVisualTick(LivingEntityTickEvent event) {
		LivingEntity entity = event.entity();
		IceVisual visual = FROST_VISUALS.get(entity.getUUID());
		if (visual == null) {
			return;
		}
		int currentTick = event.tickCount();
		if (currentTick >= visual.endTick || entity.isRemoved()) {
			for (IceDisplay ice : visual.displays) {
				if (!ice.display.isRemoved()) {
					ice.display.discard();
				}
			}
			FROST_VISUALS.remove(entity.getUUID());
			return;
		}
		double centerY = entity.getY() + entity.getBbHeight() * 0.5;
		double cx = entity.getX();
		double cz = entity.getZ();
		int elapsed = currentTick - visual.startTick;
		for (IceDisplay ice : visual.displays) {
			Display.BlockDisplay d = ice.display;
			if (d.isRemoved()) {
				continue;
			}
			d.setPos(cx + ice.ox, centerY + ice.oy, cz + ice.oz);
			float rotAngle = elapsed * ice.speed;
			Quaternionf rot = new Quaternionf().rotateAxis(rotAngle, ice.axis);
			Vector3f scaleVec = new Vector3f(ice.scale, ice.scale, ice.scale);
			setDisplayTransformation(d, new Transformation(
			new Vector3f(0.0F, 0.0F, 0.0F), rot, scaleVec, new Quaternionf()));
		}
	}

	/**
	 * 实体死亡时清除其身上的冰霜视觉。死亡后 {@link EnchantLibEvents#LIVING_ENTITY_TICK}
	 * 不再为该实体触发,需在此显式清除避免冰方块展示实体残留。
	 */
	private static void onEntityDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
		IceVisual visual = FROST_VISUALS.remove(entity.getUUID());
		if (visual != null) {
			for (IceDisplay ice : visual.displays) {
				if (!ice.display.isRemoved()) {
					ice.display.discard();
				}
			}
		}
	}
}
