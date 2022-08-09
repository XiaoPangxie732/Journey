package cn.maxpixel.mods.journey.block.entity;

import cn.maxpixel.mods.journey.entity.StructureEntity;
import cn.maxpixel.mods.journey.registries.BlockEntityRegistry;
import cn.maxpixel.mods.journey.registries.EntityRegistry;
import cn.maxpixel.mods.journey.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ControllerBlockEntity extends BlockEntity {
    public static final String START_KEY = "Start";
    public static final String SIZE_KEY = "Size";
    private static final String BUILDING_KEY = "Building";
    public static final String STRUCTURE_ID_KEY = "StructureID";

    private BlockPos.MutableBlockPos start = new BlockPos.MutableBlockPos(0, 0, 0);
    private Vec3i size = MathUtil.ONE;
    private boolean building = true;
    private UUID structureId = UUID.randomUUID();

    public ControllerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.CONTROLLER, worldPosition, blockState);
    }

    public void set(int x, int y, int z, int xLen, int yLen, int zLen) {
        BlockPos pos = getBlockPos();
        start.set(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        size = new Vec3i(xLen, yLen, zLen);
    }

    public void setSize(Vec3i size) {
        this.size = size;
    }

    public BlockPos.MutableBlockPos getStart() {
        return start;
    }

    public Vec3i getSize() {
        return size;
    }

    public boolean isBuilding() {
        return building;
    }

    public void assemble() {
        if (!building || level == null || level.isClientSide) return;

        StructureEntity entity = EntityRegistry.STRUCTURE.create(level);
        entity.moveTo(
                worldPosition.getX() + start.getX() + size.getX() / 2d,
                worldPosition.getY() + start.getY(),
                worldPosition.getZ() + start.getZ() + size.getZ() / 2d,
                0, 0
        );
        entity.setStructureId(structureId);
        entity.createStructureLevel(level, start, size, worldPosition, BlockPos.betweenClosed(start.getX(), start.getY(), start.getZ(),
                start.getX() + size.getX() - 1, start.getY() + size.getY() - 1, start.getZ() + size.getZ() - 1));
        level.addFreshEntity(entity);
    }

    public void disassemble() { // TODO: disassemble
        if (building || level.isClientSide) return;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains(START_KEY, Tag.TAG_INT_ARRAY)) {
            int[] startPos = tag.getIntArray(START_KEY);
            if (startPos.length >= 3) {
                this.start = new BlockPos.MutableBlockPos(startPos[0], startPos[1], startPos[2]);
            } else {
                this.start = new BlockPos.MutableBlockPos(0, 0, 0);
            }
        } else {
            this.start = new BlockPos.MutableBlockPos(0, 0, 0);
        }

        if (tag.contains(SIZE_KEY, Tag.TAG_INT_ARRAY)) {
            int[] size = tag.getIntArray(SIZE_KEY);
            if (size.length >= 3) {
                this.size = new Vec3i(Math.max(size[0], 1), Math.max(size[1], 1), Math.max(size[2], 1));
            } else {
                this.size = MathUtil.ONE;
            }
        } else {
            this.size = MathUtil.ONE;
        }

        if (tag.contains(BUILDING_KEY, Tag.TAG_ANY_NUMERIC)) {
            try {
                this.building = tag.getBoolean(BUILDING_KEY);
            } catch (IllegalArgumentException | NullPointerException e) {
                this.building = true;
            }
        } else {
            this.building = true;
        }

        if (tag.hasUUID(STRUCTURE_ID_KEY)) {
            this.structureId = tag.getUUID(STRUCTURE_ID_KEY);
        } else {
            this.structureId = UUID.randomUUID();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putIntArray(START_KEY, new int[] {start.getX(), start.getY(), start.getZ()});
        tag.putIntArray(SIZE_KEY, new int[] {size.getX(), size.getY(), size.getZ()});
        tag.putBoolean(BUILDING_KEY, building);
        tag.putUUID(STRUCTURE_ID_KEY, structureId);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}