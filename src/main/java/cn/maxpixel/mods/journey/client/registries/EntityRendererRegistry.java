package cn.maxpixel.mods.journey.client.registries;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.client.renderers.entity.StructureEntityRenderer;
import cn.maxpixel.mods.journey.client.renderers.entity.block.ControllerBlockEntityRenderer;
import cn.maxpixel.mods.journey.client.renderers.entity.block.CopperWireBlockEntityRenderer;
import cn.maxpixel.mods.journey.registries.BlockEntityRegistry;
import cn.maxpixel.mods.journey.registries.EntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JourneyMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRendererRegistry {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers register) {
        register.registerEntityRenderer(EntityRegistry.STRUCTURE, StructureEntityRenderer::new);

        register.registerBlockEntityRenderer(BlockEntityRegistry.CONTROLLER, ControllerBlockEntityRenderer::new);
        register.registerBlockEntityRenderer(BlockEntityRegistry.COPPER_WIRE, CopperWireBlockEntityRenderer::new);
    }
}