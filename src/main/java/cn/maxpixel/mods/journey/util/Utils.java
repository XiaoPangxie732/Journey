package cn.maxpixel.mods.journey.util;

import cn.maxpixel.mods.journey.JourneyMod;
import net.minecraft.resources.ResourceLocation;

public class Utils {
    public static ResourceLocation newResourceLocation(String path) {
        return new ResourceLocation(JourneyMod.MODID, path);
    }
}