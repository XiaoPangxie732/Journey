package cn.maxpixel.mods.journey.level.chunk;

import cn.maxpixel.mods.journey.level.StructureLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.jetbrains.annotations.Nullable;

public class StructureLevelChunk extends LevelChunk {// TODO: workaround. use mixin/coremod later
    public StructureLevelChunk(StructureLevel pLevel, ChunkPos pPos) {
        super(pLevel, pPos);
    }

    public StructureLevelChunk(StructureLevel pLevel, ChunkPos pPos, UpgradeData pData, LevelChunkTicks<Block> pBlockTicks, LevelChunkTicks<Fluid> pFluidTIcks, long pInhabitedTime, @Nullable LevelChunkSection[] pSections, @Nullable LevelChunk.PostLoadProcessor pPostLoad, @Nullable BlendingData p_196862_) {
        super(pLevel, pPos, pData, pBlockTicks, pFluidTIcks, pInhabitedTime, pSections, pPostLoad, p_196862_);
    }

    @Override
    public void addAndRegisterBlockEntity(BlockEntity blockEntity) {
        setBlockEntity(blockEntity);
        if (isInLevel()) {
            addGameEventListener(blockEntity);
            updateBlockEntityTicker(blockEntity);
            if (getLevel().isClientSide) {
                blockEntity.setLevel(((StructureLevel) getLevel()).parent);
                blockEntity.onLoad();
                blockEntity.setLevel(getLevel());
            } else {
                blockEntity.onLoad();
            }
        }
    }

    @Override
    public void clearAllBlockEntities() {
        if (getLevel().isClientSide) {
            blockEntities.values().forEach(blockEntity -> blockEntity.setLevel(((StructureLevel) getLevel()).parent));
        }
        super.clearAllBlockEntities();
    }
}