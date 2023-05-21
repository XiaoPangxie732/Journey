package cn.maxpixel.mods.journey.client.screens;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.item.MarkerItem;
import cn.maxpixel.mods.journey.network.serverbound.ServerboundControllerBlockChangePacket.AdjustAxis;
import cn.maxpixel.mods.journey.network.serverbound.ServerboundControllerBlockChangePacket.AdjustType;
import cn.maxpixel.mods.journey.network.serverbound.ServerboundStructureAssemblePacket;
import cn.maxpixel.mods.journey.registry.BlockRegistry;
import cn.maxpixel.mods.journey.registry.ItemRegistry;
import cn.maxpixel.mods.journey.util.I18nUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import static cn.maxpixel.mods.journey.network.serverbound.ServerboundControllerBlockChangePacket.send;

public class ControllerBlockConfigureScreen extends Screen {
    private static final int BUTTON_WIDTH = 100;
    private static final Component TITLE = BlockRegistry.CONTROLLER.get().getName();
    private static final Component EXPAND_XP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_xp");
    private static final Component SHRINK_XP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_xp");
    private static final Component EXPAND_XN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_xn");
    private static final Component SHRINK_XN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_xn");
    private static final Component EXPAND_YP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_yp");
    private static final Component SHRINK_YP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_yp");
    private static final Component EXPAND_YN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_yn");
    private static final Component SHRINK_YN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_yn");
    private static final Component EXPAND_ZP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_zp");
    private static final Component SHRINK_ZP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_zp");
    private static final Component EXPAND_ZN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "expand_zn");
    private static final Component SHRINK_ZN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "shrink_zn");
    private static final Component SUBMIT_AREA = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "submit_area");
    private static final Component ASSEMBLE = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "assemble");
    private static final Component DISASSEMBLE = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.SCREEN_CATEGORY, "disassemble");
    private static final Component START_ABSENT = I18nUtil.getTranslation(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "start.absent").withStyle(ChatFormatting.RED);
    private static final Component END_ABSENT = I18nUtil.getTranslation(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "end.absent").withStyle(ChatFormatting.RED);
    private static final Component[] XS = new Component[] {EXPAND_XP, SHRINK_XP, EXPAND_XN, SHRINK_XN};
    private static final Component[] YS = new Component[] {EXPAND_YP, SHRINK_YP, EXPAND_YN, SHRINK_YN};
    private static final Component[] ZS = new Component[] {EXPAND_ZP, SHRINK_ZP, EXPAND_ZN, SHRINK_ZN};

    private final ControllerBlockEntity blockEntity;

    public ControllerBlockConfigureScreen(ControllerBlockEntity blockEntity) {
        super(TITLE);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        if (blockEntity.isBuilding()) {
            for (int i = 0; i < 4; i++) {
                AdjustType adjustType = AdjustType.get(i);
                addRenderableWidget(Button.builder(XS[i], btn -> send(blockEntity.getBlockPos(), adjustType, AdjustAxis.X))
                        .pos(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40 + i + i * Button.DEFAULT_HEIGHT)
                        .width(BUTTON_WIDTH)
                        .build());
            }

            for (int i = 0; i < 4; i++) {
                AdjustType adjustType = AdjustType.get(i);
                addRenderableWidget(Button.builder(YS[i], btn -> send(blockEntity.getBlockPos(), adjustType, AdjustAxis.Y))
                        .pos(width / 2 - BUTTON_WIDTH / 2, 40 + i + i * Button.DEFAULT_HEIGHT)
                        .width(BUTTON_WIDTH)
                        .build());
            }

            for (int i = 0; i < 4; i++) {
                AdjustType adjustType = AdjustType.get(i);
                addRenderableWidget(Button.builder(ZS[i], btn -> send(blockEntity.getBlockPos(), adjustType, AdjustAxis.Z))
                        .pos(width / 2 + BUTTON_WIDTH / 2 + 1, 40 + i + i * Button.DEFAULT_HEIGHT)
                        .width(BUTTON_WIDTH)
                        .build());
            }

            addRenderableWidget(Button.builder(SUBMIT_AREA, btn -> {
                if (MarkerItem.start == null) {
                    Minecraft.getInstance().player.displayClientMessage(START_ABSENT, false);
                } else if (MarkerItem.end == null) {
                    Minecraft.getInstance().player.displayClientMessage(END_ABSENT, false);
                } else {
                    send(blockEntity.getBlockPos(), MarkerItem.start, MarkerItem.end);
                    MarkerItem.clearMark();
                }
            }).pos(width / 2 - 151, 40 + 4 + 4 * Button.DEFAULT_HEIGHT).width(302).build());
            addRenderableWidget(Button.builder(ASSEMBLE, btn -> {
                ServerboundStructureAssemblePacket.sendAssemble(blockEntity.getBlockPos());
                onClose();
            }).pos(width / 2 - 151, 40 + 5 + 5 * Button.DEFAULT_HEIGHT).build());
            addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> onClose()).pos(width / 2 + 1, 40 + 5 + 5 * Button.DEFAULT_HEIGHT).build());
        } else {
            addRenderableWidget(Button.builder(DISASSEMBLE, btn -> {
                ServerboundStructureAssemblePacket.sendDisassemble(blockEntity.getBlockPos());
                onClose();
            }).pos(width / 2 - 151, 40).build());
            addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> onClose()).pos(width / 2 - 1, 40).build());
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        drawCenteredString(poseStack, font, TITLE, width / 2, 10, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}