package cn.maxpixel.mods.journey.datagen.lang;

import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
    public LanguageProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    protected void addItemGroup(String key, String name) {
        add("itemGroup." + key, name);
    }

    protected void addCustom(IForgeRegistryEntry<?> entry, String category, String key, String value) {
        add(I18nUtil.getTranslationId(entry, category, key), value);
    }
}