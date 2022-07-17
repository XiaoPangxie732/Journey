package cn.maxpixel.mods.journey.block;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.client.screens.ScreenManager;
import cn.maxpixel.mods.journey.util.BlockGetterUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ControllerBlock extends BaseEntityBlock {
    public static final String NAME = "controller";

    public ControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ControllerBlockEntity(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return BlockGetterUtil.getExistingBlockEntity(level, pos, ControllerBlockEntity.class)
                .map(blockEntity -> {
                    if (level.isClientSide) {
                        ScreenManager.openControllerBlockConfigureScreen(blockEntity);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }).orElse(InteractionResult.PASS);
    }
}