// 进化规则表：目前只有「引雷 → 宙斯与波塞冬」一条。
// 成本按原附魔等级缩放（4 级 + 8，上限 64），时长按成本（20 tick/个，上限 16 秒）。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.EvolutionRule;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

public final class PeEvolution {
	public static final Identifier CHANNELING = Identifier.withDefaultNamespace("channeling");
	public static final Identifier ZEUS_POSEIDON =
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "zeus_poseidon");
	public static final int MAX_COST = 64;
	public static final int MAX_TICKS = 320;
	public static final EvolutionRule CHANNELING_TO_ZEUS_POSEIDON =
			new EvolutionRule(CHANNELING, ZEUS_POSEIDON, 0.75F, 0.05F, 12, 240);
	private static boolean registered;

	private PeEvolution() {
	}

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		MerlinApi.evolution().register(CHANNELING_TO_ZEUS_POSEIDON);
		MerlinApi.evolution().exclude(
				ZEUS_POSEIDON,
				Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "undying_drop"),
				Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "gentle_descent"));
	}

	public static boolean isEvolved(Holder<Enchantment> holder) {
		return holder.unwrapKey()
				.map(key -> MerlinApi.evolution().rules().stream().anyMatch(rule -> rule.to().equals(key.identifier())))
				.orElse(false);
	}
}
