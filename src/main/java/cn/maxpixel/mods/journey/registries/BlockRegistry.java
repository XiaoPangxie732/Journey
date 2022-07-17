package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.block.ControllerBlock;
import cn.maxpixel.mods.journey.block.CopperWireBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final RegistryObject<Block> CONTROLLER = registerWithItem(
            ControllerBlock.NAME,
            () -> new ControllerBlock(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.f)),
            () -> new Item.Properties().tab(Tabs.MAIN)
    );

    public static final RegistryObject<Block> COPPER_WIRE = registerWithItem(
            CopperWireBlock.NAME,
            () -> new CopperWireBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(2.f, 5.f)
                            .sound(SoundType.COPPER)
                            .isRedstoneConductor((state, getter, pos) -> false)
                            .noOcclusion()
            ), () -> new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)
    );

    /**
     * Register a block with its item
     * @param name Registry name
     * @param block Block creation function
     * @param props Item properties
     * @param <T> Block type
     * @return Registry object
     */
    private static <T extends Block> RegistryObject<T> registerWithItem(String name,
                                                                        Supplier<T> block,
                                                                        Supplier<Item.Properties> props) {
        RegistryObject<T> obj = Registries.BLOCKS.register(name, block);
        Registries.ITEMS.register(name, () -> new BlockItem(obj.get(), props.get()));
        return obj;
    }

    static void init() { /* Trigger <clinit> */ }
}