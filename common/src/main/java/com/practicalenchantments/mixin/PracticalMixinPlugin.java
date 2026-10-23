package com.practicalenchantments.mixin;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * 按运行中的版本挑立方体分裂的 mixin：26.1 的分裂代码在 {@code Slime}，26.2 搬到了
 * {@code cubemob.AbstractCubeMob}。目标类不存在是硬错误，所以必须在 Mixin 解析目标之前就退掉一条。
 */
public final class PracticalMixinPlugin implements IMixinConfigPlugin {

	/** 26.2 才有的类，用它当版本探针。 */
	private static final String CUBE_MOB_RESOURCE =
			"net/minecraft/world/entity/monster/cubemob/AbstractCubeMob.class";

	/** 26.1 的分裂实现。 */
	private static final String MIXIN_SLIME = "com.practicalenchantments.mixin.SlimeSplitMixin";
	/** 26.2 起的实现。 */
	private static final String MIXIN_CUBE_MOB = "com.practicalenchantments.mixin.CubeMobSplitMixin";

	/** 只查一次：游戏运行期间版本不会变。 */
	private static Boolean cubeMobPresent;

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		boolean modern = hasCubeMobClass();
		if (MIXIN_SLIME.equals(mixinClassName)) {
			return !modern;
		}
		if (MIXIN_CUBE_MOB.equals(mixinClassName)) {
			return modern;
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
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
			IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
			IMixinInfo mixinInfo) {
	}

	/** 走类路径查找而不是 {@code Class.forName}：提前加载 Minecraft 类会触发 Mixin 的过早加载保护。 */
	private static boolean hasCubeMobClass() {
		if (cubeMobPresent == null) {
			cubeMobPresent = PracticalMixinPlugin.class.getClassLoader().getResource(CUBE_MOB_RESOURCE) != null;
		}
		return cubeMobPresent;
	}
}
