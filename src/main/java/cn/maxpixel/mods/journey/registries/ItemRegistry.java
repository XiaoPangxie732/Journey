package cn.maxpixel.mods.journey.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    /**
     * @see cn.maxpixel.mods.journey.client.function.MarkerItemFunction
     */
    public static final RegistryObject<Item> MARKER = Registries.ITEMS.register("marker", () -> new Item(new Item.Properties().tab(Tabs.MAIN)) {
        @Override
        public boolean isFoil(ItemStack pStack) {
            return true;
        }
    });

    static void init() { /* Trigger <clinit> */ }
}