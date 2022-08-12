package cn.maxpixel.mods.journey.datagen;

import cn.maxpixel.mods.journey.registries.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    private void simpleBlockWithItem(Block b, Function<Block, ModelFile> file) {
        ModelFile model = file.apply(b);
        simpleBlock(b, model);
        simpleBlockItem(b, model);
    }

    private ResourceLocation extendBlock(Block b, String suffix) {
        ResourceLocation rl = blockTexture(b);
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(BlockRegistry.CONTROLLER, this::cubeAll);
        simpleBlockWithItem(BlockRegistry.COPPER_WIRE, block -> models()
                .getBuilder(block.getRegistryName().getPath())
                .texture("particle", blockTexture(BlockRegistry.COPPER_WIRE)));
        simpleBlockWithItem(BlockRegistry.CREATIVE_ENGINE, block -> models().cubeBottomTop(
                BlockRegistry.CREATIVE_ENGINE.getRegistryName().getPath(),
                blockTexture(BlockRegistry.CREATIVE_ENGINE),
                extendBlock(BlockRegistry.CREATIVE_ENGINE, "_bottom"),
                blockTexture(BlockRegistry.CREATIVE_ENGINE)
        ));
    }
}