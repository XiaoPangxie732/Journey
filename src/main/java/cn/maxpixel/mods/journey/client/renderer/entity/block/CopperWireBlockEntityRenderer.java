package cn.maxpixel.mods.journey.client.renderer.entity.block;

import cn.maxpixel.mods.journey.block.entity.CopperWireBlockEntity;
import cn.maxpixel.mods.journey.client.registry.TextureRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

public class CopperWireBlockEntityRenderer implements BlockEntityRenderer<CopperWireBlockEntity> {
    private final TextureAtlasSprite sprite;

    public CopperWireBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.sprite = context.getBlockRenderDispatcher()
                .getBlockModelShaper()
                .getModelManager()
                .getAtlas(InventoryMenu.BLOCK_ATLAS)
                .getSprite(TextureRegistry.COPPER_WIRE);
    }

    @Override
    public void render(CopperWireBlockEntity be, float partialTick, PoseStack stack, MultiBufferSource buf, int packedLight, int packedOverlay) {//TODO
        short count = be.getCount();
        VertexConsumer consumer = buf.getBuffer(RenderType.cutout());
        PoseStack.Pose last = stack.last();
        float f = count / 2f;
        renderCenter(last, consumer, f, packedLight, packedOverlay);
    }

    // 顺时针渲染内侧，逆时针渲染外侧
    // uv坐标原点在左上角(u, v)，u轴正方向朝右，v轴正方向朝下
    private void fillVertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, Vec3i normal, int packedLight, int packedOverlay) {
        consumer.vertex(pose.pose(), x / 16f, y / 16f, z / 16f)
                .color(0xFFFFFFFF)
                .uv(sprite.getU(u), sprite.getV(v))
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(pose.normal(), normal.getX(), normal.getY(), normal.getZ())
                .endVertex();
    }

    private void renderCenter(PoseStack.Pose pose, VertexConsumer consumer, float f, int packedLight, int packedOverlay) {
        // bottom
        float f1 = Mth.clamp(f, 0f, 8f);
        fillVertex(pose, consumer, 8 - f1, 0, 8 + f1, 8 - f1, 8 - f1, Direction.DOWN.getNormal(), packedLight, packedOverlay);// Direction.DOWN => YN
        fillVertex(pose, consumer, 8 - f1, 0, 8 - f1, 8 - f1, 8 + f1, Direction.DOWN.getNormal(), packedLight, packedOverlay);
        fillVertex(pose, consumer, 8 + f1, 0, 8 - f1, 8 + f1, 8 + f1, Direction.DOWN.getNormal(), packedLight, packedOverlay);
        fillVertex(pose, consumer, 8 + f1, 0, 8 + f1, 8 + f1, 8 - f1, Direction.DOWN.getNormal(), packedLight, packedOverlay);
    }

    private void renderNorth(PoseStack.Pose pose, VertexConsumer consumer, float i, int packedLight, int packedOverlay) {
        // bottom
//        fillVertex(pose, consumer, 8 - i, 0, 8 - i, 8 - i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 - i, 8 + i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 + i, 8 + i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 - i, 0, 8 + i, 8 - i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
    }

    private void renderSouth(PoseStack.Pose pose, VertexConsumer consumer, float i, int packedLight, int packedOverlay) {
        // bottom
//        fillVertex(pose, consumer, 8 - i, 0, 8 - i, 8 - i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 - i, 8 + i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 + i, 8 + i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 - i, 0, 8 + i, 8 - i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
    }

    private void renderWest(PoseStack.Pose pose, VertexConsumer consumer, float i, int packedLight, int packedOverlay) {
        // bottom
//        fillVertex(pose, consumer, 8 - i, 0, 8 - i, 8 - i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 - i, 8 + i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 + i, 8 + i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 - i, 0, 8 + i, 8 - i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
    }

    private void renderEast(PoseStack.Pose pose, VertexConsumer consumer, float i, int packedLight, int packedOverlay) {
        // bottom
//        fillVertex(pose, consumer, 8 - i, 0, 8 - i, 8 - i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 - i, 8 + i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 + i, 8 + i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 - i, 0, 8 + i, 8 - i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
    }

    private void renderUp(PoseStack.Pose pose, VertexConsumer consumer, float i, int packedLight, int packedOverlay) {
        // bottom
//        fillVertex(pose, consumer, 8 - i, 0, 8 - i, 8 - i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 - i, 8 + i, 8 - i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 + i, 0, 8 + i, 8 + i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
//        fillVertex(pose, consumer, 8 - i, 0, 8 + i, 8 - i, 8 + i, Vector3f.YN, packedLight, packedOverlay);
    }
}