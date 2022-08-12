package cn.maxpixel.mods.journey.client.renderers.entity.block;

import cn.maxpixel.mods.journey.block.entity.CreativeEngineBlockEntity;
import cn.maxpixel.mods.journey.level.StructureLevel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.EmptyModelData;

public class CreativeEngineBlockEntityRenderer implements BlockEntityRenderer<CreativeEngineBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    public CreativeEngineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(CreativeEngineBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int pPackedOverlay) {
        if (blockEntity.getLevel() instanceof StructureLevel) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
            poseStack.translate(0, 0, -1);
            context.getBlockRenderDispatcher().renderSingleBlock(Blocks.FIRE.defaultBlockState(), poseStack, buffer,
                    packedLight, pPackedOverlay, EmptyModelData.INSTANCE);
            poseStack.popPose();
        }
    }
}