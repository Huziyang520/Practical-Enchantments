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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 回敬附魔(retribution)。
 *
 * <p>剑/斧 II 级,受击5秒内下次攻击附额外伤害。</p>
 */
public final class RetributionEnchantment {

	public static final String RETRIBUTION_ID = MOD_ID + ":retribution";

	private static final Identifier RETRIBUTION_LAST_HURT_TICK =
		Identifier.fromNamespaceAndPath(MOD_ID, "retribution_last_hurt_tick");

	private static Holder<Enchantment> retribution;

	private RetributionEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(RETRIBUTION_ID)
			.description("Retribution")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(3).maxLevel(2)
			.minCost(10, 10).maxCost(40, 10).anvilCost(4)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		retribution = resolveEnchantment(registries, RETRIBUTION_ID);

		ServerLivingEntityEvents.AFTER_DAMAGE.register(RetributionEnchantment::handleRetributionHurt);
		registrar.register(retribution, BuiltInEvents.MODIFY_DAMAGE,
			RetributionEnchantment::onRetributionDamage);
	}

	/**
	 * 回敬受击监听:玩家受击时,若主手持回敬附魔,记录当前 tick。
	 *
	 * <p>用全局 ServerLivingEntityEvents.AFTER_DAMAGE 而非 POST_HURT,
	 * 因为 POST_HURT 不扫描主手槽位,而回敬是主手剑附魔。</p>
	 */
	private static void handleRetributionHurt(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source,
											  float amount, float blockedDamage, boolean blocked) {
		if (!(entity instanceof ServerPlayer player)) {
			return;
		}
		ItemStack mainHand = player.getMainHandItem();
		if (mainHand.getEnchantments().getLevel(retribution) > 0) {
			int currentTick = (int) player.level().getGameTime();
			EntityCounter.set(player, RETRIBUTION_LAST_HURT_TICK, currentTick);
		}
	}

	/**
	 * 回敬伤害加成回调:玩家攻击时,若5秒内曾受击,加成本次伤害。
	 *
	 * <p>lvl1=+30%, lvl2=+50% 原伤害(bonus = 0.1 + 0.2 * level)。
	 * 加成后消耗标记,下次攻击需再次受击才能触发。</p>
	 *
	 * <p>注:严格"无视护甲"需自定义 DamageType,本实现 bonus 仍受护甲扣减影响。</p>
	 */
	private static void onRetributionDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		LivingEntity attacker = event.attacker();
		if (!(attacker instanceof ServerPlayer player)) {
			return;
		}
		int lastHurtTick = EntityCounter.get(player, RETRIBUTION_LAST_HURT_TICK);
		if (lastHurtTick <= 0) {
			return;
		}
		int currentTick = (int) player.level().getGameTime();
		if (currentTick - lastHurtTick > 100) {
			return;
		}
		float multiplier = 0.1F + 0.2F * ctx.level();
		event.damage().add(event.originalDamage() * multiplier);
		EntityCounter.set(player, RETRIBUTION_LAST_HURT_TICK, 0);
	}
}
