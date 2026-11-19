// 进化规则表：目前只有「引雷 → 宙斯与波塞冬」一条。
// 成本按原附魔等级缩放（4 级 + 8，上限 64），时长按成本（20 tick/个，上限 16 秒）。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.EvolutionRule;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.config.Tunables;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

public final class PeEvolution {
	/** 引雷只到 1 级；以后新增规则时把对应附魔的最高等级写在这里。 */
	private static final int CHANNELING_MAX_LEVEL = 1;
	public static final Identifier CHANNELING = Identifier.withDefaultNamespace("channeling");
	public static final Identifier ZEUS_POSEIDON =
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "zeus_poseidon");
	public static final int MAX_COST = 64;
	public static final int MAX_TICKS = 320;
	public static final EvolutionRule CHANNELING_TO_ZEUS_POSEIDON = channelingRule();
	private static boolean registered;

	private PeEvolution() {
	}

	private static EvolutionRule channelingRule() {
		int cost = Math.min(Tunables.evolutionLapisCostCap(),
				Tunables.evolutionLapisCostBase() + Tunables.evolutionLapisCostStep() * CHANNELING_MAX_LEVEL);
		int ticks = Math.min(Tunables.evolutionDurationCapTicks(), Tunables.evolutionTicksPerLapis() * cost);
		return new EvolutionRule(CHANNELING, ZEUS_POSEIDON,
				Tunables.evolutionBaseSuccessPercent() / 100.0F,
				Tunables.evolutionBaseTransmutePercent() / 100.0F, cost, ticks);
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
		for (EvolutionRule rule : MerlinApi.evolution().rules()) {
			MerlinApi.enchantmentText().style(nameKey(rule.to()), MerlinApi.text()::rainbow);
		}
	}

	/** 附魔名与描述共用同一套 lang 键：enchantment.<namespace>.<path>。 */
	private static String nameKey(Identifier id) {
		return "enchantment." + id.getNamespace() + "." + id.getPath();
	}

	public static boolean isEvolved(Holder<Enchantment> holder) {
		return holder.unwrapKey()
				.map(key -> MerlinApi.evolution().rules().stream().anyMatch(rule -> rule.to().equals(key.identifier())))
				.orElse(false);
	}
}
