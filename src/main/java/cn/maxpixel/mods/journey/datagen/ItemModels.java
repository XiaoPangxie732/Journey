package cn.maxpixel.mods.journey.datagen;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.item.MarkerItem;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {
    public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JourneyMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(MarkerItem.NAME, "item/stick");
    }
}