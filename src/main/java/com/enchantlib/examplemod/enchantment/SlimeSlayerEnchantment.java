package com.enchantlib.examplemod.enchantment;

import static com.enchantlib.examplemod.ExampleModEnchantments.MOD_ID;
import static com.enchantlib.examplemod.ExampleModEnchantments.resolveEnchantment;

import com.enchantlib.api.EnchantmentBuilder;
import com.enchantlib.api.EnchantmentRegistrar;
import com.enchantlib.event.BuiltInEvents;
import com.enchantlib.event.EnchantmentContext;
import com.enchantlib.event.EnchantmentEventRegistrar;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 粘液杀手附魔(slime_slayer)。
 *
 * <p>剑/斧 V 级,对粘液类(史莱姆/岩浆怪/硫方怪)额外伤害 + 范围溅射。</p>
 */
public final class SlimeSlayerEnchantment {

	public static final String SLIME_SLAYER_ID = MOD_ID + ":slime_slayer";

	private SlimeSlayerEnchantment() {
	}

	public static void register(EnchantmentRegistrar registrar) {
		registrar.register(EnchantmentBuilder.create(SLIME_SLAYER_ID)
			.description("Slime Slayer")
			.supportedItems("#minecraft:enchantable/sharp_weapon")
			.weight(5).maxLevel(5)
			.minCost(5, 8).maxCost(25, 8).anvilCost(2)
			.slots("mainhand"));
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> slimeSlayer = resolveEnchantment(registries, SLIME_SLAYER_ID);
		registrar.register(slimeSlayer, BuiltInEvents.MODIFY_DAMAGE,
			SlimeSlayerEnchantment::onSlimeSlayerDamage);
		registrar.register(slimeSlayer, BuiltInEvents.POST_ATTACK,
			SlimeSlayerEnchantment::onSlimeSlayerPostAttack);
	}

	/**
	 * 粘液杀手 MODIFY_DAMAGE 回调:对粘液类目标(史莱姆/岩浆怪/硫方怪)造成额外伤害。
	 *
	 * <p>每级 +2.5 伤害(参考原版亡灵杀手/节肢杀手)。MC 26.2 中 Slime、MagmaCube、SulfurCube
	 * 均继承自 {@link AbstractCubeMob}(MagmaCube 不再继承 Slime),因此用 instanceof AbstractCubeMob
	 * 一次性覆盖全部三种粘液生物。</p>
	 */
	private static void onSlimeSlayerDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		if (target instanceof AbstractCubeMob) {
			float bonus = 2.5F * ctx.level();
			event.damage().add(bonus);
		}
	}

	/**
	 * 粘液杀手 POST_ATTACK 回调:攻击粘液类目标时,对半径 20 格内至多 {@code level + 1} 个
	 * 粘液生物(史莱姆/岩浆怪/硫方怪)同时造成额外伤害(范围溅射)。
	 *
	 * <p>目标数量:lvl1=2, lvl2=3, ..., lvl5=6。溅射伤害 = {@code 2.5 * level}(与直接命中加成一致)。
	 * 按距离排序,优先命中最近的;直接命中的目标不重复计算。</p>
	 */
	private static void onSlimeSlayerPostAttack(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		if (!(target instanceof AbstractCubeMob)) {
			return;
		}
		ServerLevel level = event.level();
		int maxTargets = ctx.level() + 1;
		float damage = 2.5F * ctx.level();
		net.minecraft.world.phys.AABB searchBox = target.getBoundingBox().inflate(20.0, 20.0, 20.0);
		List<AbstractCubeMob> nearby = level.getEntitiesOfClass(AbstractCubeMob.class, searchBox,
				e -> e != target && e.isAlive() && !e.isRemoved());
		nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(target)));
		int count = 0;
		for (AbstractCubeMob mob : nearby) {
			if (count >= maxTargets) {
				break;
			}
			if (event.attacker() instanceof net.minecraft.world.entity.player.Player player) {
				mob.hurt(level.damageSources().playerAttack(player), damage);
			} else {
				mob.hurt(level.damageSources().mobAttack(event.attacker()), damage);
			}
			count++;
		}
	}
}
