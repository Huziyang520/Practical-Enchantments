package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.EntityCounter;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 音痕附魔(echo_mark)。
 *
 * <p>弓 II 级,箭命中留标记,受伤害引爆造成已损失生命50%伤害 + 爆炸音效。</p>
 */
public final class EchoMarkEnchantment {

	public static final String ECHO_MARK_ID = MOD_ID + ":echo_mark";

	private static final Identifier ECHO_MARK_END_TICK =
		Identifier.fromNamespaceAndPath(MOD_ID, "echo_mark_end_tick");
	private static final Identifier ECHO_MARK_LEVEL =
		Identifier.fromNamespaceAndPath(MOD_ID, "echo_mark_level");

	private EchoMarkEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(ECHO_MARK_ID)
			.description("Echo Mark")
			.supportedItems("#minecraft:enchantable/bow")
			.weight(3).maxLevel(2)
			.minCost(8, 8).maxCost(30, 8).anvilCost(4)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> echoMark = resolveEnchantment(registries, ECHO_MARK_ID);

		registrar.register(echoMark, BuiltInEvents.PROJECTILE_HIT,
			EchoMarkEnchantment::onEchoMarkHit);
		ServerLivingEntityEvents.AFTER_DAMAGE.register(EchoMarkEnchantment::handleEchoMarkTrigger);
	}

	/**
	 * 音痕标记回调:箭命中后在目标身上设置4秒标记。
	 *
	 * <p>标记包含结束 tick 和等级,存于 EntityCounter。</p>
	 */
	private static void onEchoMarkHit(BuiltInEvents.ProjectileHitEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		int currentTick = (int) target.level().getGameTime();
		int durationTicks = 80;
		EntityCounter.set(target, ECHO_MARK_END_TICK, currentTick + durationTicks);
		EntityCounter.set(target, ECHO_MARK_LEVEL, ctx.level());
	}

	/**
	 * 音痕引爆回调:监听任意实体受击,若目标有音痕标记(未过期),引爆额外伤害。
	 *
	 * <p>引爆伤害 = 目标已损失生命值 × 50%(无视护甲,magic 类型近似)。
	 * 引爆后清除标记(一次性)。</p>
	 *
	 * <p>注:为避免递归(引爆的 hurt 再次触发本回调),先清除标记再造成伤害。</p>
	 */
	private static void handleEchoMarkTrigger(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source,
											  float amount, float blockedDamage, boolean blocked) {
		int endTick = EntityCounter.get(entity, ECHO_MARK_END_TICK);
		if (endTick <= 0) {
			return;
		}
		int currentTick = (int) entity.level().getGameTime();
		if (currentTick >= endTick) {
			EntityCounter.set(entity, ECHO_MARK_END_TICK, 0);
			EntityCounter.set(entity, ECHO_MARK_LEVEL, 0);
			return;
		}
		EntityCounter.set(entity, ECHO_MARK_END_TICK, 0);
		EntityCounter.set(entity, ECHO_MARK_LEVEL, 0);
		float maxHp = entity.getMaxHealth();
		float currentHp = entity.getHealth();
		float lostHp = maxHp - currentHp;
		float bonus = lostHp * 0.5F;
		if (bonus > 0) {
			entity.hurt(entity.damageSources().magic(), bonus);
		}
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
			SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.8F, 1.5F);
	}
}
