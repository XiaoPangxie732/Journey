package cn.maxpixel.mods.journey.datagen;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.datagen.lang.EnglishLanguageProvider;
import cn.maxpixel.mods.journey.datagen.lang.SimplifiedChineseLanguageProvider;
import cn.maxpixel.mods.journey.datagen.tags.BlockTagsProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = JourneyMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntryPoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        LOGGER.info("Gathering data");
        DataGenerator generator = event.getGenerator();

        // lang
        generator.addProvider(new EnglishLanguageProvider(generator, JourneyMod.MODID, "en_us"));
        generator.addProvider(new SimplifiedChineseLanguageProvider(generator, JourneyMod.MODID, "zh_cn"));

        // blocks & items
        generator.addProvider(new BlockStates(generator, JourneyMod.MODID, event.getExistingFileHelper()));
        generator.addProvider(new ItemModels(generator, JourneyMod.MODID, event.getExistingFileHelper()));

        // tags
        generator.addProvider(new BlockTagsProvider(generator, JourneyMod.MODID, event.getExistingFileHelper()));
    }
}