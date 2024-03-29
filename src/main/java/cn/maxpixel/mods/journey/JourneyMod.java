package cn.maxpixel.mods.journey;

import cn.maxpixel.mods.journey.registry.Registries;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(JourneyMod.MODID)
public class JourneyMod {
    public static final String MODID = "journey";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JourneyMod() {
        LOGGER.info("Constructing Journey Mod");
        LOGGER.debug("Registering stuffs");
        Registries.register(FMLJavaModLoadingContext.get());
    }

    public static <T> T whyYouGetHere() {
        LOGGER.error("Why you get here???", new Throwable());
        return null;
    }
}