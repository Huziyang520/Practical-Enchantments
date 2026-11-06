// 创伤附魔 - 剑/斧/矛 Ⅱ 级：命中叠流血层数，矛多一层。

package com.practicalenchantments.enchantment.weapon;

import com.practicalenchantments.config.Tunables;
import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.content.BleedContent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class TraumaEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":trauma";
	private static final TagKey<Item> SPEARS =
			TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("spears"));

	private TraumaEnchantment() {
	}

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.MODIFY_DAMAGE, TraumaEnchantment::onHit);
	}

	private static void onHit(BuiltInEvents.ModifyDamageEvent event, EnchantmentContext ctx) {
		int layers = ctx.level();
		if (ctx.itemStack().is(SPEARS)) {
			layers += Tunables.traumaSpearBonusLayers();
		}
		BleedContent.add(event.target(), layers);
	}
}
