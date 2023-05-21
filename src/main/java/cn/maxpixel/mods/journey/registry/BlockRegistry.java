package cn.maxpixel.mods.journey.registry;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.block.ControllerBlock;
import cn.maxpixel.mods.journey.block.CopperWireBlock;
import cn.maxpixel.mods.journey.block.CreativeEngineBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JourneyMod.MODID);

    public static final RegistryObject<ControllerBlock> CONTROLLER = registerWithItem(
            ControllerBlock.NAME,
            () -> new ControllerBlock(
                    BlockBehaviour.Properties.of(Material.METAL)
                            .requiresCorrectToolForDrops()
                            .strength(5.f, 3600000)
            ), Item.Properties::new
    );

    public static final RegistryObject<CopperWireBlock> COPPER_WIRE = registerWithItem(
            CopperWireBlock.NAME,
            () -> new CopperWireBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(2.f, 5.f)
                            .sound(SoundType.COPPER)
                            .isRedstoneConductor((state, getter, pos) -> false)
                            .noOcclusion()
            ), Item.Properties::new
    );

    public static final RegistryObject<CreativeEngineBlock> CREATIVE_ENGINE = registerWithItem(
            CreativeEngineBlock.NAME,
            () -> new CreativeEngineBlock(
                    BlockBehaviour.Properties.of(Material.METAL)
                            .strength(2.f)
            ), Item.Properties::new
    );

    /**
     * Register a block with its item
     * @param name Registry name
     * @param block Block creation function
     * @param props Item properties
     * @param <T> Block type
     * @return Registry object
     */
    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> block, Supplier<Item.Properties> props) {
        RegistryObject<T> blockObject = BLOCKS.register(name, block);
        ItemRegistry.ITEMS.register(name, () -> new BlockItem(blockObject.get(), props.get()));
        return blockObject;
    }

    static void init(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}