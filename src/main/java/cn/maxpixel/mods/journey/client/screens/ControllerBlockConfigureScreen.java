package cn.maxpixel.mods.journey.client.screens;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.item.MarkerItem;
import cn.maxpixel.mods.journey.network.serverbound.ServerboundControllerBlockChangePacket;
import cn.maxpixel.mods.journey.registries.BlockRegistry;
import cn.maxpixel.mods.journey.registries.ItemRegistry;
import cn.maxpixel.mods.journey.util.I18nUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import static cn.maxpixel.mods.journey.client.screens.Constants.BUTTON_HEIGHT;

public class ControllerBlockConfigureScreen extends Screen {
    private static final int BUTTON_WIDTH = 100;
    private static final Component TITLE = BlockRegistry.CONTROLLER.get().getName();
    private static final Component EXPAND_XP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "expand_xp");
    private static final Component SHRINK_XP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "shrink_xp");
    private static final Component EXPAND_XN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "expand_xn");
    private static final Component SHRINK_XN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "shrink_xn");
    private static final Component EXPAND_YP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "expand_yp");
    private static final Component SHRINK_YP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "shrink_yp");
    private static final Component EXPAND_YN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "expand_yn");
    private static final Component SHRINK_YN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "shrink_yn");
    private static final Component EXPAND_ZP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "expand_zp");
    private static final Component SHRINK_ZP = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "shrink_zp");
    private static final Component EXPAND_ZN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "expand_zn");
    private static final Component SHRINK_ZN = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "shrink_zn");
    private static final Component SUBMIT_AREA = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "submit_area");
    private static final Component ASSEMBLE = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.SCREEN_CATEGORY, "assemble");
    private static final Component START_ABSENT = I18nUtil.getTranslation(ItemRegistry.MARKER.get(), I18nUtil.MESSAGE_CATEGORY, "start.absent").withStyle(ChatFormatting.RED);
    private static final Component END_ABSENT = I18nUtil.getTranslation(ItemRegistry.MARKER.get(), I18nUtil.MESSAGE_CATEGORY, "end.absent").withStyle(ChatFormatting.RED);

    private final ControllerBlockEntity blockEntity;

    private Button expandXP;
    private Button shrinkXP;

    private Button expandXN;
    private Button shrinkXN;

    private Button expandYP;
    private Button shrinkYP;

    private Button expandYN;
    private Button shrinkYN;

    private Button expandZP;
    private Button shrinkZP;

    private Button expandZN;
    private Button shrinkZN;

    private Button submitArea;
    private Button assemble;
    private Button done;

    public ControllerBlockConfigureScreen(ControllerBlockEntity blockEntity) {
        super(TITLE);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        this.expandXP = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_XP, btn -> {}));
        this.shrinkXP = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40 + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_XP, btn -> {}));
        this.expandXN = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40 + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_XN, btn -> {}));
        this.shrinkXN = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2 - 1 - BUTTON_WIDTH, 40 + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_XN, btn -> {}));

        this.expandYP = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_YP, btn -> {}));
        this.shrinkYP = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40 + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_YP, btn -> {}));
        this.expandYN = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40 + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_YN, btn -> {}));
        this.shrinkYN = addRenderableWidget(new Button(width / 2 - BUTTON_WIDTH / 2, 40 + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_YN, btn -> {}));

        this.expandZP = addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_ZP, btn -> {}));
        this.shrinkZP = addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40 + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_ZP, btn -> {}));
        this.expandZN = addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40 + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, EXPAND_ZN, btn -> {}));
        this.shrinkZN = addRenderableWidget(new Button(width / 2 + BUTTON_WIDTH / 2 + 1, 40 + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT + 1 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, SHRINK_ZN, btn -> {}));

        this.submitArea = addRenderableWidget(new Button(width / 2 - 151, 40 + 4 + 4 * BUTTON_HEIGHT, 302, BUTTON_HEIGHT, SUBMIT_AREA, btn -> {
            if (MarkerItem.start == null) {
                Minecraft.getInstance().player.displayClientMessage(START_ABSENT, false);
            } else if (MarkerItem.end == null) {
                Minecraft.getInstance().player.displayClientMessage(END_ABSENT, false);
            } else {
                ServerboundControllerBlockChangePacket.send(blockEntity.getBlockPos(), MarkerItem.start, MarkerItem.end);
                MarkerItem.clearMark();
            }
        }));
        this.assemble = addRenderableWidget(new Button(width / 2 - 151, 40 + 5 + 5 * BUTTON_HEIGHT, 150, BUTTON_HEIGHT, ASSEMBLE, btn -> {}));
        this.done = addRenderableWidget(new Button(width / 2 + 1, 40 + 5 + 5 * BUTTON_HEIGHT, 150, BUTTON_HEIGHT, CommonComponents.GUI_DONE, btn -> onClose()));
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