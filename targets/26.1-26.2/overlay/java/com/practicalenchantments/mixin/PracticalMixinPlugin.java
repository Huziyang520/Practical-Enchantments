// Picks the cube mob mixin that matches the running build: 26.1.2 has Slime#getSplitCount,
// 26.2 and later moved it to AbstractCubeMob.

package com.practicalenchantments.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

public final class PracticalMixinPlugin implements IMixinConfigPlugin {
	private static final String CUBE_MOB = "net/minecraft/world/entity/monster/cubemob/AbstractCubeMob.class";

	private static Boolean cubeMobPresent;

	private static boolean cubeMobPresent() {
		if (cubeMobPresent == null) {
			cubeMobPresent = PracticalMixinPlugin.class.getClassLoader().getResource(CUBE_MOB) != null;
		}
		return cubeMobPresent;
	}

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.endsWith("SlimeSplitMixin")) {
			return !cubeMobPresent();
		}
		if (mixinClassName.endsWith("CubeMobSplitMixin")) {
			return cubeMobPresent();
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
