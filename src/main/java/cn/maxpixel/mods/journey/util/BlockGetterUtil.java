package cn.maxpixel.mods.journey.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Optional;

public class BlockGetterUtil { // #8654
    public static <T extends BlockEntity> Optional<T> getExistingBlockEntity(BlockGetter getter, BlockPos pos, Class<T> type) {
        if (getter instanceof LevelChunk lc) {
            return Optional.ofNullable(lc.getBlockEntities().get(pos))
                    .filter(type::isInstance)
                    .map(type::cast);
        }
        if (getter instanceof Level level && level.hasChunkAt(pos)) {
            return getExistingBlockEntity(level.getChunkAt(pos), pos, type);
        }
        if (getter instanceof ImposterProtoChunk chunk) {
            return getExistingBlockEntity(chunk.getWrapped(), pos, type);
        }
        return Optional.empty();
    }
}