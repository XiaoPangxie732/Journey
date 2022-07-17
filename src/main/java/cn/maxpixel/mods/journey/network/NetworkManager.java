package cn.maxpixel.mods.journey.network;

import cn.maxpixel.mods.journey.network.clientbound.ClientboundClearMarkPacket;
import cn.maxpixel.mods.journey.network.serverbound.ServerboundControllerBlockChangePacket;
import cn.maxpixel.mods.journey.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkManager {
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);
    public static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Utils.newResourceLocation("main"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals
    );

    public static void registerMessages() {
        // Clientbound
        registerMessage(ClientboundClearMarkPacket.class, ClientboundClearMarkPacket::encode, ClientboundClearMarkPacket::decode, ClientboundClearMarkPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // Serverbound
        registerMessage(ServerboundControllerBlockChangePacket.class, ServerboundControllerBlockChangePacket::encode, ServerboundControllerBlockChangePacket::decode, ServerboundControllerBlockChangePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private static <T> void registerMessage(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encode, Function<FriendlyByteBuf, T> decode,
                                            BiConsumer<T, Supplier<NetworkEvent.Context>> handle, Optional<NetworkDirection> direction) {
        CHANNEL.registerMessage(ID_COUNTER.getAndIncrement(), clazz, encode, decode, handle, direction);
    }
}