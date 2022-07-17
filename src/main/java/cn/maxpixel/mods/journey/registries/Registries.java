package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.JourneyMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registries {
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JourneyMod.MODID);
    static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, JourneyMod.MODID);
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JourneyMod.MODID);

    public static void register(FMLJavaModLoadingContext ctx) {
        Tabs.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
        IEventBus eventBus = ctx.getModEventBus();
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        ITEMS.register(eventBus);
    }
}