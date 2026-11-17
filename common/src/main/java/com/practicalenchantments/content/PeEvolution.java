// 进化规则表：目前只有「引雷 → 宙斯与波塞冬」一条，数值集中在这里方便调整。
// 转化池排除三个本来就不能通过附魔台或战利品直接获得的附魔。

package com.practicalenchantments.content;

import com.huziyang520.merlinlib.api.EvolutionRule;
import com.huziyang520.merlinlib.api.MerlinApi;
import com.practicalenchantments.PracticalEnchantments;
import net.minecraft.resources.Identifier;

public final class PeEvolution {
	public static final Identifier CHANNELING = Identifier.withDefaultNamespace("channeling");
	public static final Identifier ZEUS_POSEIDON =
			Identifier.fromNamespaceAndPath(PracticalEnchantments.MOD_ID, "zeus_poseidon");
	public static final EvolutionRule CHANNELING_TO_ZEUS_POSEIDON =
			new EvolutionRule(CHANNELING, ZEUS_POSEIDON, 0.65F, 0.15F, 14, 200);
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
}
