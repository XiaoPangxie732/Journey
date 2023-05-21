package cn.maxpixel.mods.journey.item;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.registry.ItemRegistry;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @see cn.maxpixel.mods.journey.network.clientbound.ClientboundClearMarkPacket
 */
public class MarkerItem extends Item {
    public static final String NAME = "marker";

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
            onMark(player, pos, MarkAction.START);
        }
        return false;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide) {
            onMark(context.getPlayer(), context.getClickedPos(), MarkAction.END);
        }
        return InteractionResult.SUCCESS;
    }

    public static void clearMark() {
        MarkerItem.start = null;
        MarkerItem.end = null;
    }

    private static boolean onMark(Player p, BlockPos pos, MarkAction action) { // TODO: Configurable marker item
        if (p instanceof LocalPlayer player) {
            switch (action) {
                case START -> {
                    start = pos;
                    player.displayClientMessage(I18nUtil.getTranslation(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "start.set", pos.toShortString()), false);
                    return true;
                }
                case END -> {
                    end = pos;
                    player.displayClientMessage(I18nUtil.getTranslation(ItemRegistry.MARKER, I18nUtil.MESSAGE_CATEGORY, "end.set", pos.toShortString()), false);
                    return true;
                }
                default -> JourneyMod.whyYouGetHere();
            }
        }
        return false;
    }

    private enum MarkAction {
        START, END
    }
}