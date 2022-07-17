package cn.maxpixel.mods.journey.network.serverbound;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.network.NetworkManager;
import cn.maxpixel.mods.journey.registries.BlockRegistry;
import cn.maxpixel.mods.journey.util.BlockGetterUtil;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record ServerboundControllerBlockChangePacket(Type type, BlockPos controllerPos, @Nullable BlockPos start, @Nullable BlockPos end) {
    private static final Component SUBMITTED_AREA = I18nUtil.getTranslation(BlockRegistry.CONTROLLER.get(), I18nUtil.MESSAGE_CATEGORY, "submitted_area");

    public static void send(BlockPos controllerPos, BlockPos start, BlockPos end) {
        NetworkManager.CHANNEL.sendToServer(create(controllerPos, start, end));
    }

    public static ServerboundControllerBlockChangePacket create(BlockPos controllerPos, BlockPos start, BlockPos end) {
        return new ServerboundControllerBlockChangePacket(Type.SUBMIT_AREA, controllerPos, start, end);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type).writeBlockPos(controllerPos);
        switch (type) {
            case SUBMIT_AREA -> buf.writeBlockPos(start).writeBlockPos(end);
            default -> JourneyMod.whyYouGetHere();
        }
    }

    public static ServerboundControllerBlockChangePacket decode(FriendlyByteBuf buf) {
        return switch (buf.readEnum(Type.class)) {
            case SUBMIT_AREA -> create(buf.readBlockPos(), buf.readBlockPos(), buf.readBlockPos());
        };
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            BlockGetterUtil.getExistingBlockEntity(player.getLevel(), controllerPos, ControllerBlockEntity.class).ifPresent(blockEntity -> {
                switch (type) {
                    case SUBMIT_AREA -> {
                        player.displayClientMessage(SUBMITTED_AREA, false);
                    }
                    default -> JourneyMod.whyYouGetHere();
                }
            });
        });
        ctx.setPacketHandled(true);
    }

    private enum Type {
        SUBMIT_AREA
    }
}