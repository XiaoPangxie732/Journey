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

import static cn.maxpixel.mods.journey.client.screens.Constants.BUTTON_HEIGHT;
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

    private final ControllerBlockEntity blockEntity;

    public ControllerBlockConfigureScreen(ControllerBlockEntity blockEntity) {
        super(TITLE);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        if (blockEntity.isBuilding()) {
            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_XP, btn -> send(blockEntity.getBlockPos(), AdjustType.EXPAND_POSITIVE, AdjustAxis.X)));
            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40 + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_XP, btn -> send(blockEntity.getBlockPos(), AdjustType.SHRINK_POSITIVE, AdjustAxis.X)));
            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40 + 2 + 2 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_XN, btn -> send(blockEntity.getBlockPos(), AdjustType.EXPAND_NEGATIVE, AdjustAxis.X)));
            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40 + 3 + 3 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_XN, btn -> send(blockEntity.getBlockPos(), AdjustType.SHRINK_NEGATIVE, AdjustAxis.X)));

            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_YP, btn -> send(blockEntity.getBlockPos(), AdjustType.EXPAND_POSITIVE, AdjustAxis.Y)));
            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40 + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_YP, btn -> send(blockEntity.getBlockPos(), AdjustType.SHRINK_POSITIVE, AdjustAxis.Y)));
            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40 + 2 + 2 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_YN, btn -> send(blockEntity.getBlockPos(), AdjustType.EXPAND_NEGATIVE, AdjustAxis.Y)));
            addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40 + 3 + 3 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_YN, btn -> send(blockEntity.getBlockPos(), AdjustType.SHRINK_NEGATIVE, AdjustAxis.Y)));

            addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_ZP, btn -> send(blockEntity.getBlockPos(), AdjustType.EXPAND_POSITIVE, AdjustAxis.Z)));
            addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40 + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_ZP, btn -> send(blockEntity.getBlockPos(), AdjustType.SHRINK_POSITIVE, AdjustAxis.Z)));
            addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40 + 2 + 2 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_ZN, btn -> send(blockEntity.getBlockPos(), AdjustType.EXPAND_NEGATIVE, AdjustAxis.Z)));
            addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40 + 3 + 3 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_ZN, btn -> send(blockEntity.getBlockPos(), AdjustType.SHRINK_NEGATIVE, AdjustAxis.Z)));

            addRenderableWidget(new Button(width / 2 - 151, 40 + 4 + 4 * BUTTON_HEIGHT, 302, BUTTON_HEIGHT, SUBMIT_AREA, btn -> {
                if (MarkerItem.start == null) {
                    Minecraft.getInstance().player.displayClientMessage(START_ABSENT, false);
                } else if (MarkerItem.end == null) {
                    Minecraft.getInstance().player.displayClientMessage(END_ABSENT, false);
                } else {
                    send(blockEntity.getBlockPos(), MarkerItem.start, MarkerItem.end);
                    MarkerItem.clearMark();
                }
            }));
            addRenderableWidget(new Button(width / 2 - 151, 40 + 5 + 5 * BUTTON_HEIGHT, 150, BUTTON_HEIGHT, ASSEMBLE, btn -> {
                ServerboundStructureAssemblePacket.sendAssemble(blockEntity.getBlockPos());
                onClose();
            }));
            addRenderableWidget(new Button(width / 2 + 1, 40 + 5 + 5 * BUTTON_HEIGHT, 150, BUTTON_HEIGHT, CommonComponents.GUI_DONE, btn -> onClose()));
        } else {
            addRenderableWidget(new Button(width / 2 - 151, 40, 150, BUTTON_HEIGHT, DISASSEMBLE, btn -> {
                ServerboundStructureAssemblePacket.sendDisassemble(blockEntity.getBlockPos());
                onClose();
            }));
            addRenderableWidget(new Button(width / 2 - 1, 40, 150, BUTTON_HEIGHT, CommonComponents.GUI_CANCEL, btn -> onClose()));
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