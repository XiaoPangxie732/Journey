package cn.maxpixel.mods.journey.registry;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TabRegistry {
    public static final ResourceLocation MAIN = Utils.newResourceLocation("main");
    public static final String MAIN_I18N = i18n("main");

    public static final ResourceLocation MEMES = Utils.newResourceLocation("memes");
    public static final String MEMES_I18N = i18n("memes");

    @SubscribeEvent
    public static void makeCreativeModeTabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(MAIN, builder ->
                builder.title(Component.translatable(MAIN_I18N))
                        .icon(() -> new ItemStack(BlockRegistry.CONTROLLER.get()))
                        .displayItems((params, output) -> {
                            output.accept(ItemRegistry.MARKER.get());
                            output.accept(BlockRegistry.CREATIVE_ENGINE.get());
                            output.accept(BlockRegistry.CONTROLLER.get());
                        })
        );
//        event.registerCreativeModeTab(MEMES, builder ->
//                builder.title(Component.translatable(MEMES_I18N))
//                        .icon(() -> new ItemStack(BlockRegistry.CONTROLLER.get()))
//                        .displayItems((params, output) -> {
//                            output.accept(ItemRegistry.MARKER.get());
//                            output.accept(BlockRegistry.CREATIVE_ENGINE.get());
//                            output.accept(BlockRegistry.CONTROLLER.get());
//                        })
//        );
    }

    @SubscribeEvent
    public static void appendToCreativeModeTabs(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(BlockRegistry.COPPER_WIRE);
        }
    }

    private static String i18n(String name) {
        return String.join(".", "itemGroup", JourneyMod.MODID, name);
    }
}