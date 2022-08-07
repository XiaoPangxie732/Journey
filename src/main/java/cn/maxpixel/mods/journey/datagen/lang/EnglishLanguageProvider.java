package cn.maxpixel.mods.journey.datagen.lang;

import cn.maxpixel.mods.journey.registries.BlockRegistry;
import cn.maxpixel.mods.journey.registries.ItemRegistry;
import cn.maxpixel.mods.journey.registries.Tabs;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.data.DataGenerator;

public class EnglishLanguageProvider extends LanguageProvider {
    public EnglishLanguageProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        // ItemGroup
        addItemGroup(Tabs.MAIN_LABEL, "Journey");
        addItemGroup(Tabs.MEMES_LABEL, "Journey | Memes");

        // Block
        addControllerBlockTranslations();
        add(BlockRegistry.COPPER_WIRE, "Copper Wire");

        // Item
        addMarkerTranslations();
    }

    private void addControllerBlockTranslations() {
        add(BlockRegistry.CONTROLLER, "Structure Controller");

        // Screen
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_xp", "Expand to X+");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_xp", "Shrink from X+");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_xn", "Expand to X-");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_xn", "Shrink from X-");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_yp", "Expand to Y+");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_yp", "Shrink from Y+");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_yn", "Expand to Y-");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_yn", "Shrink from Y-");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_zp", "Expand to Z+");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_zp", "Shrink from Z+");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_zn", "Expand to Z-");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_zn", "Shrink from Z-");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "submit_area", "Submit marked area");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "assemble", "Assemble");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "disassemble", "Disassemble");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.MESSAGE_CATEGORY, "submitted_area", "Submitted marked area");
    }

    private void addMarkerTranslations() {
        add(ItemRegistry.MARKER, "Marker");

        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "start.set", "Set start position: %s");
        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "end.set", "Set end position: %s");
        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "start.absent", "Start position is absent");
        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "end.absent", "End position is absent");
    }
}