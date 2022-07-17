package cn.maxpixel.mods.journey.client.registries;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.client.renderers.be.ControllerBlockEntityRenderer;
import cn.maxpixel.mods.journey.client.renderers.be.CopperWireBlockEntityRenderer;
import cn.maxpixel.mods.journey.registries.BlockEntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JourneyMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockEntityRendererRegistry {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers register) {
        register.registerBlockEntityRenderer(BlockEntityRegistry.CONTROLLER.get(), ControllerBlockEntityRenderer::new);
        register.registerBlockEntityRenderer(BlockEntityRegistry.COPPER_WIRE.get(), CopperWireBlockEntityRenderer::new);
    }
}