package cn.maxpixel.mods.journey.network.clientbound;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.client.function.MarkerItemFunction;
import cn.maxpixel.mods.journey.network.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = JourneyMod.MODID)
public class ClientboundClearMarkPacket {
    public void encode(FriendlyByteBuf buf) {
    }

    public static ClientboundClearMarkPacket decode(FriendlyByteBuf buf) {
        return new ClientboundClearMarkPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        MarkerItemFunction.clearMark();
        JourneyMod.LOGGER.debug("Cleared mark of {}", Minecraft.getInstance().player.getGameProfile().getName());
        contextSupplier.get().setPacketHandled(true);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) { // Only called on server side
        JourneyMod.LOGGER.debug("Clearing mark of {}", event.getPlayer().getGameProfile().getName());
        NetworkManager.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new ClientboundClearMarkPacket());
    }
}