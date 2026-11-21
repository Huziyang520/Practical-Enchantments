// 台面上方悬浮物品的渲染：绕 Y 轴旋转并轻微上下浮动，物品本体由物品模型解析。

package com.practicalenchantments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.practicalenchantments.content.EvolutionTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
	private final ItemRenderer itemRenderer;

	public EvolutionTableRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(EvolutionTableBlockEntity table, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffers, int lightCoords, int overlay) {
		ItemStack displayed = table.displayed();
		Level level = table.getLevel();
		if (displayed.isEmpty() || level == null) {
			return;
		}
		boolean working = table.isWorking();
		float time = level.getGameTime() + partialTicks;
		float rotation = table.advanceSpin(time, spinSpeed(table, working, partialTicks));
		float bob = (float) Math.sin(time * 0.1F) * BOB_HEIGHT;
		poseStack.pushPose();
		poseStack.translate(0.5F, BASE_HEIGHT + bob, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
		poseStack.scale(SCALE, SCALE, SCALE);
		this.itemRenderer.renderStatic(displayed, ItemDisplayContext.GROUND, lightCoords, OverlayTexture.NO_OVERLAY,
				poseStack, buffers, level, (int) table.getBlockPos().asLong());
		poseStack.popPose();
	}

	private static float spinSpeed(EvolutionTableBlockEntity table, boolean working, float partialTicks) {
		if (!working) {
			return DEGREES_PER_TICK;
		}
		float ramp = Math.min(1.0F, Math.min(table.progress(partialTicks), 1.0F - table.progress(partialTicks))
				/ SPIN_EASE_EDGE);
		return DEGREES_PER_TICK * (1.0F + (WORKING_SPIN - 1.0F) * (float) Math.sin(ramp * Math.PI * 0.5F));
	}
}
