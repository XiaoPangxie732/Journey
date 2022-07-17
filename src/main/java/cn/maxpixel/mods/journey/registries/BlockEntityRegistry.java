package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.block.ControllerBlock;
import cn.maxpixel.mods.journey.block.CopperWireBlock;
import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.block.entity.CopperWireBlockEntity;
import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {
    public static final RegistryObject<BlockEntityType<ControllerBlockEntity>> CONTROLLER = Registries.BLOCK_ENTITIES.register(
            ControllerBlock.NAME,
            () -> BlockEntityType.Builder.of(ControllerBlockEntity::new, BlockRegistry.CONTROLLER.get())
                    .build(DSL.remainderType())
    );

    public static final RegistryObject<BlockEntityType<CopperWireBlockEntity>> COPPER_WIRE = Registries.BLOCK_ENTITIES.register(
            CopperWireBlock.NAME,
            () -> BlockEntityType.Builder.of(CopperWireBlockEntity::new, BlockRegistry.COPPER_WIRE.get())
                    .build(DSL.remainderType())
    );

    static void init() { /* Trigger <clinit> */ }
}