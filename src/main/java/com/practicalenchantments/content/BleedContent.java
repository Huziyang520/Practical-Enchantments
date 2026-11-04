// 流血效果：层数存于 EffectLayers，效果实例只当开关与 tick 源。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.effect.MerlinMobEffect;
import com.practicalenchantments.support.EffectLayers;
import com.practicalenchantments.support.Particles;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BleedContent {
	public static final String NAMESPACE = "practical_enchantments";
	public static final ResourceLocation EFFECT_ID = ResourceLocation.fromNamespaceAndPath(NAMESPACE, "bleed");
	public static final int LAYER_TICKS = 100;
	public static final int MAX_LAYERS = 5;
	public static final float DAMAGE_PER_LAYER_TICK = 0.02F;
	public static final float DAMAGE_PER_LAYER = LAYER_TICKS * DAMAGE_PER_LAYER_TICK;
	public static final int COLOR = 0x8B0000;
	private static final int BURST_COLOR = 0xB40000;
	private static final int PACKET_TICKS = 20;

	private static final Map<UUID, Account> ACCOUNTS = new ConcurrentHashMap<>();
	private static MerlinMobEffect effect;

	private BleedContent() {
	}

	private static final class Account {
		private float pending;
		private int ticks;
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
				refresh(target);
			} else {
				target.removeEffect(effect);
			}
		}
	}

	private static void refresh(LivingEntity target) {
		int duration = EffectLayers.maxRemaining(target) + 40;
		target.addEffect(new MobEffectInstance(effect, duration, 0, false, false, false));
	}

	private static boolean onTick(ServerLevel level, LivingEntity entity, int amplifier) {
		int layers = EffectLayers.tickDown(entity);
		UUID id = entity.getUUID();
		if (layers <= 0) {
			Account done = ACCOUNTS.remove(id);
			if (done != null && done.pending > 0.01F) {
				entity.hurt(source(level), done.pending);
			}
			return false;
		}
		Account account = ACCOUNTS.computeIfAbsent(id, key -> new Account());
		account.pending += DAMAGE_PER_LAYER_TICK * layers;
		if (++account.ticks >= PACKET_TICKS) {
			float amount = account.pending;
			account.pending = 0.0F;
			account.ticks = 0;
			entity.hurt(source(level), amount);
		}
		if ((entity.tickCount & 3) == 0) {
			bloodParticles(level, entity, COLOR, 2, 0.8F);
		}
		return true;
	}

	private static void explode(ServerLevel level, LivingEntity entity) {
		int remaining = EffectLayers.drainFirst(entity, MAX_LAYERS);
		if (remaining <= 0) {
			return;
		}
		float damage = remaining * DAMAGE_PER_LAYER_TICK;
		bloodParticles(level, entity, BURST_COLOR, 18, 1.6F);
		bloodParticles(level, entity, COLOR, 10, 1.2F);
		entity.hurt(source(level), damage);
	}

	private static void bloodParticles(ServerLevel level, LivingEntity entity, int color, int count, float scale) {
		level.sendParticles(Particles.dust(color, scale),
				entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
				count, entity.getBbWidth() * 0.4D, entity.getBbHeight() * 0.3D, entity.getBbWidth() * 0.4D, 0.02D);
	}

	private static DamageSource source(ServerLevel level) {
		return new DamageSource(level.registryAccess()
				.registryOrThrow(Registries.DAMAGE_TYPE)
				.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
						ResourceLocation.fromNamespaceAndPath(NAMESPACE, "bleed"))));
	}
}
