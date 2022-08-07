package cn.maxpixel.mods.journey.tags;

import cn.maxpixel.mods.journey.util.Utils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTags {
    public static final TagKey<Block> WEAK = create("weak");// TODO: remove or keep?

    private static TagKey<Block> create(String name) {
        return net.minecraft.tags.BlockTags.create(Utils.newResourceLocation(name));
    }
}