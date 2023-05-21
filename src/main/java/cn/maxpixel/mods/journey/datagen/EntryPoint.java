package cn.maxpixel.mods.journey.datagen;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.datagen.lang.AmericanEnglishLanguageProvider;
import cn.maxpixel.mods.journey.datagen.lang.SimplifiedChineseLanguageProvider;
import cn.maxpixel.mods.journey.datagen.tags.BlockTagsProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = JourneyMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntryPoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        LOGGER.info("Gathering data");
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        // lang
        generator.addProvider(event.includeClient(), (DataProvider.Factory<AmericanEnglishLanguageProvider>) AmericanEnglishLanguageProvider::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<SimplifiedChineseLanguageProvider>) SimplifiedChineseLanguageProvider::new);

        // block states & item models
        generator.addProvider(event.includeClient(), new BlockStates(output, fileHelper));
        generator.addProvider(event.includeClient(), new ItemModels(output, fileHelper));

        // tags
        generator.addProvider(event.includeServer(), new BlockTagsProvider(output, event.getLookupProvider(), fileHelper));
    }
}