package cn.maxpixel.mods.journey.client.renderers.be;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ControllerBlockEntityRenderer implements BlockEntityRenderer<ControllerBlockEntity> {
    public ControllerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ControllerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    }

    @Override
    public boolean shouldRenderOffScreen(ControllerBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}