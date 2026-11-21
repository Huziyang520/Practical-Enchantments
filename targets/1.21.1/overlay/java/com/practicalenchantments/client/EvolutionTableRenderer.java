// 台面上方悬浮物品的渲染：绕 Y 轴旋转并轻微上下浮动，物品本体由物品模型解析。

package com.practicalenchantments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EvolutionTableRenderer implements BlockEntityRenderer<EvolutionTableBlockEntity> {
	private static final float SCALE = 0.85F;
	private static final float BASE_HEIGHT = 0.95F;
	private static final float BOB_HEIGHT = 0.04F;
	private static final float DEGREES_PER_TICK = 1.5F;
	private static final float WORKING_SPIN = 48.0F;
	// 首尾各 12% 进度用来加速/减速，避免开关瞬间转速突变。
	private static final float SPIN_EASE_EDGE = 0.12F;

	public EvolutionTableRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(EvolutionTableBlockEntity blockEntity, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		ItemStack displayed = blockEntity.displayed();
		if (displayed.isEmpty()) {
			return;
		}
		Level level = blockEntity.getLevel();
		if (level == null) {
			return;
		}
		float time = level.getGameTime() + partialTick;
		boolean working = blockEntity.isWorking();
		float rotation = blockEntity.advanceSpin(time, spinSpeed(blockEntity, working, partialTick));
		float bob = (float) Math.sin(time * 0.1F) * BOB_HEIGHT;
		poseStack.pushPose();
		poseStack.translate(0.5F, BASE_HEIGHT + bob, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
		poseStack.scale(SCALE, SCALE, SCALE);
		Minecraft.getInstance().getItemRenderer().renderStatic(displayed, ItemDisplayContext.GROUND,
				packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, level,
				(int) blockEntity.getBlockPos().asLong());
		poseStack.popPose();
	}

	private static float spinSpeed(EvolutionTableBlockEntity blockEntity, boolean working, float partialTicks) {
		if (!working) {
			return DEGREES_PER_TICK;
		}
		float progress = blockEntity.progress(partialTicks);
		float ramp = Math.min(1.0F, Math.min(progress, 1.0F - progress) / SPIN_EASE_EDGE);
		return DEGREES_PER_TICK * (1.0F + (WORKING_SPIN - 1.0F) * (float) Math.sin(ramp * Math.PI * 0.5F));
	}
}
