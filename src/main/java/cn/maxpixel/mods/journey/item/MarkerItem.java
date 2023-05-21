package cn.maxpixel.mods.journey.item;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.annotation.CalledOn;
import cn.maxpixel.mods.journey.registry.ItemRegistry;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @see cn.maxpixel.mods.journey.network.clientbound.ClientboundClearMarkPacket
 */
public class MarkerItem extends Item {
    public static final String NAME = "marker";
    public static final String SET_START = I18nUtil.getTranslationId(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "start.set");
    public static final String SET_END = I18nUtil.getTranslationId(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "end.set");
    private static final byte MARK_START = 0;
    private static final byte MARK_END = 1;

    public static BlockPos start;
    public static BlockPos end;

    public MarkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) {
            onMark(player, pos, MARK_START);
        }
        return false;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide) {
            onMark(context.getPlayer(), context.getClickedPos(), MARK_END);
        }
        return InteractionResult.SUCCESS;
    }

    @CalledOn(CalledOn.Side.CLIENT)
    public static void clearMark() {
        MarkerItem.start = null;
        MarkerItem.end = null;
    }

    @CalledOn(CalledOn.Side.CLIENT)
    private static boolean onMark(@Nullable Player p, BlockPos pos, byte action) { // TODO: Configurable marker item
        if (p instanceof LocalPlayer player) {
            switch (action) {
                case MARK_START -> {
                    start = pos;
                    player.displayClientMessage(Component.translatable(SET_START, pos.toShortString()), false);
                    return true;
                }
                case MARK_END -> {
                    end = pos;
                    player.displayClientMessage(Component.translatable(SET_END, pos.toShortString()), false);
                    return true;
                }
                default -> JourneyMod.whyYouGetHere();
            }
        }
        return false;
    }
}