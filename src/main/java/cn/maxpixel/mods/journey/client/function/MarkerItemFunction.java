package cn.maxpixel.mods.journey.client.function;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.registries.ItemRegistry;
import cn.maxpixel.mods.journey.util.I18nUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @see cn.maxpixel.mods.journey.network.clientbound.ClientboundClearMarkPacket
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JourneyMod.MODID)
public class MarkerItemFunction {
    public static BlockPos start;
    public static BlockPos end;

    @SubscribeEvent
    public static void onPlayerAttack(PlayerInteractEvent.LeftClickBlock event) {
        if (onMark(event.getPlayer(), event.getHand(), event.getPos(), MarkAction.START)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerUse(PlayerInteractEvent.RightClickBlock event) {
        if (onMark(event.getPlayer(), event.getHand(), event.getPos(), MarkAction.END)) {
            event.setCanceled(true);
        }
    }

    public static void clearMark() {
        MarkerItemFunction.start = null;
        MarkerItemFunction.end = null;
    }

    private static boolean onMark(Player p, InteractionHand hand, BlockPos pos, MarkAction action) {
        if (p instanceof LocalPlayer player) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(ItemRegistry.MARKER.get())) { // TODO: Configurable marker item
                switch (action) {
                    case START -> {
                        start = pos;
                        player.displayClientMessage(I18nUtil.getTranslation(ItemRegistry.MARKER.get(), I18nUtil.MESSAGE_CATEGORY, "start.set", pos.toShortString()), false);
                        return true;
                    }
                    case END -> {
                        end = pos;
                        player.displayClientMessage(I18nUtil.getTranslation(ItemRegistry.MARKER.get(), I18nUtil.MESSAGE_CATEGORY, "end.set", pos.toShortString()), false);
                        return true;
                    }
                    default -> JourneyMod.whyYouGetHere();
                }
            }
        }
        return false;
    }

    private enum MarkAction {
        START, END
    }
}