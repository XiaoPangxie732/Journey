package cn.maxpixel.mods.journey.block.entity;

import cn.maxpixel.mods.journey.block.CopperWireBlock;
import cn.maxpixel.mods.journey.registries.BlockEntityRegistry;
import cn.maxpixel.mods.journey.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CopperWireBlockEntity extends BlockEntity {
    private static final String COUNT_KEY = "Count";
    private static final String ENERGY_KEY = "Energy";

    private short count;

    private final CopperWireEnergyStorage storage = new CopperWireEnergyStorage();
    private final LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> storage);

    public CopperWireBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.COPPER_WIRE.get(), pWorldPosition, pBlockState);
    }

    public short getCount() {
        return MathUtil.sclamp(count, CopperWireBlock.MIN_COUNT, CopperWireBlock.MAX_COUNT);
    }

    public void setCount(short count) {
        this.count = MathUtil.sclamp(count, CopperWireBlock.MIN_COUNT, CopperWireBlock.MAX_COUNT);
        setChanged();
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        setCount(pTag.getShort(COUNT_KEY));
        storage.setEnergyStored(pTag.getInt(ENERGY_KEY));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putShort(COUNT_KEY, count);
        pTag.putInt(ENERGY_KEY, storage.getEnergyStored());
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return CapabilityEnergy.ENERGY.orEmpty(cap, optional);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    private class CopperWireEnergyStorage implements IEnergyStorage {//TODO
        private int energyStored;

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {//TODO
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return energyStored;
        }

        private void setEnergyStored(int energy) {
            if (getMaxEnergyStored() - energy < 1000) {
                BlockPos pos = getBlockPos();
                getLevel().explode(null,
                        pos.getX() + .5d, pos.getY() + .5d, pos.getZ() + .5d,
                        6.5f, true,
                        Explosion.BlockInteraction.DESTROY
                );
            }
            this.energyStored = Mth.clamp(energy, 0, getMaxEnergyStored());
            setChanged();
        }

        @Override
        public int getMaxEnergyStored() {
            return 1000 * count;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}