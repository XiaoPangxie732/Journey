package cn.maxpixel.mods.journey.block.entity;

import cn.maxpixel.mods.journey.level.StructureLevel;
import cn.maxpixel.mods.journey.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class CreativeEngineBlockEntity extends BlockEntity {
    public CreativeEngineBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.CREATIVE_ENGINE, pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CreativeEngineBlockEntity blockEntity) {
        if (level instanceof StructureLevel structureLevel) {
            if (structureLevel.isClientSide) {
                Random random = structureLevel.random;
                for (int i = 0; i < 5; i++) {
                    structureLevel.addParticle(ParticleTypes.CLOUD, pos.getX() + random.nextDouble(), pos.getY() - 1, pos.getZ() + random.nextDouble(), 0, -.5, 0);
                }
            }
            structureLevel.getEntity().setDeltaMovement(structureLevel.getEntity().getDeltaMovement().add(0, 0.041, 0));
        }
    }
}