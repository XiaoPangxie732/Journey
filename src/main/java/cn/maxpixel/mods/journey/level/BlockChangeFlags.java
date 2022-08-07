package cn.maxpixel.mods.journey.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockChangeFlags {
    int DO_UPDATE = 1;
    int SEND_TO_CLIENT = 2;
    int DONT_RERENDER = 4;
    int RERENDER_ON_MAIN_THREAD = 8;
    int DONT_UPDATE_NEIGHBOR_SHAPES = 16;
    /**
     * This flag is ignored in {@link net.minecraft.world.level.Level#setBlock(BlockPos, BlockState, int)}
     */
    int DONT_SPAWN_DROPS_WHEN_UPDATING_NEIGHBOR_SHAPE = 32;
    int BEING_MOVED = 64;
    int DONT_UPDATE_LIGHT = 128;
}