package cn.maxpixel.mods.journey.client.screens;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import net.minecraft.client.Minecraft;

public class ScreenManager {
    public static void openControllerBlockConfigureScreen(ControllerBlockEntity blockEntity) {
        Minecraft.getInstance().setScreen(new ControllerBlockConfigureScreen(blockEntity));
    }
}