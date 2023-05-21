package cn.maxpixel.mods.journey.client.renderer.entity.block;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class ControllerBlockEntityRenderer implements BlockEntityRenderer<ControllerBlockEntity> {
    public ControllerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ControllerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.isBuilding()) {
            BlockPos start = blockEntity.getStart();
            Vec3i size = blockEntity.getSize();
            VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
            int x = start.getX();
            int y = start.getY();
            int z = start.getZ();
            LevelRenderer.renderLineBox(poseStack, lines, x, y, z, x + size.getX(), y + size.getY(), z + size.getZ(),
                    .9f, .9f, .9f, 1f, .5f, .5f, .5f);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ControllerBlockEntity blockEntity) {
        return blockEntity.isBuilding();
    }

    @Override
    public boolean shouldRender(ControllerBlockEntity blockEntity, Vec3 cameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(cameraPos,
                MathUtil.max3(blockEntity.getSize().getX(), blockEntity.getSize().getY(),
                        blockEntity.getSize().getZ()) + getViewDistance());
    }
}