package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.block.ControllerBlock;
import cn.maxpixel.mods.journey.block.CopperWireBlock;
import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.block.entity.CopperWireBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityRegistry {
    public static final BlockEntityType<ControllerBlockEntity> CONTROLLER = Registries.registerBlockEntity(
            ControllerBlock.NAME,
            BlockEntityType.Builder.of(ControllerBlockEntity::new, BlockRegistry.CONTROLLER)
                    .build(null)
    );

    public static final BlockEntityType<CopperWireBlockEntity> COPPER_WIRE = Registries.registerBlockEntity(
            CopperWireBlock.NAME,
            BlockEntityType.Builder.of(CopperWireBlockEntity::new, BlockRegistry.COPPER_WIRE)
                    .build(null)
    );

    static void init() { /* Trigger <clinit> */ }
}