// 台面上方悬浮物品的渲染：绕 Y 轴旋转并轻微上下浮动，物品本体由物品模型解析。

package com.practicalenchantments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EvolutionTableRenderer implements BlockEntityRenderer<EvolutionTableBlockEntity, EvolutionTableRenderer.State> {
	private static final float SCALE = 0.85F;
	private static final float BASE_HEIGHT = 0.95F;
	private static final float BOB_HEIGHT = 0.04F;
	private static final float DEGREES_PER_TICK = 1.5F;
	private static final float WORKING_SPIN = 48.0F;
	// 首尾各 18% 进度用来加速/减速，避免开关瞬间转速突变。
	private static final float SPIN_EASE_EDGE = 0.12F;
	private final ItemModelResolver itemModelResolver;

	public EvolutionTableRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(EvolutionTableBlockEntity blockEntity, State state, float partialTicks,
			Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		ItemStack displayed = blockEntity.displayed();
		state.hasItem = !displayed.isEmpty();
		if (!state.hasItem) {
			return;
		}
		Level level = blockEntity.getLevel();
		if (level == null) {
			state.hasItem = false;
			return;
		}
		this.itemModelResolver.updateForTopItem(state.item, displayed, ItemDisplayContext.GROUND, level, blockEntity,
				(int) blockEntity.getBlockPos().asLong());
		float time = level.getGameTime() + partialTicks;
		state.working = blockEntity.isWorking();
		state.rotation = blockEntity.advanceSpin(time, spinSpeed(blockEntity, state.working, partialTicks));
		state.bob = (float) Math.sin(time * 0.1F) * BOB_HEIGHT;
	}

	private static float spinSpeed(EvolutionTableBlockEntity blockEntity, boolean working, float partialTicks) {
		if (!working) {
			return DEGREES_PER_TICK;
		}
		float ramp = Math.min(1.0F, Math.min(blockEntity.progress(partialTicks), 1.0F - blockEntity.progress(partialTicks))
				/ SPIN_EASE_EDGE);
		return DEGREES_PER_TICK * (1.0F + (WORKING_SPIN - 1.0F) * (float) Math.sin(ramp * Math.PI * 0.5F));
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera) {
		if (!state.hasItem) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5F, BASE_HEIGHT + state.bob, 0.5F);
		poseStack.rotateDegrees(Axis.YP, state.rotation);
		poseStack.scale(SCALE, SCALE, SCALE);
		state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}

	public static class State extends BlockEntityRenderState {
		public final ItemStackRenderState item = new ItemStackRenderState();
		public boolean hasItem;
		public boolean working;
		public float rotation;
		public float bob;
	}
}
