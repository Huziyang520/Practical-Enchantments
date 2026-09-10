package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.api.EntityCounter;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantLibEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import com.enchantlib.event.LivingEntityTickEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 焚心附魔(heartburn)。
 *
 * <p>剑 II 级,持续真实伤害 + 火焰粒子,与火焰附加互斥。</p>
 */
public final class HeartburnEnchantment {

	public static final String HEARTBURN_ID = MOD_ID + ":heartburn";

	/** 火焰武器互斥组:焚心 + 原版火焰附加(防止同把剑同时持有效果叠加) */
	public static final String FIRE_WEAPON_GROUP_REF = "#" + MOD_ID + ":exclusive_set/fire_weapon";

	private static final Identifier HEARTBURN_END_TICK =
		Identifier.fromNamespaceAndPath(MOD_ID, "heartburn_end_tick");
	private static final Identifier HEARTBURN_LEVEL =
		Identifier.fromNamespaceAndPath(MOD_ID, "heartburn_level");
	private static final Identifier HEARTBURN_LAST_DAMAGE_TICK =
		Identifier.fromNamespaceAndPath(MOD_ID, "heartburn_last_damage_tick");

	private HeartburnEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(HEARTBURN_ID)
			.description("Heartburn")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(2).maxLevel(2)
			.minCost(10, 10).maxCost(40, 10).anvilCost(4)
			.exclusiveSet(FIRE_WEAPON_GROUP_REF)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> heartburn = resolveEnchantment(registries, HEARTBURN_ID);

		registrar.register(heartburn, BuiltInEvents.POST_ATTACK,
			HeartburnEnchantment::onHeartburnMark);
		EnchantLibEvents.LIVING_ENTITY_TICK.register(HeartburnEnchantment::handleHeartburnTick);
	}

	/**
	 * 焚心标记回调:攻击后在目标身上设置/刷新焚心标记。
	 *
	 * <p>持续时长:lvl1=60 tick(3秒), lvl2=80 tick(4秒)。
	 * 标记包含结束 tick 和等级,存于 EntityCounter。再次攻击会刷新结束 tick(延长持续时间)。</p>
	 */
	private static void onHeartburnMark(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		int currentTick = event.level().getServer().getTickCount();
		int durationTicks = 40 + 20 * ctx.level();
		EntityCounter.set(target, HEARTBURN_END_TICK, currentTick + durationTicks);
		EntityCounter.set(target, HEARTBURN_LEVEL, ctx.level());
		if (EntityCounter.get(target, HEARTBURN_LAST_DAMAGE_TICK) == 0) {
			EntityCounter.set(target, HEARTBURN_LAST_DAMAGE_TICK, currentTick);
		}
	}

	/**
	 * 焚心持续伤害回调:每 tick 检查所有 LivingEntity 的焚心标记。
	 *
	 * <p>每 20 tick(1秒)造成一次伤害:lvl1=2% maxHP, lvl2=3% maxHP 真实伤害(无视护甲)。
	 * 标记过期后清除所有焚心计数器。</p>
	 */
	private static void handleHeartburnTick(LivingEntityTickEvent event) {
		LivingEntity entity = event.entity();
		int endTick = EntityCounter.get(entity, HEARTBURN_END_TICK);
		if (endTick <= 0) {
			return;
		}
		int currentTick = event.tickCount();
		if (currentTick >= endTick) {
			EntityCounter.set(entity, HEARTBURN_END_TICK, 0);
			EntityCounter.set(entity, HEARTBURN_LEVEL, 0);
			EntityCounter.set(entity, HEARTBURN_LAST_DAMAGE_TICK, 0);
			return;
		}
		int lastDamageTick = EntityCounter.get(entity, HEARTBURN_LAST_DAMAGE_TICK);
		if (currentTick - lastDamageTick >= 20) {
			int level = EntityCounter.get(entity, HEARTBURN_LEVEL);
			float maxHp = entity.getMaxHealth();
			float damage = maxHp * (0.01F + 0.01F * level);
			entity.hurt(entity.damageSources().magic(), damage);
			EntityCounter.set(entity, HEARTBURN_LAST_DAMAGE_TICK, currentTick);
			event.level().sendParticles(ParticleTypes.FLAME,
				entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
				8, 0.3, 0.4, 0.3, 0.02);
		}
	}
}
