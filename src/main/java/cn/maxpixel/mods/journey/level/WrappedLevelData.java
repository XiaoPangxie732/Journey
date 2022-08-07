package cn.maxpixel.mods.journey.level;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;

public class WrappedLevelData implements WritableLevelData {
    private final LevelData wrapped;

    public WrappedLevelData(LevelData wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setXSpawn(int pXSpawn) {
    }

    @Override
    public void setYSpawn(int pYSpawn) {
    }

    @Override
    public void setZSpawn(int pZSpawn) {
    }

    @Override
    public void setSpawnAngle(float pSpawnAngle) {
    }

    @Override
    public int getXSpawn() {
        return wrapped.getXSpawn();
    }

    @Override
    public int getYSpawn() {
        return wrapped.getYSpawn();
    }

    @Override
    public int getZSpawn() {
        return wrapped.getZSpawn();
    }

    @Override
    public float getSpawnAngle() {
        return wrapped.getSpawnAngle();
    }

    @Override
    public long getGameTime() {
        return wrapped.getGameTime();
    }

    @Override
    public long getDayTime() {
        return wrapped.getDayTime();
    }

    @Override
    public boolean isThundering() {
        return wrapped.isThundering();
    }

    @Override
    public boolean isRaining() {
        return wrapped.isRaining();
    }

    @Override
    public void setRaining(boolean pRaining) {
    }

    @Override
    public boolean isHardcore() {
        return wrapped.isHardcore();
    }

    @Override
    public GameRules getGameRules() {
        return wrapped.getGameRules();
    }

    @Override
    public Difficulty getDifficulty() {
        return wrapped.getDifficulty();
    }

    @Override
    public boolean isDifficultyLocked() {
        return wrapped.isDifficultyLocked();
    }
}