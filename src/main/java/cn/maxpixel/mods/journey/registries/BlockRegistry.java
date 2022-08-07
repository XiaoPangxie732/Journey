package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.block.ControllerBlock;
import cn.maxpixel.mods.journey.block.CopperWireBlock;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class BlockRegistry {
    private static final Object2ObjectArrayMap<String, BlockItem> BLOCK_ITEMS = new Object2ObjectArrayMap<>();

    public static final ControllerBlock CONTROLLER = registerWithItem(
            ControllerBlock.NAME,
            new ControllerBlock(
                    BlockBehaviour.Properties.of(Material.METAL)
                            .requiresCorrectToolForDrops()
                            .strength(5.f, 3600000)
            ),
            new Item.Properties().tab(Tabs.MAIN)
    );

    public static final CopperWireBlock COPPER_WIRE = registerWithItem(
            CopperWireBlock.NAME,
            new CopperWireBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(2.f, 5.f)
                            .sound(SoundType.COPPER)
                            .isRedstoneConductor((state, getter, pos) -> false)
                            .noOcclusion()
            ), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)
    );

    /**
     * Register a block with its item
     * @param name Registry name
     * @param block Block creation function
     * @param props Item properties
     * @param <T> Block type
     * @return Registry object
     */
    private static <T extends Block> T registerWithItem(String name, T block, Item.Properties props) {
        BLOCK_ITEMS.put(name, new BlockItem(block, props)); // should I use supplier here?
        return Registries.registerBlock(name, block);
    }

    static void registerBlockItems() {
        BLOCK_ITEMS.forEach(Registries::registerItem);
    }

    static void init() { /* Trigger <clinit> */ }
}