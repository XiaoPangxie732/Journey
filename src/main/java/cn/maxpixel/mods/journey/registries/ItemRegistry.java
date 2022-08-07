package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.item.MarkerItem;
import net.minecraft.world.item.Item;

public class ItemRegistry {
    public static final MarkerItem MARKER = Registries.registerItem("marker", new MarkerItem(new Item.Properties().tab(Tabs.MAIN)));

    static void init() { /* Trigger <clinit> */ }
}