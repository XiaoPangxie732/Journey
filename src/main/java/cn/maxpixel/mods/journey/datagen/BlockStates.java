package cn.maxpixel.mods.journey.datagen;

import cn.maxpixel.mods.journey.JourneyMod;
import cn.maxpixel.mods.journey.block.CopperWireBlock;
import cn.maxpixel.mods.journey.block.CreativeEngineBlock;
import cn.maxpixel.mods.journey.registry.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class BlockStates extends BlockStateProvider {
    public BlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, JourneyMod.MODID, exFileHelper);
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
        simpleBlockWithItem(BlockRegistry.CONTROLLER.get(), this::cubeAll);
        simpleBlockWithItem(BlockRegistry.COPPER_WIRE.get(), block -> models()
                .getBuilder(CopperWireBlock.NAME)
                .texture("particle", blockTexture(BlockRegistry.COPPER_WIRE.get())));
        simpleBlockWithItem(BlockRegistry.CREATIVE_ENGINE.get(), block -> models().cubeBottomTop(
                CreativeEngineBlock.NAME,
                blockTexture(BlockRegistry.CREATIVE_ENGINE.get()),
                extendBlock(BlockRegistry.CREATIVE_ENGINE.get(), "_bottom"),
                blockTexture(BlockRegistry.CREATIVE_ENGINE.get())
        ));
    }
}