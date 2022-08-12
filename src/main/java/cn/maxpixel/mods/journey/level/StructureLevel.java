package cn.maxpixel.mods.journey.level;

import cn.maxpixel.mods.journey.annotation.CalledOn;
import cn.maxpixel.mods.journey.entity.StructureEntity;
import cn.maxpixel.mods.journey.level.chunk.StructureChunkSource;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class StructureLevel extends Level {
    private static final LevelEntityGetter<Entity> EMPTY_ENTITY_GETTER = new LevelEntityGetter<>() {
        @Nullable
        @Override
        public Entity get(int p_156931_) {
            return null;
        }

        @Nullable
        @Override
        public Entity get(UUID pUuid) {
            return null;
        }

        @Override
        public Iterable<Entity> getAll() {
            return ObjectIterators::emptyIterator;
        }

        @Override
        public <U extends Entity> void get(EntityTypeTest<Entity, U> p_156935_, Consumer<U> p_156936_) {
        }

        @Override
        public void get(AABB pBoundingBox, Consumer<Entity> p_156938_) {
        }

        @Override
        public <U extends Entity> void get(EntityTypeTest<Entity, U> p_156932_, AABB p_156933_, Consumer<U> p_156934_) {
        }
    };
    private static final int MAX_BLOCKS_MOVED_PER_TICK = 4096;

    private final OnTickExecutor EXECUTOR = new OnTickExecutor();
    public final Level parent;
    public final BlockPos start;
    public final Vec3i size;
    private final StructureEntity entity;
    private final BoundingBox box;
    private final LevelTicks<Block> blockTicks;
    private final LevelTicks<Fluid> fluidTicks;
    private final StructureChunkSource chunkSource;

    public StructureLevel(Level parent, BlockPos start, Vec3i size, StructureEntity entity) {
        super(new WrappedLevelData(parent.getLevelData()), parent.dimension(), parent.dimensionTypeRegistration(), parent.getProfilerSupplier(), parent.isClientSide, parent.isDebug(), parent.getBiomeManager().biomeZoomSeed);
        this.parent = parent;
        this.start = start;
        this.size = size;
        this.entity = entity;
        this.box = new BoundingBox(
                start.getX(), start.getY(), start.getZ(),
                start.getX() + size.getX() - 1, start.getY() + size.getY() - 1, start.getZ() + size.getZ() - 1
        );
        this.chunkSource = new StructureChunkSource(this);
        if (!isClientSide) { // TODO: Tick check
            blockTicks = new LevelTicks<>((chunkPos) -> true, getProfilerSupplier());
            fluidTicks = new LevelTicks<>((chunkPos) -> true, getProfilerSupplier());
        } else {
            blockTicks = null;
            fluidTicks = null;
        }
        chunkSource.loadChunks();
        getWorldBorder().setSize(Math.max(size.getX(), size.getZ()));
    }

    @CalledOn(CalledOn.Side.SERVER)
    public void moveBlocks(Iterable<BlockPos> blocks, BlockPos controllerPos) {
        int size = this.size.getX() * this.size.getY() * this.size.getZ();

        Iterator<BlockPos> iterator = blocks.iterator();
        if (size <= MAX_BLOCKS_MOVED_PER_TICK) {
            EXECUTOR.execute(() -> startMovingBlocks(iterator, controllerPos, true));
        } else {
            for (int i = size; i > MAX_BLOCKS_MOVED_PER_TICK; i -= MAX_BLOCKS_MOVED_PER_TICK) {
                EXECUTOR.execute(() -> startMovingBlocks(iterator, controllerPos, false));
            }
            EXECUTOR.execute(() -> startMovingBlocks(iterator, controllerPos, true));
        }

        BoundingBox boundingBox = box.moved(controllerPos.getX(), controllerPos.getY(), controllerPos.getZ());
        ((LevelTicks<Block>) parent.getBlockTicks()).clearArea(boundingBox);
        ((LevelTicks<Fluid>) parent.getFluidTicks()).clearArea(boundingBox);

        Iterator<BlockPos> iterator1 = blocks.iterator();
        if (size <= MAX_BLOCKS_MOVED_PER_TICK) {
            EXECUTOR.execute(() -> completeMovingBlocks(iterator1, controllerPos, true));
        } else {
            for (int i = size; i > MAX_BLOCKS_MOVED_PER_TICK; i -= MAX_BLOCKS_MOVED_PER_TICK) {
                EXECUTOR.execute(() -> completeMovingBlocks(iterator1, controllerPos, false));
            }
            EXECUTOR.execute(() -> completeMovingBlocks(iterator1, controllerPos, true));
        }
    }

    @CalledOn(CalledOn.Side.SERVER)
    private void startMovingBlocks(Iterator<BlockPos> blocks, BlockPos controllerPos, boolean unlimited) {
        if (unlimited) {
            while (blocks.hasNext()) {
                BlockPos relativePos = blocks.next();
                doMoveBlocks(relativePos, relativePos.offset(controllerPos));
            }
        } else {
            for (int i = 0; i < MAX_BLOCKS_MOVED_PER_TICK; i++) {
                if (!blocks.hasNext()) break;
                BlockPos relativePos = blocks.next();
                doMoveBlocks(relativePos, relativePos.offset(controllerPos));
            }
        }
    }

    @CalledOn(CalledOn.Side.SERVER)
    private void doMoveBlocks(BlockPos relativePos, BlockPos levelPos) {
        BlockState originalState = parent.getBlockState(levelPos);
        BlockEntity originalBlockEntity = parent.getBlockEntity(levelPos);

        if (originalState.getMaterial() != Material.AIR) {
            setBlock(relativePos, originalState, BlockChangeFlags.SEND_TO_CLIENT + BlockChangeFlags.DONT_UPDATE_NEIGHBOR_SHAPES);
            if (originalBlockEntity != null) {
                CompoundTag data = originalBlockEntity.saveWithoutMetadata();
                Clearable.tryClear(originalBlockEntity);
                BlockEntity newBlockEntity = getBlockEntity(relativePos);
                if (newBlockEntity != null) {
                    newBlockEntity.load(data);
                    newBlockEntity.setChanged();
                }
            }
            parent.setBlock(levelPos, Blocks.BARRIER.defaultBlockState(), BlockChangeFlags.SEND_TO_CLIENT + BlockChangeFlags.DONT_UPDATE_NEIGHBOR_SHAPES);
        }
    }

    @CalledOn(CalledOn.Side.SERVER)
    private void completeMovingBlocks(Iterator<BlockPos> blocks, BlockPos controllerPos, boolean unlimited) {
        if (unlimited) {
            while (blocks.hasNext()) {
                BlockPos relativePos = blocks.next();
                doneMoveBlocks(relativePos, relativePos.offset(controllerPos));
            }
        } else {
            for (int i = 0; i < MAX_BLOCKS_MOVED_PER_TICK; i++) {
                if (!blocks.hasNext()) break;
                BlockPos relativePos = blocks.next();
                doneMoveBlocks(relativePos, relativePos.offset(controllerPos));
            }
        }
    }

    @CalledOn(CalledOn.Side.SERVER)
    private void doneMoveBlocks(BlockPos relativePos, BlockPos levelPos) {
        parent.setBlockAndUpdate(levelPos, Blocks.AIR.defaultBlockState());
        BlockState state = getBlockState(relativePos);
        markAndNotifyBlock(relativePos, getChunkAt(relativePos), state, state, BlockChangeFlags.DO_UPDATE, 512);
    }

    public @Nullable UUID getStructureId() {
        return entity.getStructureId();
    }

    public BoundingBox getBox() {
        return box;
    }

    public StructureEntity getEntity() {
        return entity;
    }

    @CalledOn(CalledOn.Side.CLIENT)
    public void updateClientChunks(ClientboundLevelChunkPacketData chunkData, int chunkX, int chunkZ) {
        chunkSource.updateClientChunks(chunkData, chunkX, chunkZ);
    }

    public void tick() {
        ProfilerFiller profiler = getProfiler();
        profiler.push("task executor");
        EXECUTOR.tick();
        profiler.popPush("world border");
        getWorldBorder().setCenter(-entity.getOriginRelative().x, -entity.getOriginRelative().z);
        profiler.popPush("chunks");
        chunkSource.tick(() -> true, true);
        profiler.popPush("block entities");
        tickBlockEntities();
        profiler.pop();
    }

    @Override
    public int getMinBuildHeight() {
        return start.getY();
    }

    @Override
    public int getHeight() {
        return size.getY();
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return parent.getServer();
    }

    @Override
    public boolean isInWorldBounds(BlockPos pos) {
        return box.isInside(pos);
    }

    @Override
    public void blockUpdated(BlockPos pos, Block block) {
        if (!isClientSide) {
            updateNeighborsAt(pos, block);
        }
    }

    @Override
    public void sendBlockUpdated(BlockPos pPos, BlockState pOldState, BlockState pNewState, int pFlags) { // FIXME: better ways
        this.getChunkAt(pPos).setUnsaved(true);
    }

    @Override
    public int getHeight(Heightmap.Types type, int x, int z) {
        int height;
        if (x >= box.minX() && z >= box.minZ() && x <= box.maxX() && z <= box.maxZ() &&
                hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z))) {
            height = getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)).getHeight(type, x & 15, z & 15) + 1;
        } else {
            height = getMinBuildHeight();
        }

        return height;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (!box.isInside(pos)) return Blocks.VOID_AIR.defaultBlockState();
        return super.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (!box.isInside(pos)) return Fluids.EMPTY.defaultFluidState();
        return super.getFluidState(pos);
    }

    @Override
    public void playSound(@Nullable Player pPlayer, double pX, double pY, double pZ, SoundEvent pSound, SoundSource pCategory, float pVolume, float pPitch) {
    }

    @Override
    public void playSound(@Nullable Player pPlayer, Entity pEntity, SoundEvent pEvent, SoundSource pCategory, float pVolume, float pPitch) {
    }

    @Override
    public String gatherChunkSourceStats() {
        return "";
    }

    @Nullable
    @Override
    public Entity getEntity(int pId) {
        return parent.getEntity(pId);
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(String pMapName) {
        return null;
    }

    @Override
    public void setMapData(String pMapId, MapItemSavedData pData) {
    }

    @Override
    public int getFreeMapId() {
        return -1;
    }

    @Override
    public void destroyBlockProgress(int pBreakerId, BlockPos pPos, int pProgress) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return parent.getScoreboard();
    }

    @Override
    public int getSkyDarken() {
        return parent.getSkyDarken();
    }

    @Override
    public void setSkyFlashTime(int pTimeFlash) {
        parent.setSkyFlashTime(pTimeFlash);
    }

    @Override
    public void sendPacketToServer(Packet<?> pPacket) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return parent.getRecipeManager();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return EMPTY_ENTITY_GETTER;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        if (isClientSide) return BlackholeTickAccess.emptyLevelList();
        return blockTicks;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        if (isClientSide) return BlackholeTickAccess.emptyLevelList();
        return fluidTicks;
    }

    @Override
    public StructureChunkSource getChunkSource() {
        return chunkSource;
    }

    @Override
    public void close() {
        chunkSource.close();
    }

    @Override
    public void levelEvent(@Nullable Player pPlayer, int pType, BlockPos pPos, int pData) {// TODO
    }

    @Override
    public void gameEvent(@Nullable Entity pEntity, GameEvent pEvent, BlockPos pPos) {// TODO
    }

    @Override
    public RegistryAccess registryAccess() {
        return parent.registryAccess();
    }

    @Override
    public float getShade(Direction pDirection, boolean pShade) {
        return parent.getShade(pDirection, pShade);
    }

    @Override
    public List<? extends Player> players() {
        return parent.players();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity pEntity, AABB pBoundingBox, Predicate<? super Entity> pPredicate) {
        if (pEntity == null || pEntity.level == this) {
            pBoundingBox.move(entity.getOriginPos());
        }
        return parent.getEntities(pEntity, pBoundingBox, pPredicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> pEntityTypeTest, AABB pArea, Predicate<? super T> pPredicate) {
        return parent.getEntities(pEntityTypeTest, pArea, pPredicate);
    }

    @Override
    public boolean addFreshEntity(Entity entity) {// FIXME: Is this correct?
        entity.setPos(entity.position().add(this.entity.getOriginPos()));
        entity.level = parent;
        return parent.addFreshEntity(entity);
    }

    @Override
    public void addParticle(ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        Vec3 origin = entity.getOriginPos();
        parent.addParticle(pParticleData, pX + origin.x, pY + origin.y, pZ + origin.z, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void addParticle(ParticleOptions pParticleData, boolean pForceAlwaysRender, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        Vec3 origin = entity.getOriginPos();
        parent.addParticle(pParticleData, pForceAlwaysRender, pX + origin.x, pY + origin.y, pZ + origin.z, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        Vec3 origin = entity.getOriginPos();
        parent.addAlwaysVisibleParticle(pParticleData, pX + origin.x, pY + origin.y, pZ + origin.z, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions pParticleData, boolean pIgnoreRange, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        Vec3 origin = entity.getOriginPos();
        parent.addAlwaysVisibleParticle(pParticleData, pIgnoreRange, pX + origin.x, pY + origin.y, pZ + origin.z, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ) {
        int x = QuartPos.fromBlock(entity.getPosXInParent(QuartPos.toBlock(quartX)));
        int y = QuartPos.fromBlock(entity.getPosYInParent(QuartPos.toBlock(quartY)));
        int z = QuartPos.fromBlock(entity.getPosZInParent(QuartPos.toBlock(quartZ)));
        return parent.getNoiseBiome(x, y, z);
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ) {
        int x = QuartPos.fromBlock(entity.getPosXInParent(QuartPos.toBlock(quartX)));
        int y = QuartPos.fromBlock(entity.getPosYInParent(QuartPos.toBlock(quartY)));
        int z = QuartPos.fromBlock(entity.getPosZInParent(QuartPos.toBlock(quartZ)));
        return parent.getUncachedNoiseBiome(x, y, z);
    }

    private static class OnTickExecutor implements Executor {
        private final ObjectArrayFIFOQueue<Runnable> runnables = new ObjectArrayFIFOQueue<>();

        @Override
        public void execute(@NotNull Runnable command) {
            this.runnables.enqueue(command);
        }

        public void tick() {
            if (!runnables.isEmpty()) {
                runnables.dequeue().run();
            }
        }
    }
}