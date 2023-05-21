package cn.maxpixel.mods.journey.util;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.registries.RegistryObject;

public class I18nUtil {
    public static final String MESSAGE_CATEGORY = "message";
    public static final String SCREEN_CATEGORY = "screen";

    public static String getTranslationId(RegistryObject<?> entry, String category, String key) {
        return Util.makeDescriptionId(category, entry.getId()) + '.' + key;
    }

    public static MutableComponent getTranslation(RegistryObject<?> entry, String category, String key) {
        return Component.translatable(getTranslationId(entry, category, key));
    }

    public static MutableComponent getTranslation(RegistryObject<?> entry, String category, String key, Object... args) {
        return Component.translatable(getTranslationId(entry, category, key), args);
    }
}