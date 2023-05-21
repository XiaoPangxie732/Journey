package cn.maxpixel.mods.journey.registry;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.entity.StructureEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JourneyMod.MODID);

    public static final RegistryObject<EntityType<StructureEntity>> STRUCTURE = ENTITY_TYPES.register(
            StructureEntity.NAME,
            () -> EntityType.Builder.of(StructureEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .noSummon().build(null)
    );

    static void init(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }
}