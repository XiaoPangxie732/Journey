package cn.maxpixel.mods.journey.datagen;

import cn.maxpixel.mods.journey.registries.BlockRegistry;
import net.minecraft.data.DataGenerator;
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

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(BlockRegistry.CONTROLLER.get(), this::cubeAll);
        simpleBlockWithItem(BlockRegistry.COPPER_WIRE.get(), block -> models()
                .getBuilder(block.getRegistryName().getPath())
                .texture("particle", blockTexture(BlockRegistry.COPPER_WIRE.get())));
    }
}