package cn.maxpixel.mods.journey.entity;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.annotation.CalledOn;
import cn.maxpixel.mods.journey.level.StructureLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.UUID;

import static cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity.*;

public class StructureEntity extends Entity implements IEntityAdditionalSpawnData {
    public static final String NAME = "structure";
    private static final String INITIALIZED_KEY = "Initialized";
    private static final String ORIGIN_POS_KEY = "OriginPosition";

    private UUID structureId;
    private boolean initialized;
    private StructureLevel structureLevel;
    private BlockPos originBlockPos;
    private Vec3 originPos;
    private Vec3 originRelative = Vec3.ZERO;

    public StructureEntity(EntityType<StructureEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @CalledOn(CalledOn.Side.SERVER)
    public void createStructureLevel(Level parent, BlockPos start, Vec3i size, BlockPos controllerPos, Iterable<BlockPos> blocks) {
        this.structureLevel = new StructureLevel(parent, start, size, this);
        this.originBlockPos = controllerPos;
        this.originPos = new Vec3(originBlockPos.getX(), originBlockPos.getY(), originBlockPos.getZ());
        reapplyPosition();
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
        return originRelative;
    }

    @CalledOn(CalledOn.Side.CLIENT)
    public void updateClientChunks(ClientboundLevelChunkPacketData chunkData, int chunkX, int chunkZ) {
        structureLevel.updateClientChunks(chunkData, chunkX, chunkZ);
    }

    public int getPosXInParent(int x) {
        return originBlockPos.getX() + x;
    }

    public int getPosYInParent(int y) {
        return originBlockPos.getY() + y;
    }

    public int getPosZInParent(int z) {
        return originBlockPos.getZ() + z;
    }

    public BlockPos getPosInParent(BlockPos pos) {
        return originBlockPos.offset(pos);
    }

    public BlockPos getOriginBlockPos() {
        return originBlockPos;
    }

    public Vec3 getOriginPos() {
        return originPos;
    }

    @Override
    public void remove(RemovalReason reason) {// TODO: Delete structure file when killed
        super.remove(reason);
        if (!reason.shouldDestroy()) {
            structureLevel.getChunkSource().save();
        }
        try {
            structureLevel.close();
        } catch (IOException e) {
            JourneyMod.LOGGER.error("Error closing level", e);
        }
        if (reason.shouldDestroy()) {
            try {
                structureLevel.getChunkSource().delete();
            } catch (IOException e) {
                JourneyMod.LOGGER.error("Error deleting structure file", e);
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.fixed(Math.max(structureLevel.size.getX(), structureLevel.size.getZ()),
                structureLevel.size.getY());
    }


    @Override
    protected AABB makeBoundingBox() {
        if (structureLevel != null) {
            return new AABB(structureLevel.start, structureLevel.start.offset(structureLevel.size))
                    .move(originPos);
        } else return super.makeBoundingBox();
    }

    @Override
    public void tick() {
        super.tick();
        level.getProfiler().push("levelTick");
        structureLevel.tick();
        level.getProfiler().pop();
        setDeltaMovement(getDeltaMovement().add(0, -0.04, 0));// gravity
        move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    public void setPos(double x, double y, double z) {
        Vec3 pos = position();
        if (originPos != null && (firstTick || pos.x != x || pos.y != y || pos.z != z)) {
            originPos = originPos.add(x - pos.x, y - pos.y, z - pos.z);
            originBlockPos = BlockPos.containing(originPos);
            originRelative = originPos.subtract(x, y, z);
        }
        super.setPos(x, y, z);
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
    public boolean canBeCollidedWith() {
//        return false;
        return !isRemoved();
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
                ListTag originPos = tag.getList(ORIGIN_POS_KEY, Tag.TAG_DOUBLE);
                this.originPos = new Vec3(originPos.getDouble(0), originPos.getDouble(1), originPos.getDouble(2));
                this.originBlockPos = BlockPos.containing(this.originPos);
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
            tag.put(ORIGIN_POS_KEY, newDoubleList(originPos.x, originPos.y, originPos.z));
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        if (initialized) {
            buffer.writeBoolean(true)
                    .writeDouble(originPos.x)
                    .writeDouble(originPos.y)
                    .writeDouble(originPos.z);
            buffer.writeBlockPos(structureLevel.start)
                    .writeVarInt(structureLevel.size.getX())
                    .writeVarInt(structureLevel.size.getY())
                    .writeVarInt(structureLevel.size.getZ());
            structureLevel.getChunkSource().writeChunks(buffer);
        } else buffer.writeBoolean(false);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        if (additionalData.readBoolean()) {
            this.originPos = new Vec3(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble());
            this.originBlockPos = BlockPos.containing(this.originPos);
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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}