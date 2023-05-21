package cn.maxpixel.mods.journey.registry;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.block.ControllerBlock;
import cn.maxpixel.mods.journey.block.CopperWireBlock;
import cn.maxpixel.mods.journey.block.CreativeEngineBlock;
import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.block.entity.CopperWireBlockEntity;
import cn.maxpixel.mods.journey.block.entity.CreativeEngineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JourneyMod.MODID);

    public static final RegistryObject<BlockEntityType<ControllerBlockEntity>> CONTROLLER = BLOCK_ENTITY_TYPES.register(
            ControllerBlock.NAME,
            () -> BlockEntityType.Builder.of(ControllerBlockEntity::new, BlockRegistry.CONTROLLER.get())
                    .build(null)
    );

    public static final RegistryObject<BlockEntityType<CopperWireBlockEntity>> COPPER_WIRE = BLOCK_ENTITY_TYPES.register(
            CopperWireBlock.NAME,
            () -> BlockEntityType.Builder.of(CopperWireBlockEntity::new, BlockRegistry.COPPER_WIRE.get())
                    .build(null)
    );

    public static final RegistryObject<BlockEntityType<CreativeEngineBlockEntity>> CREATIVE_ENGINE = BLOCK_ENTITY_TYPES.register(
            CreativeEngineBlock.NAME,
            () -> BlockEntityType.Builder.of(CreativeEngineBlockEntity::new, BlockRegistry.CREATIVE_ENGINE.get())
                    .build(null)
    );

    static void init(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}