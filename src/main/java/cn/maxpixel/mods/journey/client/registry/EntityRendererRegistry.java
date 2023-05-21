package cn.maxpixel.mods.journey.client.registry;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.client.renderer.entity.StructureEntityRenderer;
import cn.maxpixel.mods.journey.client.renderer.entity.block.ControllerBlockEntityRenderer;
import cn.maxpixel.mods.journey.client.renderer.entity.block.CopperWireBlockEntityRenderer;
import cn.maxpixel.mods.journey.client.renderer.entity.block.CreativeEngineBlockEntityRenderer;
import cn.maxpixel.mods.journey.registry.BlockEntityRegistry;
import cn.maxpixel.mods.journey.registry.EntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JourneyMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRendererRegistry {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers register) {
        register.registerEntityRenderer(EntityRegistry.STRUCTURE.get(), StructureEntityRenderer::new);

        register.registerBlockEntityRenderer(BlockEntityRegistry.CONTROLLER.get(), ControllerBlockEntityRenderer::new);
        register.registerBlockEntityRenderer(BlockEntityRegistry.COPPER_WIRE.get(), CopperWireBlockEntityRenderer::new);
        register.registerBlockEntityRenderer(BlockEntityRegistry.CREATIVE_ENGINE.get(), CreativeEngineBlockEntityRenderer::new);
    }
}