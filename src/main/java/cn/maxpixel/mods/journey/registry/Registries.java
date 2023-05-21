package cn.maxpixel.mods.journey.registry;

import cn.maxpixel.mods.journey.network.NetworkManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Registries {
    public static void register(FMLJavaModLoadingContext ctx) {
        IEventBus modBus = ctx.getModEventBus();
        BlockRegistry.init(modBus);
        ItemRegistry.init(modBus);
        EntityRegistry.init(modBus);
        BlockEntityRegistry.init(modBus);
        modBus.register(TabRegistry.class);
        NetworkManager.registerMessages();
    }
}