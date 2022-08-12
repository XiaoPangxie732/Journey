package cn.maxpixel.mods.journey.registries;

import cn.maxpixel.mods.journey.entity.StructureEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityRegistry {
    public static final EntityType<StructureEntity> STRUCTURE = Registries.registerEntity(
            StructureEntity.NAME,
            EntityType.Builder.of(StructureEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .noSummon()
    );

    static void init() { /* Trigger <clinit> */ }
}