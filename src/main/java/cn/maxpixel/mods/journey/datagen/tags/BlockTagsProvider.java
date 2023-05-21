package cn.maxpixel.mods.journey.datagen.tags;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.tags.BlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends net.minecraftforge.common.data.BlockTagsProvider {
    public BlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, JourneyMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(BlockTags.WEAK).addTag(net.minecraft.tags.BlockTags.BUTTONS);
    }
}