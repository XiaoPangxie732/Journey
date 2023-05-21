package cn.maxpixel.mods.journey.datagen.lang;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.data.PackOutput;
import net.minecraftforge.registries.RegistryObject;

public abstract class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
    public LanguageProvider(PackOutput output, String locale) {
        super(output, JourneyMod.MODID, locale);
    }

    protected void addCustom(RegistryObject<?> entry, String category, String key, String value) {
        add(I18nUtil.getTranslationId(entry, category, key), value);
    }
}