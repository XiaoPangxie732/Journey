package cn.maxpixel.mods.journey.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class BlockGetterUtil { // #8654
    public static <T extends BlockEntity> Optional<T> getExistingBlockEntity(BlockGetter getter, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(getter.getExistingBlockEntity(pos))
                .filter(type::isInstance)
                .map(type::cast);
    }
}