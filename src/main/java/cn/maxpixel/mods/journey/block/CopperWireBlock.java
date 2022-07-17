package cn.maxpixel.mods.journey.block;

import cn.maxpixel.mods.journey.block.entity.CopperWireBlockEntity;
import cn.maxpixel.mods.journey.util.BlockGetterUtil;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CopperWireBlock extends BaseEntityBlock {
    public static final String NAME = "copper_wire";
    public static final short MIN_COUNT = 1;
    public static final short MAX_COUNT = 256;

    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    private static final Byte2ObjectOpenHashMap<VoxelShape> SHAPE_CACHE = Util.make(() -> {
        var map = new Byte2ObjectOpenHashMap<VoxelShape>();
        for (byte b = 0; b < 16; map.put(b, box(0, 0, 0, 16, ++b, 16)));
        return map;
    });

    public CopperWireBlock(Properties p_49224_) {
        super(p_49224_);
        registerDefaultState(stateDefinition.any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CopperWireBlockEntity(pPos, pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return BlockGetterUtil.getExistingBlockEntity(pLevel, pPos, CopperWireBlockEntity.class)
                .map(cw -> {
                    short count = cw.getCount();
                    if (count >= MAX_COUNT) return Shapes.block();
                    return SHAPE_CACHE.get((byte) ((count - 1) / 16));
                }).orElseGet(() -> SHAPE_CACHE.get((byte) 0));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pCurrentPos, BlockPos pNeighborPos) {//TODO
        return super.updateShape(state, direction, neighbor, level, pCurrentPos, pNeighborPos);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return BlockGetterUtil.getExistingBlockEntity(pLevel, pPos, CopperWireBlockEntity.class)
                .map(cw -> {
                    var stack = pPlayer.getItemInHand(pHand);
                    if (!stack.is(asItem())) return InteractionResult.PASS;
                    if (!pPlayer.getAbilities().instabuild) stack.shrink(1);
                    cw.setCount((short) (cw.getCount() + 1));
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }).orElse(InteractionResult.PASS);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        return getSignal(pState, pLevel, pPos, pDirection);
    }

    @Override
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {//TODO
        return super.getSignal(pState, pLevel, pPos, pDirection);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return false;
    }
}