package cn.maxpixel.mods.journey.level.chunk;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.level.StructureLevel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;

import java.util.EnumSet;
import java.util.Map;

import static net.minecraft.world.level.chunk.storage.ChunkSerializer.BLOCK_STATE_CODEC;
import static net.minecraft.world.level.chunk.storage.ChunkSerializer.makeBiomeCodec;

public class ChunkLoader {
    public static StructureLevelChunk loadChunk(StructureLevel level, ChunkPos pos, CompoundTag tag) {
        if (ChunkSerializer.getChunkTypeFromTag(tag) != ChunkStatus.ChunkType.LEVELCHUNK) {
            throw new UnsupportedOperationException();
        }
        boolean lightOn = tag.getBoolean("isLightOn");
        boolean hasSkyLight = level.dimensionType().hasSkyLight();
        LevelLightEngine lightEngine = level.getLightEngine();
        if (lightOn) {
            lightEngine.retainData(pos, true);
        }
        UpgradeData upgradeData = tag.contains("UpgradeData", Tag.TAG_COMPOUND) ?
                new UpgradeData(tag.getCompound("UpgradeData"), level) : UpgradeData.EMPTY;
        LevelChunkTicks<Block> blockTicks = LevelChunkTicks.load(tag.getList("block_ticks", Tag.TAG_COMPOUND),
                (id) -> Registry.BLOCK.getOptional(ResourceLocation.tryParse(id)), pos);
        LevelChunkTicks<Fluid> fluidTicks = LevelChunkTicks.load(tag.getList("fluid_ticks", Tag.TAG_COMPOUND),
                (id) -> Registry.FLUID.getOptional(ResourceLocation.tryParse(id)), pos);
        Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        Codec<PalettedContainer<Holder<Biome>>> biomeCodec = makeBiomeCodec(biomeRegistry);

        int sectionCount = level.getSectionsCount();
        ListTag sectionsData = tag.getList("sections", Tag.TAG_COMPOUND);
        LevelChunkSection[] sections = new LevelChunkSection[sectionCount];
        for (int i = 0; i < sectionsData.size(); i++) {
            CompoundTag sectionData = sectionsData.getCompound(i);
            int sectionY = sectionData.getByte("Y");
            int sectionIndex = level.getSectionIndexFromSectionY(sectionY);
            if (sectionIndex >= 0 && sectionIndex < sections.length) {
                PalettedContainer<BlockState> blockStates;
                if (sectionData.contains("block_states", Tag.TAG_COMPOUND)) {
                    blockStates = BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, sectionData.getCompound("block_states"))
                            .promotePartial((errorMessage) -> JourneyMod.LOGGER.error("Recoverable errors when loading section [" + pos.x + ", " + sectionY + ", " + pos.z + "]: " + errorMessage))
                            .getOrThrow(false, JourneyMod.LOGGER::error);
                } else {
                    blockStates = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
                }

                PalettedContainer<Holder<Biome>> biomes;
                if (sectionData.contains("biomes", Tag.TAG_COMPOUND)) {
                    biomes = biomeCodec.parse(NbtOps.INSTANCE, sectionData.getCompound("biomes"))
                            .promotePartial((errorMessage) -> JourneyMod.LOGGER.error("Recoverable errors when loading section [" + pos.x + ", " + sectionY + ", " + pos.z + "]: " + errorMessage))
                            .getOrThrow(false, JourneyMod.LOGGER::error);
                } else {
                    biomes = new PalettedContainer<>(biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
                }

                LevelChunkSection section = new LevelChunkSection(sectionY, blockStates, biomes);
                sections[sectionIndex] = section;
                // TODO: POI
            }
            if (lightOn) {
                if (sectionData.contains("BlockLight", Tag.TAG_BYTE_ARRAY)) {
                    lightEngine.queueSectionData(LightLayer.BLOCK, SectionPos.of(pos, sectionY), new DataLayer(sectionData.getByteArray("BlockLight")), true);
                }
                if (hasSkyLight && sectionData.contains("SkyLight", Tag.TAG_BYTE_ARRAY)) {
                    lightEngine.queueSectionData(LightLayer.SKY, SectionPos.of(pos, sectionY), new DataLayer(sectionData.getByteArray("SkyLight")), true);
                }
            }
        }

        BlendingData blendingData;
        if (tag.contains("blending_data", Tag.TAG_COMPOUND)) {
            blendingData = BlendingData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.getCompound("blending_data")))
                    .resultOrPartial(JourneyMod.LOGGER::error).orElse(null);
        } else {
            blendingData = null;
        }
        StructureLevelChunk chunk = new StructureLevelChunk(level, pos, upgradeData, blockTicks, fluidTicks, tag.getLong("InhabitedTime"),
                sections, null, blendingData);// TODO: Post load
        chunk.setLightCorrect(lightOn);

        CompoundTag heightMapData = tag.getCompound("Heightmaps");
        EnumSet<Heightmap.Types> heightMapTypes = EnumSet.noneOf(Heightmap.Types.class);
        for(Heightmap.Types types : chunk.getStatus().heightmapsAfter()) {
            String key = types.getSerializationKey();
            if (heightMapData.contains(key, Tag.TAG_LONG_ARRAY)) {
                chunk.setHeightmap(types, heightMapData.getLongArray(key));
            } else {
                heightMapTypes.add(types);
            }
        }
        Heightmap.primeHeightmaps(chunk, heightMapTypes);

        if (tag.getBoolean("shouldSave")) {
            chunk.setUnsaved(true);
        }

        ListTag postProcessingData = tag.getList("PostProcessing", Tag.TAG_LIST);
        for(int j = 0; j < postProcessingData.size(); ++j) {
            ListTag postProcess = postProcessingData.getList(j);
            for(int k = 0; k < postProcess.size(); ++k) {
                chunk.addPackedPostProcess(postProcess.getShort(k), j);
            }
        }

        ListTag blockEntitiesData = tag.getList("block_entities", Tag.TAG_COMPOUND);
        for (int i = 0; i < blockEntitiesData.size(); i++) {
            CompoundTag data = blockEntitiesData.getCompound(i);
            boolean keepPacked = data.getBoolean("keepPacked");
            if (keepPacked) {
                chunk.setBlockEntityNbt(data);
            } else {
                BlockPos blockEntityPos = BlockEntity.getPosFromTag(data);
                BlockEntity blockEntity = BlockEntity.loadStatic(blockEntityPos, chunk.getBlockState(blockEntityPos), data);
                if (blockEntity != null) {
                    chunk.setBlockEntity(blockEntity);
                }
            }
        }

        return chunk;
    }

    public static CompoundTag saveChunk(StructureLevel level, StructureLevelChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        long gameTime = level.getGameTime();
        CompoundTag tag = new CompoundTag();
        tag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        tag.putInt("xPos", chunkPos.x);
        tag.putInt("yPos", chunk.getMinSection());
        tag.putInt("zPos", chunkPos.z);
        tag.putLong("LastUpdate", gameTime);
        tag.putLong("InhabitedTime", chunk.getInhabitedTime());
        tag.putString("Status", chunk.getStatus().getName());
        BlendingData blendingData = chunk.getBlendingData();
        if (blendingData != null) {
            BlendingData.CODEC.encodeStart(NbtOps.INSTANCE, blendingData)
                    .resultOrPartial(JourneyMod.LOGGER::error)
                    .ifPresent((data) -> tag.put("blending_data", data));
        }
        BelowZeroRetrogen retrogen = chunk.getBelowZeroRetrogen();
        if (retrogen != null) {
            BelowZeroRetrogen.CODEC.encodeStart(NbtOps.INSTANCE, retrogen)
                    .resultOrPartial(JourneyMod.LOGGER::error)
                    .ifPresent((data) -> tag.put("below_zero_retrogen", data));
        }
        UpgradeData upgradeData = chunk.getUpgradeData();
        if (!upgradeData.isEmpty()) {
            tag.put("UpgradeData", upgradeData.write());
        }

        LevelChunkSection[] sections = chunk.getSections();
        ListTag sectionsData = new ListTag();
        LevelLightEngine lightEngine = level.getChunkSource().getLightEngine();
        Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        Codec<PalettedContainer<Holder<Biome>>> biomeCodec = makeBiomeCodec(biomeRegistry);
        boolean lightCorrect = chunk.isLightCorrect();
        for(int i = lightEngine.getMinLightSection(); i < lightEngine.getMaxLightSection(); ++i) {
            int sectionIndex = chunk.getSectionIndexFromSectionY(i);
            boolean inBounds = sectionIndex >= 0 && sectionIndex < sections.length;
            DataLayer blockLight = lightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(chunkPos, i));
            DataLayer skyLight = lightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(chunkPos, i));
            if (inBounds || blockLight != null || skyLight != null) {
                CompoundTag sectionData = new CompoundTag();
                if (inBounds) {
                    LevelChunkSection section = sections[sectionIndex];
                    sectionData.put("block_states", BLOCK_STATE_CODEC.encodeStart(NbtOps.INSTANCE, section.getStates())
                            .getOrThrow(false, JourneyMod.LOGGER::error));
                    sectionData.put("biomes", biomeCodec.encodeStart(NbtOps.INSTANCE, section.getBiomes())
                            .getOrThrow(false, JourneyMod.LOGGER::error));
                }

                if (blockLight != null && !blockLight.isEmpty()) {
                    sectionData.putByteArray("BlockLight", blockLight.getData());
                }

                if (skyLight != null && !skyLight.isEmpty()) {
                    sectionData.putByteArray("SkyLight", skyLight.getData());
                }

                if (!sectionData.isEmpty()) {
                    sectionData.putByte("Y", (byte)i);
                    sectionsData.add(sectionData);
                }
            }
        }
        tag.put("sections", sectionsData);
        if (lightCorrect) {
            tag.putBoolean("isLightOn", true);
        }

        ListTag blockEntitiesData = new ListTag();
        for(BlockPos blockpos : chunk.getBlockEntitiesPos()) {
            CompoundTag blockEntityData = chunk.getBlockEntityNbtForSaving(blockpos);
            if (blockEntityData != null) {
                blockEntitiesData.add(blockEntityData);
            }
        }
        tag.put("block_entities", blockEntitiesData);

        ChunkAccess.TicksToSave toSave = chunk.getTicksForSerialization();
        tag.put("block_ticks", toSave.blocks().save(gameTime, (val) -> Registry.BLOCK.getKey(val).toString()));
        tag.put("fluid_ticks", toSave.fluids().save(gameTime, (val) -> Registry.FLUID.getKey(val).toString()));
        tag.put("PostProcessing", ChunkSerializer.packOffsets(chunk.getPostProcessing()));

        CompoundTag heightMapTag = new CompoundTag();
        for(Map.Entry<Heightmap.Types, Heightmap> entry : chunk.getHeightmaps()) {
            if (chunk.getStatus().heightmapsAfter().contains(entry.getKey())) {
                heightMapTag.put(entry.getKey().getSerializationKey(), new LongArrayTag(entry.getValue().getRawData()));
            }
        }

        tag.put("Heightmaps", heightMapTag);
        return tag;
    }
}