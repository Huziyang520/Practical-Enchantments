package com.practicalenchantments.enchantment.linked;

import com.huziyang520.merlinlib.api.LootInjectionBuilder;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.huziyang520.merlinlib.content.Acquisition;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 飞行杀手：以投射物命中空中目标时追加 {@code 1 + 等级} 伤害，并从二级起附带减速。
 */
public final class AetherSkySlayer {

	public static final String PATH = "aether_sky_slayer";
	public static final String ID = PracticalEnchantments.MOD_ID + ":" + PATH;

	/** 减速持续 5 秒。 */
	private static final int SLOW_TICKS = 100;

	/** 天境战利品箱候选表，命名空间用实际加载的那个模组。 */
	private static final String[] LOOT_PATHS = {
			"chests/dungeon",
			"chests/bronze_dungeon",
			"chests/silver_dungeon",
	};

	private AetherSkySlayer() {
	}

	static void registerContent(String namespace) {
		MerlinApi.enchantments().register(Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, PATH))
				.maxLevel(5)
				.weight(2)
				.anvilCost(8)
				.slots("mainhand")
				.supportedItems("#minecraft:enchantable/bow")
				.cost(10, 8, 40, 10)
				.acquisition(Acquisition.TREASURE_ONLY)
				.submit();
		for (String path : LOOT_PATHS) {
			MerlinApi.loot().register(LootInjectionBuilder.create()
					.toTables(namespace + ":" + path)
					.asBook()
					.withEnchantments(ID)
					.chance(0.12F)
					.weight(1)
					.quality(2)
					.allowModdedTables());
		}
	}

	static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, AetherSkySlayer::onDamage);
	}

	private static void onDamage(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		if (!(event.source().getDirectEntity() instanceof Projectile)) {
			return;
		}
		LivingEntity target = event.target();
		if (target.onGround()) {
			return;
		}
		event.damage().add(1.0F + ctx.level());
		if (ctx.level() >= 2) {
			target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, SLOW_TICKS, ctx.level() - 2));
		}
	}
}
