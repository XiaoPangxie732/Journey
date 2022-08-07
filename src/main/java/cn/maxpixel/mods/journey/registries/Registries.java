package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.network.NetworkManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

public class Registries {
    private static IForgeRegistry<Block> BLOCKS;
    private static IForgeRegistry<Item> ITEMS;
    private static IForgeRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
    private static IForgeRegistry<EntityType<?>> ENTITY_TYPES;

    public static void register(FMLJavaModLoadingContext ctx) {
        ctx.getModEventBus().register(Registries.class);
        Tabs.init();
        NetworkManager.registerMessages();
    }

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> register) {
        BLOCKS = register.getRegistry();
        BlockRegistry.init();
        BLOCKS = null;
    }

    static <T extends Block> T registerBlock(String name, T block) {
        BLOCKS.register(block.setRegistryName(JourneyMod.MODID, name));
        return block;
    }

    @SubscribeEvent
    public static void onRegisterBlockEntities(RegistryEvent.Register<BlockEntityType<?>> register) {
        BLOCK_ENTITY_TYPES = register.getRegistry();
        BlockEntityRegistry.init();
        BLOCK_ENTITY_TYPES = null;
    }

    static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntityType) {
        BLOCK_ENTITY_TYPES.register(blockEntityType.setRegistryName(JourneyMod.MODID, name));
        return blockEntityType;
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> register) {
        ITEMS = register.getRegistry();
        BlockRegistry.registerBlockItems();
        ItemRegistry.init();
        ITEMS = null;
    }

    static <T extends Item> T registerItem(String name, T item) {
        ITEMS.register(item.setRegistryName(JourneyMod.MODID, name));
        return item;
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> register) {
        ENTITY_TYPES = register.getRegistry();
        EntityRegistry.init();
        ENTITY_TYPES = null;
    }

    static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> entityTypeBuilder) {
        EntityType<T> type = entityTypeBuilder.build(name);
        ENTITY_TYPES.register(type.setRegistryName(JourneyMod.MODID, name));
        return type;
    }
}