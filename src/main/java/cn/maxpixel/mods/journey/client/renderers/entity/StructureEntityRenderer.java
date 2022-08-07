package cn.maxpixel.mods.journey.client.renderers.entity;

import cn.maxpixel.mods.journey.entity.StructureEntity;
import cn.maxpixel.mods.journey.level.StructureLevel;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StructureEntityRenderer extends EntityRenderer<StructureEntity> {
    private static final List<RenderType> CHUNK_BUFFER_LAYERS = RenderType.chunkBufferLayers();
    private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers().stream()
            .collect(Collectors.toMap(Function.identity(), v -> new VertexBuffer()));
    private final ChunkBufferBuilderPack pack = new ChunkBufferBuilderPack();
    private final ObjectOpenHashSet<RenderType> hasBlocks = new ObjectOpenHashSet<>();
    private final ObjectOpenHashSet<BlockEntityRenderer<? extends BlockEntity>> blockEntities = new ObjectOpenHashSet<>();
    private VisibilitySet visibilitySet;
    private BufferBuilder.SortState transparencyState;

    public StructureEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(StructureEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {// TODO: culling, optimization, etc.
        StructureLevel structureLevel = entity.getStructureLevel();
        prepareChunks(entity, structureLevel, poseStack);
    }

    @Override
    protected boolean shouldShowName(StructureEntity pEntity) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(StructureEntity pEntity) {
        return null;
    }

    private void prepareChunks(StructureEntity entity, StructureLevel structureLevel, PoseStack stack) {
        BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
        BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        Random random = new Random();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        ModelBlockRenderer.enableCaching();
        VisGraph graph = new VisGraph();
        ObjectOpenHashSet<RenderType> hasLayer = new ObjectOpenHashSet<>();
        for (LevelChunk chunk : structureLevel.getChunkSource().getLoadedChunks()) {
            int minX = chunk.getPos().getMinBlockX();
            int maxX = chunk.getPos().getMaxBlockX();
            int minY = chunk.getMinBuildHeight();
            int maxY = chunk.getMaxBuildHeight();
            int minZ = chunk.getPos().getMinBlockX();
            int maxZ = chunk.getPos().getMaxBlockZ();
            // FIXME: <= or < ?
            for (int x = minX; x <= maxX; x++) for (int y = minY; y < maxY; y++) for (int z = minZ; z <= maxZ; z++) {
                pos.set(x, y, z);
                BlockState blockState = chunk.getBlockState(pos);
                if (blockState.isSolidRender(structureLevel, pos)) {
                    graph.setOpaque(pos);
                }
                if (blockState.hasBlockEntity()) {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                    if (blockEntity != null) { // TODO: filter off-screen rendering
                        var renderer = blockEntityRenderDispatcher.getRenderer(blockEntity);
                        if (renderer != null) {
                            blockEntities.add(renderer);
                        }
                    }
                }
                FluidState fluidState = blockState.getFluidState();
                for (RenderType renderType : CHUNK_BUFFER_LAYERS) {
                    if (!fluidState.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluidState, renderType)) {
                        BufferBuilder builder = pack.builder(renderType);
                        if (hasLayer.add(renderType)) {
                            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                        }
                        if (blockRenderDispatcher.renderLiquid(pos, structureLevel, builder, blockState, fluidState)) {
                            hasBlocks.add(renderType);
                        }
                    }
                    if (blockState.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(blockState, renderType)) {
                        BufferBuilder builder = pack.builder(renderType);
                        if (hasLayer.add(renderType)) {
                            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                        }
                        stack.pushPose();
                        stack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
                        if (blockRenderDispatcher.renderBatched(blockState, pos, structureLevel, stack, builder, true, random, EmptyModelData.INSTANCE)) {
                            hasBlocks.add(renderType);
                        }
                        stack.popPose();
                    }
                }
            }
        }
        if (hasBlocks.contains(RenderType.translucent())) {
            BufferBuilder builder = pack.builder(RenderType.translucent());
            Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 entityPos = entity.position();
            builder.setQuadSortOrigin(
                    (float) (camPos.x - pos.getX() - entityPos.x),
                    (float) (camPos.y - pos.getY() - entityPos.y),
                    (float) (camPos.z - pos.getZ() - entityPos.z)
            );
            this.transparencyState = builder.getSortState();
        }
        hasLayer.stream().map(pack::builder).forEach(BufferBuilder::end);
        ModelBlockRenderer.clearCache();
        this.visibilitySet = graph.resolve();
        hasLayer.forEach(type -> buffers.get(type).upload(pack.builder(type)));
    }
}