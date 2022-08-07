package cn.maxpixel.mods.journey.network.clientbound;

import cn.maxpixel.mods.journey.entity.StructureEntity;
import cn.maxpixel.mods.journey.network.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ClientboundChunkUpdatePacket {
    private final int entityId;
    private final int chunkX;
    private final int chunkZ;
    private final ClientboundLevelChunkPacketData chunkData;
//    private final ClientboundLightUpdatePacketData lightData;TODO

    public ClientboundChunkUpdatePacket(int entityId, int chunkX, int chunkZ, LevelChunk chunk) {
        this.entityId = entityId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkData = new ClientboundLevelChunkPacketData(chunk);
    }

    public ClientboundChunkUpdatePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.chunkData = new ClientboundLevelChunkPacketData(buf, chunkX, chunkZ);
    }

    public static void send(StructureEntity entity, int chunkX, int chunkZ, LevelChunk chunk) {
        NetworkManager.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                new ClientboundChunkUpdatePacket(entity.getId(), chunkX, chunkZ, chunk));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId).writeInt(chunkX).writeInt(chunkZ);
        chunkData.write(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getEntity(entityId) instanceof StructureEntity entity) {
                entity.updateClientChunks(chunkData, chunkX, chunkZ);
            }
        });
        ctx.setPacketHandled(true);
    }
}