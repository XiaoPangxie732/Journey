package cn.maxpixel.mods.journey.block.entity;

import cn.maxpixel.mods.journey.registries.BlockEntityRegistry;
import cn.maxpixel.mods.journey.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class ControllerBlockEntity extends BlockEntity {
    private static final String START_KEY = "Start";
    private static final String SIZE_KEY = "Size";
    private static final String STATE_KEY = "State";
    private ControllerState state = ControllerState.BUILDING;

    private BlockPos.MutableBlockPos start = getBlockPos().mutable();
    private Vec3i size = MathUtil.ONE;

    public ControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.CONTROLLER.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains(START_KEY, Tag.TAG_INT_ARRAY)) {
            int[] startPos = tag.getIntArray(START_KEY);
            if (startPos.length >= 3) {
                this.start = new BlockPos.MutableBlockPos(startPos[0], startPos[1], startPos[2]);
            } else {
                this.start = getBlockPos().mutable();
            }
        } else {
            this.start = getBlockPos().mutable();
        }

        if (tag.contains(SIZE_KEY, Tag.TAG_INT_ARRAY)) {
            int[] size = tag.getIntArray(START_KEY);
            if (size.length >= 3) {
                this.size = new Vec3i(Math.max(size[0], 1), Math.max(size[1], 1), Math.max(size[2], 1));
            } else {
                this.size = MathUtil.ONE;
            }
        } else {
            this.size = MathUtil.ONE;
        }

        if (tag.contains(STATE_KEY, Tag.TAG_STRING)) {
            try {
                this.state = Objects.requireNonNull(ControllerState.valueOf(tag.getString(STATE_KEY)));
            } catch (IllegalArgumentException | NullPointerException e) {
                this.state = ControllerState.BUILDING;
            }
        } else {
            this.state = ControllerState.BUILDING;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putIntArray(START_KEY, new int[] {start.getX(), start.getY(), start.getZ()});
        tag.putIntArray(SIZE_KEY, new int[] {size.getX(), size.getY(), size.getZ()});
        tag.putString(STATE_KEY, state.name());
    }

    public enum ControllerState {
        BUILDING,
        CREATED
    }
}