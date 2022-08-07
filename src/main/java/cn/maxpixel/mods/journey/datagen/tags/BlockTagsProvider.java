package cn.maxpixel.mods.journey.datagen.tags;

import cn.maxpixel.mods.journey.tags.BlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider {
    public BlockTagsProvider(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.WEAK).addTags(net.minecraft.tags.BlockTags.BUTTONS);
    }
}