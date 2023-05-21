package cn.maxpixel.mods.journey.registry;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.item.MarkerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JourneyMod.MODID);

    public static final RegistryObject<MarkerItem> MARKER = ITEMS.register(MarkerItem.NAME, () -> new MarkerItem(new Item.Properties()));

    static void init(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}