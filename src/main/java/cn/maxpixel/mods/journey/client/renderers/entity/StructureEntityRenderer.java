package cn.maxpixel.mods.journey.client.renderers.entity;

import cn.maxpixel.mods.journey.entity.StructureEntity;
import cn.maxpixel.mods.journey.level.StructureLevel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class StructureEntityRenderer extends EntityRenderer<StructureEntity> {
    private static final List<RenderType> CHUNK_BUFFER_LAYERS = RenderType.chunkBufferLayers();
//    private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers().stream()
//            .collect(Collectors.toMap(Function.identity(), v -> new VertexBuffer()));
//    private final ChunkBufferBuilderPack pack = new ChunkBufferBuilderPack();
//    private final ObjectOpenHashSet<RenderType> hasBlocks = new ObjectOpenHashSet<>();
//    private final ObjectOpenHashSet<BlockEntityRenderer<? extends BlockEntity>> blockEntities = new ObjectOpenHashSet<>();
//    private VisibilitySet visibilitySet;
//    private BufferBuilder.SortState transparencyState;
//    private double oldCamX;
//    private double oldCamY;
//    private double oldCamZ;

    public StructureEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public boolean shouldRender(StructureEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    @Override
    public void render(StructureEntity entity, float yaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight) {// TODO: culling, optimization, etc.
        StructureLevel structureLevel = entity.getStructureLevel();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        stack.pushPose();
        Vec3 relative = entity.getOriginRelative();
        stack.translate(relative.x, relative.y, relative.z);
        for (BlockPos pos : BlockPos.betweenClosed(structureLevel.start, structureLevel.start.offset(structureLevel.size))) {
            stack.pushPose();
            stack.translate(pos.getX(), pos.getY(), pos.getZ());
            BlockState state = structureLevel.getBlockState(pos);
            FluidState fluid = state.getFluidState();
            for (RenderType chunkBufferLayer : CHUNK_BUFFER_LAYERS) {
                ForgeHooksClient.setRenderType(chunkBufferLayer);
                if (!fluid.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluid, chunkBufferLayer)) {
                    stack.pushPose();
                    stack.translate(
                            -SectionPos.sectionRelative(pos.getX()),
                            -SectionPos.sectionRelative(pos.getY()),
                            -SectionPos.sectionRelative(pos.getZ())
                    );
                    blockRenderer.renderLiquid(pos, structureLevel, new TransformingVertexConsumer(
                            buffer.getBuffer(chunkBufferLayer), stack.last()), state, fluid);
                    stack.popPose();
                }
                if (state.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(state, chunkBufferLayer)) {
                    blockRenderer.renderBatched(
                            state, pos, structureLevel, stack, buffer.getBuffer(chunkBufferLayer),
                            true, new Random(), EmptyModelData.INSTANCE
                    );
                }
            }
            ForgeHooksClient.setRenderType(null);
            if (state.hasBlockEntity()) {
                BlockEntity blockEntity = structureLevel.getBlockEntity(pos);
                if (blockEntity != null) {
                    blockEntityRenderDispatcher.render(blockEntity, partialTick, stack, buffer);
                }
            }
            stack.popPose();
        }
        stack.popPose();
//        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//        prepareChunks(entity, structureLevel, stack, camPos);
    }

    @Override
    protected boolean shouldShowName(StructureEntity pEntity) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(StructureEntity pEntity) {
        return null;
    }

//    private void prepareChunks(StructureEntity entity, StructureLevel structureLevel, PoseStack stack, Vec3 camPos) {// TODO
//        BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
//        BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
//        Random random = new Random();
//
//        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
//        ModelBlockRenderer.enableCaching();
//        VisGraph graph = new VisGraph();
//        ObjectOpenHashSet<RenderType> hasLayer = new ObjectOpenHashSet<>();
//        for (LevelChunk chunk : structureLevel.getChunkSource().getLoadedChunks()) {
//            int minX = chunk.getPos().getMinBlockX();
//            int maxX = chunk.getPos().getMaxBlockX();
//            int minY = chunk.getMinBuildHeight();
//            int maxY = chunk.getMaxBuildHeight();
//            int minZ = chunk.getPos().getMinBlockX();
//            int maxZ = chunk.getPos().getMaxBlockZ();
//            // FIXME: <= or < ?
//            for (int x = minX; x <= maxX; x++) for (int y = minY; y < maxY; y++) for (int z = minZ; z <= maxZ; z++) {
//                pos.set(x, y, z);
//                BlockState blockState = chunk.getBlockState(pos);
//                if (blockState.isSolidRender(structureLevel, pos)) {
//                    graph.setOpaque(pos);
//                }
//                if (blockState.hasBlockEntity()) {
//                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
//                    if (blockEntity != null) { // TODO: filter off-screen rendering
//                        var renderer = blockEntityRenderDispatcher.getRenderer(blockEntity);
//                        if (renderer != null) {
//                            blockEntities.add(renderer);
//                        }
//                    }
//                }
//                FluidState fluidState = blockState.getFluidState();
//                for (RenderType renderType : CHUNK_BUFFER_LAYERS) {
//                    if (!fluidState.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluidState, renderType)) {
//                        BufferBuilder builder = pack.builder(renderType);
//                        if (hasLayer.add(renderType)) {
//                            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
//                        }
//                        if (blockRenderDispatcher.renderLiquid(pos, structureLevel, builder, blockState, fluidState)) {
//                            hasBlocks.add(renderType);
//                        }
//                    }
//                    if (blockState.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(blockState, renderType)) {
//                        BufferBuilder builder = pack.builder(renderType);
//                        if (hasLayer.add(renderType)) {
//                            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
//                        }
//                        stack.pushPose();
//                        stack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
//                        if (blockRenderDispatcher.renderBatched(blockState, pos, structureLevel, stack, builder, true, random, EmptyModelData.INSTANCE)) {
//                            hasBlocks.add(renderType);
//                        }
//                        stack.popPose();
//                    }
//                }
//            }
//        }
//        if (hasBlocks.contains(RenderType.translucent())) {
//            BufferBuilder builder = pack.builder(RenderType.translucent());
//            Vec3 entityPos = entity.position();
//            builder.setQuadSortOrigin(
//                    (float) (camPos.x - pos.getX() - entityPos.x),
//                    (float) (camPos.y - pos.getY() - entityPos.y),
//                    (float) (camPos.z - pos.getZ() - entityPos.z)
//            );
//            this.transparencyState = builder.getSortState();
//        }
//        hasLayer.stream().map(pack::builder).forEach(BufferBuilder::end);
//        ModelBlockRenderer.clearCache();
//        this.visibilitySet = graph.resolve();
//        hasLayer.forEach(type -> buffers.get(type).upload(pack.builder(type)));
//    }
//
//    private void renderChunkLayer(StructureLevel structureLevel, RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
//        RenderSystem.assertOnRenderThread();
//        renderType.setupRenderState();
//        boolean translucent = renderType == RenderType.translucent();
//        if (translucent) {
//            double deltaX = camX - oldCamX;
//            double deltaY = camY - oldCamY;
//            double deltaZ = camZ - oldCamZ;
//            if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 1) {
//                this.oldCamX = camX;
//                this.oldCamY = camY;
//                this.oldCamZ = camZ;
//            }
//            for (LevelChunk chunk : structureLevel.getChunkSource().getLoadedChunks()) {
//            }
//        }
//    }

    private record TransformingVertexConsumer(VertexConsumer parent, PoseStack.Pose pose) implements VertexConsumer {
        @Override
        public TransformingVertexConsumer vertex(double x, double y, double z) {
            Vector4f vector = new Vector4f((float) x, (float) y, (float) z, 1);
            vector.transform(pose.pose());
            parent.vertex(vector.x(), vector.y(), vector.z());
            return this;
        }

        @Override
        public TransformingVertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {
            parent.color(pRed, pGreen, pBlue, pAlpha);
            return this;
        }

        @Override
        public TransformingVertexConsumer uv(float pU, float pV) {
            parent.uv(pU, pV);
            return this;
        }

        @Override
        public TransformingVertexConsumer overlayCoords(int pU, int pV) {
            parent.overlayCoords(pU, pV);
            return this;
        }

        @Override
        public TransformingVertexConsumer uv2(int pU, int pV) {
            parent.uv2(pU, pV);
            return this;
        }

        @Override
        public TransformingVertexConsumer normal(float x, float y, float z) {
            Vector3f vector = new Vector3f(x, y, z);
            vector.transform(pose.normal());
            parent.normal(vector.x(), vector.y(), vector.z());
            return this;
        }

        @Override
        public void endVertex() {
            parent.endVertex();
        }

        @Override
        public void defaultColor(int pDefaultR, int pDefaultG, int pDefaultB, int pDefaultA) {
            parent.defaultColor(pDefaultR, pDefaultG, pDefaultB, pDefaultA);
        }

        @Override
        public void unsetDefaultColor() {
            parent.unsetDefaultColor();
        }
    }
}