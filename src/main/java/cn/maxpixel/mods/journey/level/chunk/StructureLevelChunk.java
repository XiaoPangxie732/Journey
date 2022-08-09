package cn.maxpixel.mods.journey.level.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.jetbrains.annotations.Nullable;

public class StructureLevelChunk extends LevelChunk {
    public StructureLevelChunk(Level pLevel, ChunkPos pPos) {
        super(pLevel, pPos);
    }

    public StructureLevelChunk(Level pLevel, ChunkPos pPos, UpgradeData pData, LevelChunkTicks<Block> pBlockTicks, LevelChunkTicks<Fluid> pFluidTIcks, long pInhabitedTime, @Nullable LevelChunkSection[] pSections, @Nullable LevelChunk.PostLoadProcessor pPostLoad, @Nullable BlendingData p_196862_) {
        super(pLevel, pPos, pData, pBlockTicks, pFluidTIcks, pInhabitedTime, pSections, pPostLoad, p_196862_);
    }

    public StructureLevelChunk(ServerLevel pLevel, ProtoChunk pChunk, @Nullable LevelChunk.PostLoadProcessor pPostLoad) {
        super(pLevel, pChunk, pPostLoad);
    }

    @Override
    public void addAndRegisterBlockEntity(BlockEntity blockEntity) {
        setBlockEntity(blockEntity);
        if (isInLevel()) {
            addGameEventListener(blockEntity);
            updateBlockEntityTicker(blockEntity);
        }
    }
}