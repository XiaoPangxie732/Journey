package cn.maxpixel.mods.journey.network.serverbound;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.block.entity.ControllerBlockEntity;
import cn.maxpixel.mods.journey.network.NetworkManager;
import cn.maxpixel.mods.journey.registry.BlockRegistry;
import cn.maxpixel.mods.journey.util.BlockGetterUtil;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record ServerboundControllerBlockChangePacket(Type type, BlockPos controllerPos,
                                                     @Nullable BlockPos start, @Nullable BlockPos end,
                                                     @Nullable AdjustType adjustType, @Nullable AdjustAxis adjustAxis) {
    private static final Component SUBMITTED_AREA = I18nUtil.getTranslation(BlockRegistry.CONTROLLER, I18nUtil.MESSAGE_CATEGORY, "submitted_area");

    public static void send(BlockPos controllerPos, BlockPos start, BlockPos end) {
        NetworkManager.CHANNEL.sendToServer(new ServerboundControllerBlockChangePacket(controllerPos, start, end));
    }

    public static void send(BlockPos controllerPos, AdjustType adjustType, AdjustAxis adjustAxis) {
        NetworkManager.CHANNEL.sendToServer(new ServerboundControllerBlockChangePacket(controllerPos, adjustType, adjustAxis));
    }

    public ServerboundControllerBlockChangePacket(BlockPos controllerPos, BlockPos start, BlockPos end) {
        this(Type.SUBMIT_AREA, controllerPos, start, end, null, null);
    }

    public ServerboundControllerBlockChangePacket(BlockPos controllerPos, AdjustType adjustType, AdjustAxis adjustAxis) {
        this(Type.ADJUST, controllerPos, null, null, adjustType, adjustAxis);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type).writeBlockPos(controllerPos);
        switch (type) {
            case SUBMIT_AREA -> buf.writeBlockPos(start).writeBlockPos(end);
            case ADJUST -> buf.writeEnum(adjustType).writeEnum(adjustAxis);
            default -> JourneyMod.whyYouGetHere();
        }
    }

    public static ServerboundControllerBlockChangePacket decode(FriendlyByteBuf buf) {
        return switch (buf.readEnum(Type.class)) {
            case SUBMIT_AREA -> new ServerboundControllerBlockChangePacket(buf.readBlockPos(), buf.readBlockPos(), buf.readBlockPos());
            case ADJUST -> new ServerboundControllerBlockChangePacket(buf.readBlockPos(), buf.readEnum(AdjustType.class), buf.readEnum(AdjustAxis.class));
        };
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            BlockGetterUtil.getExistingBlockEntity(player.getLevel(), controllerPos, ControllerBlockEntity.class).ifPresent(blockEntity -> {
                if (blockEntity.isBuilding()) {
                    switch (type) {
                        case SUBMIT_AREA -> {
                            BoundingBox box = BoundingBox.fromCorners(start, end).encapsulate(controllerPos);
                            blockEntity.set(box.minX(), box.minY(), box.minZ(), box.getXSpan(), box.getYSpan(), box.getZSpan());
                            blockEntity.setChanged();
                            Level level = blockEntity.getLevel();
                            BlockState state = level.getBlockState(controllerPos);
                            level.sendBlockUpdated(controllerPos, state, state, 3);
                            player.displayClientMessage(SUBMITTED_AREA, false);
                        }
                        case ADJUST -> {
                            blockEntity.setSize(adjustAxis.getSize(blockEntity.getSize(), blockEntity.getStart(), adjustType));
                            adjustAxis.setStart(blockEntity.getStart(), adjustType);
                            blockEntity.setChanged();
                            Level level = blockEntity.getLevel();
                            BlockState state = level.getBlockState(controllerPos);
                            level.sendBlockUpdated(controllerPos, state, state, 3);
                        }
                        default -> JourneyMod.whyYouGetHere();
                    }
                }
            });
        });
        ctx.setPacketHandled(true);
    }

    private enum Type {
        SUBMIT_AREA, ADJUST
    }

    public enum AdjustType {
        EXPAND_POSITIVE, EXPAND_NEGATIVE, SHRINK_POSITIVE, SHRINK_NEGATIVE
    }

    public enum AdjustAxis {
        X(1, 0, 0),
        Y(0, 1, 0),
        Z(0, 0, 1);

        private final int x;
        private final int y;
        private final int z;

        AdjustAxis(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3i getSize(Vec3i old, BlockPos.MutableBlockPos start, AdjustType type) {
            return switch (type) {
                case EXPAND_POSITIVE, EXPAND_NEGATIVE -> old.offset(x, y, z);
                case SHRINK_POSITIVE -> {
                    if (old.getX() <= x || old.getY() <= y || old.getZ() <= z ||
                            (x > 0 && start.getX() + old.getX() == 1) ||
                            (y > 0 && start.getY() + old.getY() == 1) ||
                            (z > 0 && start.getZ() + old.getZ() == 1)) {
                        yield old;
                    }
                    yield old.offset(-x, -y, -z);
                }
                case SHRINK_NEGATIVE -> {
                    if (old.getX() <= x || old.getY() <= y || old.getZ() <= z ||
                            (x > 0 && start.getX() == 0) ||
                            (y > 0 && start.getY() == 0) ||
                            (z > 0 && start.getZ() == 0)) {
                        yield old;
                    }
                    yield old.offset(-x, -y, -z);
                }
            };
        }

        public void setStart(BlockPos.MutableBlockPos start, AdjustType type) {
            if (type == AdjustType.EXPAND_NEGATIVE) {
                start.set(start.getX() - x, start.getY() - y, start.getZ() - z);
            } else if (type == AdjustType.SHRINK_NEGATIVE) {
                start.set(Math.min(start.getX() + x, 0), Math.min(start.getY() + y, 0), Math.min(start.getZ() + z, 0));
            }
        }
    }
}