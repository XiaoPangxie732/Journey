package cn.maxpixel.mods.journey.entity;

import cn.maxpixel.mods.journey.annotation.CalledOn;
import cn.maxpixel.mods.journey.level.StructureLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

import static cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity.*;

public class StructureEntity extends Entity implements IEntityAdditionalSpawnData {
    public static final String NAME = "structure";
    private static final String INITIALIZED_KEY = "Initialized";
    private static final String ORIGIN_POS_KEY = "OriginPosition";

    private UUID structureId;
    private boolean initialized;
    private StructureLevel structureLevel;
    private BlockPos originPos;
    private Vec3 originRelative;

    public StructureEntity(EntityType<StructureEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @CalledOn(CalledOn.Side.SERVER)
    public void createStructureLevel(Level parent, BlockPos start, Vec3i size, BlockPos controllerPos, Iterable<BlockPos> blocks) {
        this.structureLevel = new StructureLevel(parent, start, size, this);
        this.originPos = controllerPos;
        structureLevel.moveBlocks(blocks, controllerPos);
        this.initialized = true;
    }

    public void setStructureId(UUID id) {
        this.structureId = id;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public StructureLevel getStructureLevel() {
        return structureLevel;
    }

    public @Nullable UUID getStructureId() {
        if (!level.isClientSide && structureId == null) {
            throw new IllegalStateException("Structure ID not present");
        }
        return structureId;
    }

    public Vec3 getOriginRelative() {
        return originRelative == null ? Vec3.ZERO : originRelative;
    }

    @CalledOn(CalledOn.Side.CLIENT)
    public void updateClientChunks(ClientboundLevelChunkPacketData chunkData, int chunkX, int chunkZ) {
        structureLevel.updateClientChunks(chunkData, chunkX, chunkZ);
    }

    public int getPosXInParent(int x) {
        return originPos.getX() + x;
    }

    public int getPosYInParent(int y) {
        return originPos.getY() + y;
    }

    public int getPosZInParent(int z) {
        return originPos.getZ() + z;
    }

    public BlockPos getPosInParent(BlockPos pos) {
        return originPos.offset(pos);
    }

    @Override
    public void remove(RemovalReason pReason) {// TODO: Delete structure file when killed
        super.remove(pReason);
        structureLevel.close();
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.fixed(Math.max(structureLevel.size.getX(), structureLevel.size.getZ()),
                structureLevel.size.getY());
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 delta = position().subtract(xOld, yOld, zOld);
        originPos.offset(delta.x, delta.y, delta.z);
        originRelative = position().subtract(originPos.getX(), originPos.getY(), originPos.getZ());
        structureLevel.tick();
    }

    @Override
    protected void outOfWorld() {
    }

    @Override
    public void push(Entity pEntity) {
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public boolean startRiding(Entity pVehicle, boolean pForce) {
        return false;
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return false;
    }

    @Override
    public HitResult pick(double pHitDistance, float pPartialTicks, boolean pHitFluids) {//TODO: Pick blocks
        return super.pick(pHitDistance, pPartialTicks, pHitFluids);
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(INITIALIZED_KEY, Tag.TAG_ANY_NUMERIC)) {
            this.initialized = tag.getBoolean(INITIALIZED_KEY);
            if (initialized) {
                setStructureId(tag.getUUID(STRUCTURE_ID_KEY));
                int[] startPos = tag.getIntArray(START_KEY);
                int[] size = tag.getIntArray(SIZE_KEY);
                int[] originPos = tag.getIntArray(ORIGIN_POS_KEY);
                this.originPos = new BlockPos(originPos[0], originPos[1], originPos[2]);
                this.structureLevel = new StructureLevel(level, new BlockPos(startPos[0], startPos[1], startPos[2]),
                        new Vec3i(size[0], size[1], size[2]), this);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (initialized) {
            tag.putBoolean(INITIALIZED_KEY, true);
            tag.putUUID(STRUCTURE_ID_KEY, getStructureId());
            BlockPos start = structureLevel.start;
            Vec3i size = structureLevel.size;
            tag.putIntArray(START_KEY, new int[] {start.getX(), start.getY(), start.getZ()});
            tag.putIntArray(SIZE_KEY, new int[] {size.getX(), size.getY(), size.getZ()});
            tag.putIntArray(ORIGIN_POS_KEY, new int[] {originPos.getX(), originPos.getY(), originPos.getZ()});
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        if (initialized) {
            buffer.writeBoolean(true);
            buffer.writeBlockPos(originPos)
                    .writeBlockPos(structureLevel.start)
                    .writeVarInt(structureLevel.size.getX())
                    .writeVarInt(structureLevel.size.getY())
                    .writeVarInt(structureLevel.size.getZ());
            structureLevel.getChunkSource().writeChunks(buffer);
        } else buffer.writeBoolean(false);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        if (additionalData.readBoolean()) {
            this.originPos = additionalData.readBlockPos();
            structureLevel = new StructureLevel(level, additionalData.readBlockPos(), new Vec3i(additionalData.readVarInt(),
                    additionalData.readVarInt(), additionalData.readVarInt()), this);
            structureLevel.getChunkSource().readChunks(additionalData);
        }
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {// TODO: Can we change dimensions in the future?
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}