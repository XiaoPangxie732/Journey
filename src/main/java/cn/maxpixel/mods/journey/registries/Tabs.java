package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.JourneyMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class Tabs {
    public static final String MAIN_LABEL = JourneyMod.MODID + ".main";
    public static final CreativeModeTab MAIN = new CreativeModeTab(MAIN_LABEL) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BlockRegistry.CONTROLLER);
        }
    };

    public static final String MEMES_LABEL = JourneyMod.MODID + ".memes";
//    public static final CreativeModeTab MEMES = new CreativeModeTab(MEMES_LABEL) {
//        @Override
//        public ItemStack makeIcon() {
//            return new ItemStack(BlockRegistry.CONTROLLER.get()); // TODO: Change icon
//        }
//    };

    static void init() { /* Trigger <clinit> */ }
}