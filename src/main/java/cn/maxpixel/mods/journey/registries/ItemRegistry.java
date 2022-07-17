package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.item.MarkerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final RegistryObject<Item> MARKER = Registries.ITEMS.register("marker", () -> new MarkerItem(new Item.Properties().tab(Tabs.MAIN)));

    static void init() { /* Trigger <clinit> */ }
}