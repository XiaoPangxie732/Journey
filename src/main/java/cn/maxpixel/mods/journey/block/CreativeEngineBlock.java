package cn.maxpixel.mods.journey.block;

import cn.maxpixel.mods.journey.block.entity.CreativeEngineBlockEntity;
import cn.maxpixel.mods.journey.level.StructureLevel;
import cn.maxpixel.mods.journey.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeEngineBlock extends BaseEntityBlock {
    public static final String NAME = "creative_engine";

    public CreativeEngineBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CreativeEngineBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level instanceof StructureLevel ? createTickerHelper(type, BlockEntityRegistry.CREATIVE_ENGINE.get(),
                CreativeEngineBlockEntity::tick) : null;
    }
}