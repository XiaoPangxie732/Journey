package cn.maxpixel.mods.journey.block.entity;

import cn.maxpixel.mods.journey.registries.BlockEntityRegistry;
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

public class ControllerBlockEntity extends BlockEntity {
    private static final String START_KEY = "Start";
    private static final String SIZE_KEY = "Size";
    private static final String BUILDING_KEY = "Building";

    private BlockPos.MutableBlockPos start = new BlockPos.MutableBlockPos(0, 0, 0);
    private Vec3i size = MathUtil.ONE;
    private boolean building = true;

    public ControllerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.CONTROLLER.get(), worldPosition, blockState);
    }

    public void set(int x, int y, int z, int xLen, int yLen, int zLen) {
        BlockPos pos = getBlockPos();
        start.set(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        size = new Vec3i(xLen, yLen, zLen);
        setChanged();
    }

    public BlockPos.MutableBlockPos getStart() {
        return start;
    }

    public Vec3i getSize() {
        return size;
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
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putIntArray(START_KEY, new int[] {start.getX(), start.getY(), start.getZ()});
        tag.putIntArray(SIZE_KEY, new int[] {size.getX(), size.getY(), size.getZ()});
        if (building) {
            tag.putBoolean(BUILDING_KEY, true);
        }
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