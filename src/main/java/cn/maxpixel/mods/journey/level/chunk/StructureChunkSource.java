package cn.maxpixel.mods.journey.level.chunk;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.annotation.CalledOn;
import cn.maxpixel.mods.journey.level.LevelResources;
import cn.maxpixel.mods.journey.level.StructureLevel;
import cn.maxpixel.mods.journey.network.clientbound.ClientboundChunkUpdatePacket;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.BooleanSupplier;

public class StructureChunkSource extends ChunkSource {
    private final StructureLevel level;
    private final boolean isClientSide;
    private final Storage storage;
    private final LevelLightEngine lightEngine;

    public StructureChunkSource(StructureLevel level) {
        this.level = level;
        this.isClientSide = level.isClientSide;
        this.storage = isClientSide ? new ClientStorage() : new ServerStorage();
        this.lightEngine = new LevelLightEngine(this, true, level.dimensionType().hasSkyLight());
    }

    @Nullable
    @Override
    public LevelChunk getChunk(int pChunkX, int pChunkZ, ChunkStatus pRequiredStatus, boolean pLoad) {
        return storage.getChunk(pChunkX, pChunkZ, pRequiredStatus, pLoad);
    }

    @Override
    public void tick(BooleanSupplier hasTimeLeft, boolean tickChunks) { // TODO: More things
        if (tickChunks) {
            storage.tick();
        }
    }

    @Override
    public String gatherStats() {
        return Integer.toString(getLoadedChunksCount());
    }

    @Override
    public int getLoadedChunksCount() {
        return storage.getLoadedChunks().size();
    }

    public ObjectCollection<LevelChunk> getLoadedChunks() {
        return storage.getLoadedChunks().values();
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return lightEngine;
    }

    @Override
    public BlockGetter getLevel() {
        return level;
    }

    @CalledOn(CalledOn.Side.CLIENT)
    public void updateClientChunks(ClientboundLevelChunkPacketData chunkData, int chunkX, int chunkZ) {
        storage.updateClientChunks(chunkData, chunkX, chunkZ);
    }

    @CalledOn(CalledOn.Side.SERVER)
    public void writeChunks(FriendlyByteBuf buf) {
        storage.writeChunks(buf);
    }

    @CalledOn(CalledOn.Side.CLIENT)
    public void readChunks(FriendlyByteBuf buf) {
        storage.readChunks(buf);
    }

    @Override
    public void close() {
        storage.save();
    }

    private interface Storage {
        @Nullable LevelChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load);

        Long2ObjectOpenHashMap<LevelChunk> getLoadedChunks();

        @CalledOn(CalledOn.Side.CLIENT)
        default void updateClientChunks(ClientboundLevelChunkPacketData chunkData, int chunkX, int chunkZ) {
        }

        @CalledOn(CalledOn.Side.SERVER)
        void writeChunks(FriendlyByteBuf buf);

        @CalledOn(CalledOn.Side.CLIENT)
        void readChunks(FriendlyByteBuf buf);

        void tick();

        void save();
    }

    private class ClientStorage implements Storage {
        private final Long2ObjectOpenHashMap<LevelChunk> loadedChunks = new Long2ObjectOpenHashMap<>();

        @Override
        public LevelChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
            return loadedChunks.get(ChunkPos.asLong(chunkX, chunkZ));
        }

        @Override
        public Long2ObjectOpenHashMap<LevelChunk> getLoadedChunks() {
            return loadedChunks;
        }

        @Override
        public void updateClientChunks(ClientboundLevelChunkPacketData chunkData, int chunkX, int chunkZ) {
            long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
            LevelChunk chunk = loadedChunks.computeIfAbsent(chunkPos, pos -> new LevelChunk(level, new ChunkPos(pos)));
            if (chunk.getPos().toLong() != chunkPos) {
                chunk = new LevelChunk(level, new ChunkPos(chunkX, chunkZ));
            }
            chunk.replaceWithPacketData(chunkData.getReadBuffer(), chunkData.getHeightmaps(),
                    chunkData.getBlockEntitiesTagsConsumer(chunkX, chunkZ));
            loadedChunks.put(chunkPos, chunk);
        }

        @Override
        public void writeChunks(FriendlyByteBuf buf) {
        }

        @Override
        public void readChunks(FriendlyByteBuf buf) {
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                long chunkPos = buf.readLong();
                int x = ChunkPos.getX(chunkPos);
                int z = ChunkPos.getZ(chunkPos);
                var data = new ClientboundLevelChunkPacketData(buf, x, z);
                updateClientChunks(data, x, z);
            }
        }

        @Override
        public void tick() {
        }

        @Override
        public void save() {
        }
    }

    private class ServerStorage implements Storage {
        private final ChunkStorage chunkStorage = new ChunkStorage(
                level.getServer().getWorldPath(LevelResources.STRUCTURES).resolve(level.getStructureId().toString()),
                level.getServer().getFixerUpper(), true// TODO: Change this?
        );
        private final Long2ObjectOpenHashMap<LevelChunk> loadedChunks = new Long2ObjectOpenHashMap<>();

        private long lastInhabitedUpdate;

        public ServerStorage() {
            int maxX = SectionPos.blockToSectionCoord(level.getBox().maxX());
            int maxZ = SectionPos.blockToSectionCoord(level.getBox().maxZ());
            for (int x = SectionPos.blockToSectionCoord(level.getBox().minX()); x < maxX; x++) {
                for (int z = SectionPos.blockToSectionCoord(level.getBox().minZ()); z < maxZ; z++) {
                    getChunk(x, z, ChunkStatus.FULL, true);
                }
            }
        }

        @Override
        public LevelChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) { // TODO: ChunkStatus, load
            return loadedChunks.computeIfAbsent(ChunkPos.asLong(chunkX, chunkZ), this::tryLoadingChunk);
        }

        private LevelChunk tryLoadingChunk(long chunkPos) {
            ChunkPos pos = new ChunkPos(chunkPos);
            try {
                CompoundTag data = chunkStorage.read(pos);
                if (data == null) {
                    return new LevelChunk(level, pos);
                }
                return ChunkLoader.loadChunk(level, pos, data);
            } catch (IOException e) {
                JourneyMod.LOGGER.error("Error loading chunk of structure {}", level.getStructureId());
                return new LevelChunk(level, pos);
            }
        }

        public void save() {
            loadedChunks.values().forEach(chunk -> {
                if (chunk.isUnsaved()) {
                    chunkStorage.write(chunk.getPos(), ChunkLoader.saveChunk(level, chunk));
                    chunk.setUnsaved(false);
                }
            });
        }

        @Override
        public Long2ObjectOpenHashMap<LevelChunk> getLoadedChunks() {
            return loadedChunks;
        }

        @Override
        public void writeChunks(FriendlyByteBuf buf) {
            buf.writeInt(loadedChunks.size());
            loadedChunks.values().forEach(chunk -> {
                buf.writeLong(chunk.getPos().toLong());
                new ClientboundLevelChunkPacketData(chunk).write(buf);
            });
        }

        @Override
        public void readChunks(FriendlyByteBuf buf) {
        }

        @Override
        public void tick() {
            long gameTime = level.getGameTime();
            long delta = gameTime - lastInhabitedUpdate;
            this.lastInhabitedUpdate = gameTime;
            ProfilerFiller profiler = level.getProfiler();
            int randomTickSpeed = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);

            profiler.push("tickChunks");
            loadedChunks.values().forEach(chunk -> {// TODO: ice and snow
                ChunkPos chunkPos = chunk.getPos();
                int minX = chunkPos.getMinBlockX();
                int minZ = chunkPos.getMinBlockZ();
                profiler.push("tickBlocks");
                chunk.incrementInhabitedTime(delta);
                if (randomTickSpeed > 0) {
                    for(LevelChunkSection section : chunk.getSections()) {
                        if (section.isRandomlyTicking()) {
                            int bottomBlockY = section.bottomBlockY();
                            for(int k = 0; k < randomTickSpeed; ++k) {
                                BlockPos randomTickPos = level.getBlockRandomPos(minX, bottomBlockY, minZ, 15);
                                profiler.push("randomTick");
                                BlockState blockstate = section.getBlockState(randomTickPos.getX() - minX, randomTickPos.getY() - bottomBlockY, randomTickPos.getZ() - minZ);
//                                if (blockstate.isRandomlyTicking()) {
//                                    blockstate.randomTick(level, randomTickPos, level.random);
//                                }FIXME: Block random tick

                                FluidState fluidstate = blockstate.getFluidState();
                                if (fluidstate.isRandomlyTicking()) {
                                    fluidstate.randomTick(level, randomTickPos, level.random);
                                }

                                profiler.pop();
                            }
                        }
                    }
                }
                profiler.pop();
                if (chunk.isUnsaved()) {
                    chunkStorage.write(chunk.getPos(), ChunkLoader.saveChunk(level, chunk));
                    chunk.setUnsaved(false);
                    ClientboundChunkUpdatePacket.send(level.getEntity(), chunkPos.x, chunkPos.z, chunk);
                }
            });
            profiler.pop();
        }
    }
}