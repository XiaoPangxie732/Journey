package cn.maxpixel.mods.journey.datagen.lang;

import cn.maxpixel.mods.journey.registry.BlockRegistry;
import cn.maxpixel.mods.journey.registry.ItemRegistry;
import cn.maxpixel.mods.journey.registry.TabRegistry;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.data.PackOutput;

public class SimplifiedChineseLanguageProvider extends LanguageProvider {
    public SimplifiedChineseLanguageProvider(PackOutput output) {
        super(output, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        // ItemGroup
        add(TabRegistry.MAIN_I18N, "远行");
        add(TabRegistry.MEMES_I18N, "远行 | 梗");

        // Block
        addControllerBlockTranslations();
        addBlock(BlockRegistry.COPPER_WIRE, "铜线");
        addBlock(BlockRegistry.CREATIVE_ENGINE, "创造引擎");

        // Item
        addMarkerTranslations();
    }

    private void addControllerBlockTranslations() {
        addBlock(BlockRegistry.CONTROLLER, "结构控制器");

        // Screen
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_xp", "向X+扩大");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_xp", "从X+缩小");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_xn", "向X-扩大");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_xn", "从X-缩小");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_yp", "向Y+扩大");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_yp", "从Y+缩小");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_yn", "向Y-扩大");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_yn", "从Y-缩小");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_zp", "向Z+扩大");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_zp", "从Z+缩小");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_zn", "向Z-扩大");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_zn", "从Z-缩小");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "submit_area", "提交选区");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "assemble", "组装");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "disassemble", "解装");
        addCustom(BlockRegistry.CONTROLLER, I18nUtil.MESSAGE_CATEGORY, "submitted_area", "已提交选区");
    }

    private void addMarkerTranslations() {
        addItem(ItemRegistry.MARKER, "选区工具");

        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "start.set", "已设置起始位置: %s");
        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "end.set", "已设置结束位置: %s");
        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "start.absent", "未设置起始位置");
        addCustom(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "end.absent", "未设置结束位置");
    }
}