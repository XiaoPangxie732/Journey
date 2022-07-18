package cn.maxpixel.mods.journey.client.renderers.be;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class ControllerBlockEntityRenderer implements BlockEntityRenderer<ControllerBlockEntity> {
    public ControllerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ControllerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.isBuilding()) {
            BlockPos start = blockEntity.getStart();
            Vec3i size = blockEntity.getSize();
            if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
                VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
                int x = start.getX();
                int y = start.getY();
                int z = start.getZ();
                LevelRenderer.renderLineBox(poseStack, lines, x, y, z, x + size.getX(), y + size.getY(), z + size.getZ(), .9f, .9f, .9f, 1f, .5f, .5f, .5f);
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ControllerBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 384;
    }
}