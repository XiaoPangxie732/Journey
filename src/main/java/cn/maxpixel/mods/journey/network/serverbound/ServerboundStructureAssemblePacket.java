package cn.maxpixel.mods.journey.network.serverbound;

import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.network.NetworkManager;
import cn.maxpixel.mods.journey.util.BlockGetterUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundStructureAssemblePacket(BlockPos controllerPos, boolean assemble) {
    public static void sendAssemble(BlockPos controllerPos) {
        NetworkManager.CHANNEL.sendToServer(new ServerboundStructureAssemblePacket(controllerPos, true));
    }

    public static void sendDisassemble(BlockPos controllerPos) {
        NetworkManager.CHANNEL.sendToServer(new ServerboundStructureAssemblePacket(controllerPos, false));
    }

    public ServerboundStructureAssemblePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(controllerPos).writeBoolean(assemble);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> BlockGetterUtil.getExistingBlockEntity(ctx.getSender().getLevel(), controllerPos, ControllerBlockEntity.class)
                .ifPresent(ControllerBlockEntity::assemble));
        ctx.setPacketHandled(true);
    }
}