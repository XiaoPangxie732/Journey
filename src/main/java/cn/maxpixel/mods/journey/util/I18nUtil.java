package cn.maxpixel.mods.journey.util;

import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class I18nUtil {
    public static final String MESSAGE_CATEGORY = "message";
    public static final String SCREEN_CATEGORY = "screen";

    public static String getTranslationId(IForgeRegistryEntry<?> entry, String category, String key) {
        return Util.makeDescriptionId(category, entry.getRegistryName()) + '.' + key;
    }

    public static TranslatableComponent getTranslation(IForgeRegistryEntry<?> entry, String category, String key) {
        return new TranslatableComponent(getTranslationId(entry, category, key));
    }

    public static TranslatableComponent getTranslation(IForgeRegistryEntry<?> entry, String category, String key, Object... args) {
        return new TranslatableComponent(getTranslationId(entry, category, key), args);
    }
}